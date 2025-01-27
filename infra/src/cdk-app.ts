import * as cdk from "aws-cdk-lib";
import * as constructs from "constructs";
import * as iam from "aws-cdk-lib/aws-iam";
import * as route53 from "aws-cdk-lib/aws-route53";
import * as ssm from "aws-cdk-lib/aws-ssm";
import * as ec2 from "aws-cdk-lib/aws-ec2";

import { getConfig, getEnvironment } from "./config";

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
    new AlarmStack(this, "AlarmStack", stackProps);
    new VpcStack(this, "VpcStack", stackProps);
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

class AlarmStack extends cdk.Stack {
  constructor(scope: constructs.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

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
}

const app = new CdkApp({});
app.synth();
