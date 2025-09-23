import * as cdk from "aws-cdk-lib";
import * as constructs from "constructs";
import * as iam from "aws-cdk-lib/aws-iam";
import * as path from "path";
import * as route53 from "aws-cdk-lib/aws-route53";
import * as route53_targets from "aws-cdk-lib/aws-route53-targets";
import * as ecr_assets from "aws-cdk-lib/aws-ecr-assets";
import * as ssm from "aws-cdk-lib/aws-ssm";
import * as secretsmanager from "aws-cdk-lib/aws-secretsmanager";
import * as lambda from "aws-cdk-lib/aws-lambda";
import * as logs from "aws-cdk-lib/aws-logs";
import * as s3 from "aws-cdk-lib/aws-s3";
import * as sns_subscriptions from "aws-cdk-lib/aws-sns-subscriptions";
import * as sns from "aws-cdk-lib/aws-sns";
import * as ecs from "aws-cdk-lib/aws-ecs";
import * as ec2 from "aws-cdk-lib/aws-ec2";
import * as rds from "aws-cdk-lib/aws-rds";
import * as elasticloadbalancingv2 from "aws-cdk-lib/aws-elasticloadbalancingv2";
import * as certificatemanager from "aws-cdk-lib/aws-certificatemanager";
import * as kms from "aws-cdk-lib/aws-kms";

import { getConfig, getEnvironment } from "./config";
import * as alarms from "./alarms";
import { DatabaseBackupToS3 } from "./DatabaseBackupToS3";
import { createHealthCheckStacks } from "./health-check";
import * as datantuonti from "./datantuonti";

class CdkApp extends cdk.App {
  constructor(props: cdk.AppProps) {
    super(props);
    const stackProps = {
      env: {
        account: process.env.CDK_DEPLOY_TARGET_ACCOUNT,
        region: process.env.CDK_DEPLOY_TARGET_REGION,
      },
    };
    const config = getConfig();

    const { hostedZone } = new DnsStack(this, "DnsStack", stackProps);
    const { alarmTopic, alarmsToSlackLambda } = new AlarmStack(
      this,
      "AlarmStack",
      stackProps
    );
    const { vpc, bastion } = new VpcStack(this, "VpcStack", stackProps);
    const { ecsCluster } = new EcsStack(this, "EcsStack", {
      ...stackProps,
      vpc,
    });
    const datantuontiExport = new datantuonti.ExportStack(
      this,
      "DatantuontiExport",
      stackProps
    );
    const { database, exportBucket } = new DatabaseStack(
      this,
      "DatabaseStack",
      {
        ...stackProps,
        vpc,
        bastion,
        ecsCluster,
        alarmTopic,
        datantuontiExportBucket: datantuontiExport.bucket,
      }
    );
    new ApplicationStack(this, "ApplicationStack", {
      ...stackProps,
      hostedZone,
      alarmTopic,
      vpc,
      ecsCluster,
      database,
      exportBucket,
      datantuontiExportBucket: datantuontiExport.bucket,
      datantuontiExportEncryptionKey: datantuontiExport.encryptionKey,
    });
    createHealthCheckStacks(this, alarmsToSlackLambda, [
      {
        name: "Koodisto",
        url: new URL(
          `https://${config.virkailijaHost}/koodisto-service/actuator/health`
        ),
      },
    ]);
  }
}

class ApplicationStack extends cdk.Stack {
  constructor(
    scope: constructs.Construct,
    id: string,
    props: cdk.StackProps & {
      hostedZone: route53.IHostedZone;
      alarmTopic: sns.ITopic;
      vpc: ec2.IVpc;
      ecsCluster: ecs.Cluster;
      database: rds.DatabaseCluster;
      exportBucket: s3.Bucket;
      datantuontiExportBucket: s3.Bucket;
      datantuontiExportEncryptionKey: kms.Key;
    }
  ) {
    super(scope, id, props);
    const {
      vpc,
      ecsCluster,
      database,
      exportBucket,
      datantuontiExportBucket,
      datantuontiExportEncryptionKey,
      hostedZone,
    } = props;
    const config = getConfig();

    const appPort = 8080;
    const albHostname = `koodisto.${config.zoneName}`;

    const logGroup = new logs.LogGroup(this, "AppLogGroup", {
      logGroupName: "Koodisto/koodisto",
      retention: logs.RetentionDays.INFINITE,
    });

    const dockerImage = new ecr_assets.DockerImageAsset(this, "AppImage", {
      directory: path.join(__dirname, "../../"),
      file: "Dockerfile",
      platform: ecr_assets.Platform.LINUX_ARM64,
      exclude: ["infra/cdk.out"],
    });

    const taskDefinition = new ecs.FargateTaskDefinition(
      this,
      "TaskDefinition",
      {
        cpu: 1024,
        memoryLimitMiB: 2048,
        runtimePlatform: {
          operatingSystemFamily: ecs.OperatingSystemFamily.LINUX,
          cpuArchitecture: ecs.CpuArchitecture.ARM64,
        },
      }
    );
    exportBucket.grantReadWrite(taskDefinition.taskRole);
    datantuontiExportBucket.grantReadWrite(taskDefinition.taskRole);
    datantuontiExportEncryptionKey.grantEncrypt(taskDefinition.taskRole);
    datantuonti
      .createS3ImporPolicyStatements(this)
      .forEach((statement) => taskDefinition.addToTaskRolePolicy(statement));

    const oauthProperties: ecs.ContainerDefinitionProps["environment"] =
      config.oauthServer
        ? {
            "#spring.security.oauth2.resourceserver.jwt.issuer-uri": `https://${config.oauthServer}/kayttooikeus-service`,
            "#spring.security.oauth2.resourceserver.jwt.jwk-set-uri": `https://${config.oauthServer}/kayttooikeus-service/oauth2/jwks`,
          }
        : {};

    const lampiProperties: Record<string, string> = config.lampiExport
      ? {
          "koodisto.tasks.export.enabled":
            config.lampiExport.enabled.toString(),
          "koodisto.tasks.export.bucket-name": exportBucket.bucketName,
          "koodisto.tasks.export.lampi-bucket-name":
            config.lampiExport.bucketName,
          "koodisto.tasks.export.lampi-role-arn":
            ssm.StringParameter.valueFromLookup(this, "LampiRoleArn"),
          "koodisto.tasks.export.lampi-external-id":
            ssm.StringParameter.valueFromLookup(this, "LampiExternalId"),
        }
      : {
          "koodisto.tasks.export.enabled": "false",
          "koodisto.tasks.export.bucket-name": exportBucket.bucketName,
        };

    taskDefinition.addContainer("AppContainer", {
      image: ecs.ContainerImage.fromDockerImageAsset(dockerImage),
      logging: ecs.LogDrivers.awsLogs({ logGroup, streamPrefix: "app" }),
      environment: {
        "spring.datasource.url": `jdbc:postgresql://${database.clusterEndpoint.hostname}:${database.clusterEndpoint.port.toString()}/koodisto`,
        "host.virkailija": config.virkailijaHost,
        "koodisto.tasks.datantuonti.export.enabled": `${config.datantuonti.export.enabled}`,
        "koodisto.tasks.datantuonti.export.bucket-name":
          props.datantuontiExportBucket.bucketName,
        "koodisto.tasks.datantuonti.export.encryption-key-arn":
          props.datantuontiExportEncryptionKey.keyArn,
        "koodisto.tasks.datantuonti.import.enabled": config.datantuonti.import
          ? `${config.datantuonti.import.enabled}`
          : "false",
        ...lampiProperties,
        ...oauthProperties,
        "koodisto.jwt-issuers": config.jwtIssuers,
      },
      secrets: {
        "koodisto.tasks.datantuonti.import.bucket-name":
          ecs.Secret.fromSsmParameter(
            ssm.StringParameter.fromStringParameterName(
              this,
              `ParamDatantuontiImportBucketName`,
              "koodisto.tasks.datantuonti.import.bucket-name"
            )
          ),
        "spring.datasource.username": ecs.Secret.fromSecretsManager(
          database.secret!,
          "username"
        ),
        "spring.datasource.password": ecs.Secret.fromSecretsManager(
          database.secret!,
          "password"
        ),
      },
      portMappings: [
        {
          name: "koodisto",
          containerPort: appPort,
          appProtocol: ecs.AppProtocol.http,
        },
      ],
    });

    if (config.lampiExport) {
      taskDefinition.addToTaskRolePolicy(
        new iam.PolicyStatement({
          actions: ["sts:AssumeRole"],
          resources: [
            ssm.StringParameter.valueFromLookup(this, "LampiRoleArn"),
          ],
        })
      );

      new alarms.ExpectedLogLineAlarm(this, "ExportTaskAlarm", {
        logGroup,
        alarmTopic: props.alarmTopic,
        metricNamespace: "Koodisto",
        name: "ExportTask",
        expectedLogLine: "Koodisto export task completed",
      });
    }

    new alarms.ExpectedLogLineAlarm(this, "DatantuontiExportTaskAlarm", {
      logGroup,
      alarmTopic: props.alarmTopic,
      metricNamespace: "Koodisto",
      name: "DatantuontiExportTask",
      expectedLogLine: "Koodisto datantuonti export task completed",
      period: cdk.Duration.hours(25),
      evaluationPeriods: 1,
    });

    if (config.datantuonti.import) {
      new alarms.ExpectedLogLineAlarm(this, "DatantuontiImportTaskAlarm", {
        logGroup,
        alarmTopic: props.alarmTopic,
        metricNamespace: "Koodisto",
        name: "DatantuontiImportTask",
        expectedLogLine: "Koodisto datantuonti import task completed",
        period: cdk.Duration.hours(25),
        evaluationPeriods: 1,
      });
    }

    const service = new ecs.FargateService(this, "Service", {
      cluster: ecsCluster,
      taskDefinition,
      desiredCount: config.minCapacity,
      minHealthyPercent: 100,
      maxHealthyPercent: 200,
      vpcSubnets: { subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS },
      healthCheckGracePeriod: cdk.Duration.minutes(2),
      circuitBreaker: { enable: true, rollback: true },
    });
    service.connections.allowToDefaultPort(database);

    if (config.minCapacity !== config.maxCapacity) {
      const scaling = service.autoScaleTaskCount({
        minCapacity: config.minCapacity,
        maxCapacity: config.maxCapacity,
      });
      scaling.scaleOnMetric("ServiceCpuScaling", {
        metric: service.metricCpuUtilization(),
        scalingSteps: [
          { upper: 15, change: -1 },
          { lower: 50, change: +1 },
          { lower: 65, change: +2 },
          { lower: 80, change: +3 },
        ],
      });
    }

    const alb = new elasticloadbalancingv2.ApplicationLoadBalancer(
      this,
      "LoadBalancer",
      { vpc, internetFacing: true }
    );

    new route53.ARecord(this, "ALBARecord", {
      zone: hostedZone,
      target: route53.RecordTarget.fromAlias(
        new route53_targets.LoadBalancerTarget(alb)
      ),
      recordName: albHostname,
    });

    const albCertificate = new certificatemanager.Certificate(
      this,
      "AlbCertificate",
      {
        domainName: albHostname,
        validation:
          certificatemanager.CertificateValidation.fromDns(hostedZone),
      }
    );

    const listener = alb.addListener("Listener", {
      protocol: elasticloadbalancingv2.ApplicationProtocol.HTTPS,
      port: 443,
      certificates: [albCertificate],
      open: true,
    });
    listener.addTargets("ServiceTarget", {
      port: appPort,
      targets: [service],
      healthCheck: {
        enabled: true,
        path: "/koodisto-service/actuator/health",
        interval: cdk.Duration.seconds(10),
        port: appPort.toString(),
      },
    });
  }
}

export class DnsStack extends cdk.Stack {
  readonly hostedZone: route53.IHostedZone;
  constructor(scope: constructs.Construct, id: string, props: cdk.StackProps) {
    super(scope, id, props);

    const config = getConfig();

    this.hostedZone = new route53.HostedZone(this, "HostedZone", {
      zoneName: config.zoneName,
    });
  }
}

class VpcStack extends cdk.Stack {
  readonly vpc: ec2.IVpc;
  readonly bastion: ec2.BastionHostLinux;

  constructor(scope: constructs.Construct, id: string, props: cdk.StackProps) {
    super(scope, id, props);

    const config = getConfig();
    this.vpc = this.createVpc(config.vpcCidr);
    this.bastion = this.createBastion();
  }

  private createVpc(cidr: string) {
    const vpc = new ec2.Vpc(this, "Vpc", {
      ipAddresses: ec2.IpAddresses.cidr(cidr),
      maxAzs: 2,
      natGateways: 2,
      subnetConfiguration: [
        {
          cidrMask: 22,
          name: "Ingress",
          subnetType: ec2.SubnetType.PUBLIC,
        },
        {
          cidrMask: 22,
          name: "Application",
          subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS,
        },
        {
          cidrMask: 22,
          name: "Database",
          subnetType: ec2.SubnetType.PRIVATE_ISOLATED,
        },
      ],
    });
    vpc.addGatewayEndpoint("S3Endpoint", {
      service: ec2.GatewayVpcEndpointAwsService.S3,
    });
    return vpc;
  }

  private createBastion(): ec2.BastionHostLinux {
    return new ec2.BastionHostLinux(this, "Bastion", {
      vpc: this.vpc,
      instanceName: "Bastion",
    });
  }
}

class EcsStack extends cdk.Stack {
  readonly ecsCluster: ecs.Cluster;

  constructor(
    scope: constructs.Construct,
    id: string,
    props: cdk.StackProps & {
      vpc: ec2.IVpc;
    }
  ) {
    super(scope, id, props);
    const { vpc } = props;

    this.ecsCluster = new ecs.Cluster(this, "Cluster", {
      vpc,
      clusterName: "Cluster",
    });
  }
}

class AlarmStack extends cdk.Stack {
  readonly alarmTopic: sns.ITopic;
  readonly alarmsToSlackLambda: lambda.IFunction;

  constructor(scope: constructs.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);
    this.alarmsToSlackLambda = this.createAlarmsToSlackLambda();
    this.alarmTopic = this.createAlarmTopic();

    this.alarmTopic.addSubscription(
      new sns_subscriptions.LambdaSubscription(this.alarmsToSlackLambda)
    );

    const radiatorAccountId = "905418271050";
    const radiatorReader = new iam.Role(this, "RadiatorReaderRole", {
      assumedBy: new iam.AccountPrincipal(radiatorAccountId),
      roleName: "RadiatorReader",
    });
    radiatorReader.addToPolicy(
      new iam.PolicyStatement({
        effect: iam.Effect.ALLOW,
        actions: ["cloudwatch:DescribeAlarms"],
        resources: ["*"],
      })
    );
  }

  createAlarmTopic(): sns.ITopic {
    return new sns.Topic(this, "AlarmTopic", {
      displayName: "alarm",
    });
  }

  createAlarmsToSlackLambda() {
    const alarmsToSlack = new lambda.Function(this, "AlarmsToSlack", {
      functionName: "alarms-to-slack",
      code: lambda.Code.fromAsset("../alarms-to-slack"),
      handler: "alarms-to-slack.handler",
      runtime: lambda.Runtime.NODEJS_20_X,
      architecture: lambda.Architecture.ARM_64,
      timeout: cdk.Duration.seconds(30),
    });

    // https://docs.aws.amazon.com/secretsmanager/latest/userguide/retrieving-secrets_lambda.html
    const parametersAndSecretsExtension =
      lambda.LayerVersion.fromLayerVersionArn(
        this,
        "ParametersAndSecretsLambdaExtension",
        "arn:aws:lambda:eu-west-1:015030872274:layer:AWS-Parameters-and-Secrets-Lambda-Extension-Arm64:11"
      );

    alarmsToSlack.addLayers(parametersAndSecretsExtension);
    secretsmanager.Secret.fromSecretNameV2(
      this,
      "slack-webhook",
      "slack-webhook"
    ).grantRead(alarmsToSlack);

    return alarmsToSlack;
  }
}

class DatabaseStack extends cdk.Stack {
  readonly database: rds.DatabaseCluster;
  readonly exportBucket: s3.Bucket;

  constructor(
    scope: constructs.Construct,
    id: string,
    props: cdk.StackProps & {
      vpc: ec2.IVpc;
      bastion: ec2.BastionHostLinux;
      ecsCluster: ecs.Cluster;
      alarmTopic: sns.ITopic;
      datantuontiExportBucket: s3.Bucket;
    }
  ) {
    super(scope, id, props);

    const { vpc, bastion, ecsCluster, alarmTopic, datantuontiExportBucket } =
      props;
    this.exportBucket = new s3.Bucket(this, "ExportBucket", {});

    const datantuontiImportRole = new iam.Role(this, "DatantuontiImport", {
      assumedBy: new iam.ServicePrincipal("rds.amazonaws.com"),
    });
    datantuonti
      .createS3ImporPolicyStatements(this)
      .forEach((statement) => datantuontiImportRole.addToPolicy(statement));

    this.database = new rds.DatabaseCluster(this, "Database", {
      vpc,
      vpcSubnets: { subnetType: ec2.SubnetType.PRIVATE_ISOLATED },
      defaultDatabaseName: "koodisto",
      engine: rds.DatabaseClusterEngine.auroraPostgres({
        version: rds.AuroraPostgresEngineVersion.VER_15_7,
      }),
      credentials: rds.Credentials.fromGeneratedSecret("koodisto", {
        secretName: "KoodistoDatabaseSecret",
      }),
      storageType: rds.DBClusterStorageType.AURORA,
      writer: rds.ClusterInstance.provisioned("writer", {
        enablePerformanceInsights: true,
        instanceType: ec2.InstanceType.of(
          ec2.InstanceClass.T4G,
          ec2.InstanceSize.MEDIUM
        ),
      }),
      storageEncrypted: true,
      readers: [],
      s3ImportRole: datantuontiImportRole,
      s3ExportBuckets: [this.exportBucket, datantuontiExportBucket],
    });
    this.database.connections.allowDefaultPortFrom(bastion);
    const backup = new DatabaseBackupToS3(this, "DatabaseBackup", {
      ecsCluster: ecsCluster,
      dbCluster: this.database,
      dbName: "koodisto",
      alarmTopic,
    });
    this.database.connections.allowDefaultPortFrom(backup);
  }
}

const app = new CdkApp({});
app.synth();
