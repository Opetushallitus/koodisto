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
import java.nio.file.Files;

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
        jdbcTemplate.execute("""
                CREATE TABLE exportnew.koodi AS
                SELECT
                    koodisto.koodistouri AS koodistouri,
                    koodi.koodiuri AS koodiuri,
                    koodiversio.koodiarvo AS koodiarvo,
                    koodiversio.versio AS koodiversio,
                    koodiversio.tila AS tila,
                    koodiversio.voimassaalkupvm AS voimassaalkupvm,
                    koodiversio.voimassaloppupvm AS voimassaloppupvm,
                    metadata_fi.nimi AS koodinimi_fi,
                    metadata_sv.nimi AS koodinimi_sv,
                    metadata_en.nimi AS koodinimi_en,
                    metadata_fi.kuvaus AS koodikuvaus_fi,
                    metadata_sv.kuvaus AS koodikuvaus_sv,
                    metadata_en.kuvaus AS koodikuvaus_en,
                    koodiversio.luotu AS koodiversiocreated_at,
                    koodiversio.paivityspvm AS koodiversioupdated_at
                FROM koodi
                JOIN koodisto ON koodi.koodisto_id = koodisto.id
                JOIN koodiversio ON koodi.id = koodiversio.koodi_id
                LEFT JOIN public.koodimetadata metadata_fi ON koodiversio.id = metadata_fi.koodiversio_id AND metadata_fi.kieli = 'FI'
                LEFT JOIN public.koodimetadata metadata_sv ON koodiversio.id = metadata_sv.koodiversio_id AND metadata_sv.kieli = 'SV'
                LEFT JOIN public.koodimetadata metadata_en ON koodiversio.id = metadata_en.koodiversio_id AND metadata_en.kieli = 'EN'
                """);
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
                    'SELECT koodistouri, koodiuri, koodiarvo, koodiversio, tila, voimassaalkupvm, voimassaloppupvm, koodinimi_fi, koodinimi_sv, koodinimi_en, koodikuvaus_fi, koodikuvaus_sv, koodikuvaus_en, koodiversiocreated_at, koodiversio_updated_at FROM export.koodi',
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
            Files.deleteIfExists(temporaryFile.toPath());
        }
    }
}