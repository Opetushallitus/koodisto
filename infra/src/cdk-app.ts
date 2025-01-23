import * as cdk from "aws-cdk-lib";
import * as constructs from "constructs";
import * as iam from "aws-cdk-lib/aws-iam";

class CdkApp extends cdk.App {
  constructor(props: cdk.AppProps) {
    super(props);
    const stackProps = {
      env: {
        account: process.env.CDK_DEPLOY_TARGET_ACCOUNT,
        region: process.env.CDK_DEPLOY_TARGET_REGION,
      },
    };

    new AlarmStack(this, "AlarmStack", stackProps);
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
      }),
    );
  }
}

const app = new CdkApp({});
app.synth();
