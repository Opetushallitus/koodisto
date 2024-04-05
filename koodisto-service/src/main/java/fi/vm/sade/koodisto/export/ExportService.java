package fi.vm.sade.koodisto.export;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.DownloadFileRequest;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExportService {
    private static final String S3_PREFIX = "fulldump/koodisto/v2";
    private final ObjectMapper objectMapper = new ObjectMapper();
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
        jdbcTemplate.execute("""
                CREATE TABLE exportnew.relaatio AS
                SELECT
                    ylakoodi.koodiuri ylakoodiuri,
                    ylakoodiversio.versio ylakoodiversio,
                    suhteentyyppi relaatiotyyppi,
                    alakoodi.koodiuri alakoodiuri,
                    alakoodiversio.versio alakoodiversio,
                    koodinsuhde.versio relaatioversio
                FROM koodinsuhde
                LEFT JOIN koodiversio ylakoodiversio ON ylakoodiversio.id = ylakoodiversio_id
                LEFT JOIN koodi ylakoodi ON ylakoodiversio.koodi_id = ylakoodi.id
                LEFT JOIN koodiversio alakoodiversio ON alakoodiversio.id = alakoodiversio_id
                LEFT JOIN koodi alakoodi ON alakoodiversio.koodi_id = alakoodi.id
                JOIN koodisto ON ylakoodi.koodisto_id = koodisto.id
                """);
        jdbcTemplate.execute("DROP SCHEMA IF EXISTS export CASCADE");
        jdbcTemplate.execute("ALTER SCHEMA exportnew RENAME TO export");
    }

    public void exportSchema() throws IOException {
        exportTableToS3();
        copyToLampi();
    }

    void exportTableToS3() {
        exportQueryToS3(S3_PREFIX + "/koodi.csv", "SELECT koodistouri, koodiuri, koodiarvo, koodiversio, tila, voimassaalkupvm, voimassaloppupvm, koodinimi_fi, koodinimi_sv, koodinimi_en, koodikuvaus_fi, koodikuvaus_sv, koodikuvaus_en, koodiversiocreated_at, koodiversioupdated_at FROM export.koodi");
        exportQueryToS3(S3_PREFIX + "/relaatio.csv", "SELECT ylakoodiuri, ylakoodiversio, relaatiotyyppi, alakoodiuri, alakoodiversio, relaatioversio FROM export.relaatio");
    }

    void exportQueryToS3(String objectKey, String query) {
        log.info("Exporting table to S3: {}/{}", bucketName, objectKey);
        var sql = "SELECT rows_uploaded FROM aws_s3.query_export_to_s3(?, aws_commons.create_s3_uri(?, ?, ?), options := 'FORMAT CSV, HEADER TRUE')";
        var rowsUploaded = jdbcTemplate.queryForObject(sql, Long.class, query, bucketName, objectKey, OpintopolkuAwsClients.REGION.id());
        log.info("Exported {} rows to S3 object {}", rowsUploaded, objectKey);
    }

    void copyToLampi() throws IOException {
        var koodiversionId = copyFileToLampi(S3_PREFIX + "/koodi.csv");
        log.info("Wrote koodis to Lampi with version id {}", koodiversionId);
        var relaatioVersionId = copyFileToLampi(S3_PREFIX + "/relaatio.csv");
        log.info("Wrote relaatiot to Lampi with version id {}", relaatioVersionId);
        writeManifest(new ExportManifest(List.of(koodiversionId, relaatioVersionId)));
    }

    private void writeManifest(ExportManifest manifest) throws JsonProcessingException {
        var manifestJson = objectMapper.writeValueAsString(manifest);
        var response = lampiS3Client.putObject(
                b -> b.bucket(lampiBucketName).key(S3_PREFIX + "/manifest.json"),
                AsyncRequestBody.fromString(manifestJson)
        ).join();
        log.info("Wrote manifest to S3: {}", response);
    }

    private ExportManifest.ExportFileDetails copyFileToLampi(String objectKey) throws IOException {
        var temporaryFile = File.createTempFile("export", "csv");
        try {
            log.info("Downloading file from S3: {}/{}", bucketName, objectKey);
            try (var downloader = S3TransferManager.builder().s3Client(opintopolkuS3Client).build()) {
                var fileDownload = downloader.downloadFile(DownloadFileRequest.builder()
                        .getObjectRequest(b -> b.bucket(bucketName).key(objectKey))
                        .destination(temporaryFile)
                        .build());
                fileDownload.completionFuture().join();
            }


            log.info("Uploading file to S3: {}/{}", lampiBucketName, objectKey);
            try (var uploader = S3TransferManager.builder().s3Client(lampiS3Client).build()) {
                var fileUpload = uploader.uploadFile(UploadFileRequest.builder()
                        .putObjectRequest(b -> b.bucket(lampiBucketName).key(objectKey))
                        .source(temporaryFile)
                        .build());
                var result = fileUpload.completionFuture().join();
                var objectVersion = result.response().versionId();
                return new ExportManifest.ExportFileDetails(objectKey, objectVersion);
            }
        } finally {
            Files.deleteIfExists(temporaryFile.toPath());
        }
    }
}