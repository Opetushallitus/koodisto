package fi.vm.sade.koodisto.export;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class LampiConfiguration {
    private static final Region REGION = Region.EU_WEST_1;

    @Value("${koodisto.tasks.export.lampi-role-arn}")
    private String lampiRoleArn;
    @Value("${koodisto.tasks.export.lampi-external-id}")
    private String lampiExternalId;

    @Bean
    public StsAssumeRoleCredentialsProvider lampiCredentialsProvider(AwsCredentialsProvider opinpolkuCredentialsProvider) {
        var stsClient = StsClient.builder()
                .credentialsProvider(opinpolkuCredentialsProvider)
                .region(REGION)
                .build();

        return StsAssumeRoleCredentialsProvider.builder()
                .stsClient(stsClient)
                .refreshRequest(() -> AssumeRoleRequest.builder()
                        .roleArn(lampiRoleArn)
                        .externalId(lampiExternalId)
                        .roleSessionName("koodisto-lampi-export")
                        .build())
                .build();
    }

    @Bean
    public S3AsyncClient lampiS3Client(StsAssumeRoleCredentialsProvider lampiCredentialsProvider) {
        return S3AsyncClient.crtBuilder()
                .credentialsProvider(lampiCredentialsProvider)
                .region(REGION)
                .build();
    }
}
