package fi.vm.sade.koodisto.export;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class OpintopolkuAwsClients {
    public static final Region REGION = Region.EU_WEST_1;

    @Bean
    public AwsCredentialsProvider opintopolkuCredentialsProvider() {
        return DefaultCredentialsProvider.create();
    }

    @Bean
    public S3AsyncClient opintopolkuS3Client(AwsCredentialsProvider opintopolkuCredentialsProvider) {
        return S3AsyncClient.builder()
                .credentialsProvider(opintopolkuCredentialsProvider)
                .region(REGION)
                .build();
    }
}
