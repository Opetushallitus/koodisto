package fi.vm.sade.koodisto.export;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.DownloadFileRequest;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;

import java.io.File;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExportService {
    private final JdbcTemplate jdbcTemplate;
    private final S3AsyncClient opintopolkuS3Client;
    private final S3AsyncClient lampiS3Client;

    @Value("${koodisto.tasks.export.bucket-name}")
    private String bucketName;
    @Value("${koodisto.tasks.export.lampi-bucket-name}")
    private String lampiBucketName;

    @Transactional
    public void createSchema() {
        jdbcTemplate.execute("DROP SCHEMA IF EXISTS exportnew CASCADE");
        jdbcTemplate.execute("CREATE SCHEMA exportnew");
        jdbcTemplate.execute(
                "CREATE TABLE exportnew.koodi AS " +
                        "SELECT k1.koodistouri," +
                        "k0.koodiuri," +
                        "k2.koodiarvo," +
                        "(SELECT nimi from koodimetadata where kieli = 'FI' and koodiversio_id = k2.id) koodinimi_fi," +
                        "(SELECT nimi from koodimetadata where kieli = 'SV' and koodiversio_id = k2.id) koodinimi_sv " +
                        "FROM koodi k0 " +
                        "JOIN koodisto k1 on k1.id = k0.koodisto_id " +
                        "JOIN koodiversio k2 ON k0.id = k2.koodi_id " +
                        "WHERE now() BETWEEN k2.voimassaalkupvm AND coalesce(k2.voimassaloppupvm, now())");
        jdbcTemplate.execute("DROP SCHEMA IF EXISTS export CASCADE");
        jdbcTemplate.execute("ALTER SCHEMA exportnew RENAME TO export");
    }

    public void exportSchema() throws IOException {
        exportTableToS3();
        copyToLampi();
    }

    void exportTableToS3() {
        var objectKey = "fulldump/v2/koodi.csv";
        log.info("Exporting table to S3: {}/{}", bucketName, objectKey);
        var sql = """
                SELECT * FROM aws_s3.query_export_to_s3(
                    'SELECT koodistouri, koodiuri, koodiarvo, koodinimi_fi, koodinimi_sv FROM export.koodi',
                    aws_commons.create_s3_uri(?, ?, ?)
                )
                """;
        jdbcTemplate.update(sql, bucketName, objectKey, OpintopolkuAwsClients.REGION.id());
    }

    void copyToLampi() throws IOException {
        var temporaryFile = File.createTempFile("koodi", "csv");
        try {
            log.info("Downloading file from S3: {}/fulldump/v2/koodi.csv", bucketName);
            try (var downloader = S3TransferManager.builder().s3Client(opintopolkuS3Client).build()) {
                var fileDownload = downloader.downloadFile(DownloadFileRequest.builder()
                        .getObjectRequest(b -> b.bucket(bucketName).key("fulldump/v2/koodi.csv"))
                        .destination(temporaryFile)
                        .build());
                fileDownload.completionFuture().join();
            }


            log.info("Uploading file to S3: {}/fulldump/v2/koodi.csv", lampiBucketName);
            try (var uploader = S3TransferManager.builder().s3Client(lampiS3Client).build()) {
                var fileUpload = uploader.uploadFile(UploadFileRequest.builder()
                        .putObjectRequest(b -> b.bucket(lampiBucketName).key("fulldump/v2/koodi.csv"))
                        .source(temporaryFile)
                        .build());
                fileUpload.completionFuture().join();
            }
        } finally {
            temporaryFile.delete();
        }
    }
}