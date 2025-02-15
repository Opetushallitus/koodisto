package fi.vm.sade.koodisto.datantuonti;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;

@Slf4j
@Service
public class DatantuontiExportService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private S3AsyncClient opintopolkuS3Client;
    @Value("${koodisto.tasks.datantuonti.export.bucket-name}")
    private String bucketName;
    @Value("${koodisto.tasks.datantuonti.export.encryption-key-arn}")
    private String encryptionKeyArn;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new Jdk8Module());

    private static final String V1_PREFIX = "koodisto/v1";

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String createSchemaAndReturnTransactionTimestampFromEpoch() {
        jdbcTemplate.execute("DROP SCHEMA IF EXISTS datantuonti_export_new CASCADE");
        jdbcTemplate.execute("CREATE SCHEMA datantuonti_export_new");
        jdbcTemplate.execute("CREATE TABLE datantuonti_export_new.koodi AS SELECT * FROM public.koodi");
        jdbcTemplate.execute("CREATE TABLE datantuonti_export_new.koodimetadata AS SELECT * FROM public.koodimetadata");
        jdbcTemplate.execute("CREATE TABLE datantuonti_export_new.koodinsuhde AS SELECT * FROM public.koodinsuhde");
        jdbcTemplate.execute("CREATE TABLE datantuonti_export_new.koodisto AS SELECT * FROM public.koodisto");
        jdbcTemplate.execute("CREATE TABLE datantuonti_export_new.koodistometadata AS SELECT * FROM public.koodistometadata");
        jdbcTemplate.execute("CREATE TABLE datantuonti_export_new.koodistonsuhde AS SELECT * FROM public.koodistonsuhde");
        jdbcTemplate.execute("CREATE TABLE datantuonti_export_new.koodistoryhma AS SELECT * FROM public.koodistoryhma");
        jdbcTemplate.execute("CREATE TABLE datantuonti_export_new.koodistoryhma_koodisto AS SELECT * FROM public.koodistoryhma_koodisto");
        jdbcTemplate.execute("CREATE TABLE datantuonti_export_new.koodistoryhmametadata AS SELECT * FROM public.koodistoryhmametadata");
        jdbcTemplate.execute("CREATE TABLE datantuonti_export_new.koodistoversio AS SELECT * FROM public.koodistoversio");
        jdbcTemplate.execute("CREATE TABLE datantuonti_export_new.koodistoversio_koodiversio AS SELECT * FROM public.koodistoversio_koodiversio");
        jdbcTemplate.execute("CREATE TABLE datantuonti_export_new.koodiversio AS SELECT * FROM public.koodiversio");
        jdbcTemplate.execute("CREATE TABLE datantuonti_export_new.hibernate_sequence AS SELECT last_value FROM public.hibernate_sequence");
        jdbcTemplate.execute("DROP SCHEMA IF EXISTS datantuonti_export CASCADE");
        jdbcTemplate.execute("ALTER SCHEMA datantuonti_export_new RENAME TO datantuonti_export");

        return jdbcTemplate.queryForObject("SELECT extract(epoch from transaction_timestamp())", String.class);
    }

    public void generateExportFiles(String timestamp) throws JsonProcessingException {
        var koodiObjectKey = writeTableToS3(timestamp, "koodi", "SELECT * FROM datantuonti_export.koodi");
        var koodimetadataObjectKey = writeTableToS3(timestamp, "koodimetadata", "SELECT * FROM datantuonti_export.koodimetadata");
        var koodinsuhdeObjectKey = writeTableToS3(timestamp, "koodinsuhde", "SELECT * FROM datantuonti_export.koodinsuhde");
        var koodistoObjectKey = writeTableToS3(timestamp, "koodisto", "SELECT * FROM datantuonti_export.koodisto");
        var koodistometadataObjectKey = writeTableToS3(timestamp, "koodistometadata", "SELECT * FROM datantuonti_export.koodistometadata");
        var koodistonsuhdeObjectKey = writeTableToS3(timestamp, "koodistonsuhde", "SELECT * FROM datantuonti_export.koodistonsuhde");
        var koodistoryhmaObjectKey = writeTableToS3(timestamp, "koodistoryhma", "SELECT * FROM datantuonti_export.koodistoryhma");
        var koodistoryhma_koodistoObjectKey = writeTableToS3(timestamp, "koodistoryhma_koodisto", "SELECT * FROM datantuonti_export.koodistoryhma_koodisto");
        var koodistoryhmametadataObjectKey = writeTableToS3(timestamp, "koodistoryhmametadata", "SELECT * FROM datantuonti_export.koodistoryhmametadata");
        var koodistoversioObjectKey = writeTableToS3(timestamp, "koodistoversio", "SELECT * FROM datantuonti_export.koodistoversio");
        var koodistoversio_koodiversioObjectKey = writeTableToS3(timestamp, "koodistoversio_koodiversio", "SELECT * FROM datantuonti_export.koodistoversio_koodiversio");
        var koodiversioObjectKey = writeTableToS3(timestamp, "koodiversio", "SELECT * FROM datantuonti_export.koodiversio");
        var hibernate_sequenceObjectKey = writeTableToS3(timestamp, "hibernate_sequence", "SELECT * FROM datantuonti_export.hibernate_sequence");
        writeManifest(
                koodiObjectKey,
                koodimetadataObjectKey,
                koodinsuhdeObjectKey,
                koodistoObjectKey,
                koodistometadataObjectKey,
                koodistonsuhdeObjectKey,
                koodistoryhmaObjectKey,
                koodistoryhma_koodistoObjectKey,
                koodistoryhmametadataObjectKey,
                koodistoversioObjectKey,
                koodistoversio_koodiversioObjectKey,
                koodiversioObjectKey,
                hibernate_sequenceObjectKey
        );
    }

    private void writeManifest(
            String koodiObjectKey,
            String koodimetadataObjectKey,
            String koodinsuhdeObjectKey,
            String koodistoObjectKey,
            String koodistometadataObjectKey,
            String koodistonsuhdeObjectKey,
            String koodistoryhmaObjectKey,
            String koodistoryhma_koodistoObjectKey,
            String koodistoryhmametadataObjectKey,
            String koodistoversioObjectKey,
            String koodistoversio_koodiversioObjectKey,
            String koodiversioObjectKey,
            String hibernate_sequenceObjectKey) throws JsonProcessingException {
        var objectKey = V1_PREFIX + "/manifest.json";
        var manifest = new DatantuontiManifest(
                koodiObjectKey,
                koodimetadataObjectKey,
                koodinsuhdeObjectKey,
                koodistoObjectKey,
                koodistometadataObjectKey,
                koodistonsuhdeObjectKey,
                koodistoryhmaObjectKey,
                koodistoryhma_koodistoObjectKey,
                koodistoryhmametadataObjectKey,
                koodistoversioObjectKey,
                koodistoversio_koodiversioObjectKey,
                koodiversioObjectKey,
                hibernate_sequenceObjectKey
        );
        log.info("Writing manifest file {}/{}: {}", bucketName, objectKey, manifest);
        var manifestJson = objectMapper.writeValueAsString(manifest);
        var response = opintopolkuS3Client.putObject(
                b -> b.bucket(bucketName).key(objectKey),
                AsyncRequestBody.fromString(manifestJson)
        ).join();
        log.info("Wrote manifest to S3: {}", response);
    }

    private String writeTableToS3(String timestamp, String table, String query) {
        var objectKey = getObjectKey(timestamp, table);
        exportQueryToS3(objectKey, query);
        reEncryptFile(objectKey);

        return objectKey;
    }

    private String getObjectKey(String timestamp, String table) {
        return V1_PREFIX + "/csv/" + table + "-" + timestamp + ".csv";
    }

    private void exportQueryToS3(String objectKey, String query) {
        log.info("Exporting table to S3: {}/{}", bucketName, objectKey);
        var sql = "SELECT rows_uploaded FROM aws_s3.query_export_to_s3(?, aws_commons.create_s3_uri(?, ?, ?), options := 'FORMAT CSV, HEADER TRUE')";
        var rowsUploaded = jdbcTemplate.queryForObject(sql, Long.class, query, bucketName, objectKey, Region.EU_WEST_1.id());
        log.info("Exported {} rows to S3 object {}", rowsUploaded, objectKey);
    }

    private void reEncryptFile(String objectKey) {
        log.info("Re-encrypting {}/{} with custom key", bucketName, objectKey);
        CopyObjectRequest request = CopyObjectRequest.builder()
                .sourceBucket(bucketName)
                .destinationBucket(bucketName)
                .sourceKey(objectKey)
                .destinationKey(objectKey)
                .ssekmsKeyId(encryptionKeyArn)
                .serverSideEncryption(ServerSideEncryption.AWS_KMS)
                .build();
        opintopolkuS3Client.copyObject(request).join();
        log.info("{}/{} re-encrypted with custom key", bucketName, objectKey);
    }
}
