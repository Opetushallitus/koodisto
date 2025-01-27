import * as cdk from "aws-cdk-lib";
import * as constructs from "constructs";
import * as iam from "aws-cdk-lib/aws-iam";
import * as route53 from "aws-cdk-lib/aws-route53";
import * as ssm from "aws-cdk-lib/aws-ssm";
import * as secretsmanager from "aws-cdk-lib/aws-secretsmanager";
import * as lambda from "aws-cdk-lib/aws-lambda";
import * as sns_subscriptions from "aws-cdk-lib/aws-sns-subscriptions";
import * as sns from "aws-cdk-lib/aws-sns";
import * as ecs from "aws-cdk-lib/aws-ecs";
import * as ec2 from "aws-cdk-lib/aws-ec2";
import * as rds from "aws-cdk-lib/aws-rds";

import { getConfig, getEnvironment } from "./config";
import { DatabaseBackupToS3 } from "./DatabaseBackupToS3";

class CdkApp extends cdk.App {
  constructor(props: cdk.AppProps) {
    super(props);
    const stackProps = {
      env: {
        account: process.env.CDK_DEPLOY_TARGET_ACCOUNT,
        region: process.env.CDK_DEPLOY_TARGET_REGION,
      },
    };

    const { hostedZone } = new DnsStack(this, "DnsStack", stackProps);
    const { alarmTopic } = new AlarmStack(this, "AlarmStack", stackProps);
    const { vpc, bastion } = new VpcStack(this, "VpcStack", stackProps);
    const { ecsCluster } = new EcsStack(this, "EcsStack", {
      ...stackProps,
      vpc,
    });
    new DatabaseStack(this, "DatabaseStack", {
      ...stackProps,
      vpc,
      bastion,
      ecsCluster,
      alarmTopic,
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

  constructor(scope: constructs.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);
    const alarmsToSlackLambda = this.createAlarmsToSlackLambda();
    this.alarmTopic = this.createAlarmTopic();

    this.alarmTopic.addSubscription(
      new sns_subscriptions.LambdaSubscription(alarmsToSlackLambda)
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
  constructor(
    scope: constructs.Construct,
    id: string,
    props: cdk.StackProps & {
      vpc: ec2.IVpc;
      bastion: ec2.BastionHostLinux;
      ecsCluster: ecs.Cluster;
      alarmTopic: sns.ITopic;
    }
  ) {
    super(scope, id, props);

    const { vpc, bastion, ecsCluster, alarmTopic } = props;
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
    });
    this.database.connections.allowDefaultPortFrom(bastion);
    new DatabaseBackupToS3(this, "DatabaseBackup", {
      ecsCluster: ecsCluster,
      dbCluster: this.database,
      dbName: "koodisto",
      alarmTopic,
    });
  }
}

const app = new CdkApp({});
app.synth();
