import * as cdk from "aws-cdk-lib";
import * as kms from "aws-cdk-lib/aws-kms";
import * as ssm from "aws-cdk-lib/aws-ssm";
import * as iam from "aws-cdk-lib/aws-iam";
import * as s3 from "aws-cdk-lib/aws-s3";
import * as constructs from "constructs";

export class ExportStack extends cdk.Stack {
    readonly bucket: s3.Bucket;
    readonly encryptionKey: kms.Key;

    constructor(scope: constructs.Construct, id: string, props: cdk.StackProps) {
        super(scope, id, props);

        const targetAccountPrincipal = this.createTargetAccountPrincipal();
        this.bucket = this.createExportBucket(targetAccountPrincipal);
        this.encryptionKey = this.createEncryptionKey(targetAccountPrincipal);
    }

    private createEncryptionKey(targetAccountPrincipal: iam.AccountPrincipal) {
        const key = new kms.Key(this, "S3EncryptionKey", {
            enableKeyRotation: true,
        });

        key.grantDecrypt(targetAccountPrincipal);

        return key;
    }

    private createTargetAccountPrincipal() {
        const targetAccountId = ssm.StringParameter.valueFromLookup(
            this,
            "koodisto.tasks.datantuonti.export.role.target-account-id"
        );

        return new iam.AccountPrincipal(targetAccountId);
    }

    private createExportBucket(targetAccountPrincipal: iam.AccountPrincipal) {
        const bucket = new s3.Bucket(this, "ExportBucket");

        bucket.addLifecycleRule({
            id: "DeleteDatantuontiObjectsAfterSevenDays",
            enabled: true,
            expiration: cdk.Duration.days(7),
            prefix: "koodisto/v1/csv/",
        });
        bucket.grantRead(targetAccountPrincipal);

        return bucket;
    }
}

export function createS3ImporPolicyStatements(scope: constructs.Construct) {
    const importBucketName = ssm.StringParameter.valueFromLookup(
        scope,
        "koodisto.tasks.datantuonti.import.bucket-name"
    );

    const decryptionKeyArn = ssm.StringParameter.valueFromLookup(
        scope,
        "koodisto.tasks.datantuonti.import.decryption-key-arn"
    );

    return [
        new iam.PolicyStatement({
            actions: ["s3:GetObject", "s3:ListBucket"],
            resources: [
                `arn:aws:s3:::${importBucketName}`,
                `arn:aws:s3:::${importBucketName}/*`,
            ],
        }),
        new iam.PolicyStatement({
            actions: ["kms:Decrypt"],
            resources: [decryptionKeyArn],
        })
    ];
}