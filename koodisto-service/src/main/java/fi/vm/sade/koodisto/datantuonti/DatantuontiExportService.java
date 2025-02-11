package fi.vm.sade.koodisto.datantuonti;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;

import java.util.List;

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

    private static final String V1_PREFIX = "koodisto/v1";

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String createSchemaAndReturnTransactionTimestampFromEpoch() {
        jdbcTemplate.execute("DROP SCHEMA IF EXISTS datantuonti_export_new CASCADE");
        jdbcTemplate.execute("CREATE SCHEMA datantuonti_export_new");
        listTableNamesToBeExported().forEach(this::createExportTable);
        jdbcTemplate.execute("DROP SCHEMA IF EXISTS datantuonti_export CASCADE");
        jdbcTemplate.execute("ALTER SCHEMA datantuonti_export_new RENAME TO datantuonti_export");

        return jdbcTemplate.queryForObject("SELECT extract(epoch from transaction_timestamp())", String.class);
    }

    public void generateExportFiles(String timestamp) {
        var tables = listTableNamesToBeExported();
        tables.parallelStream().forEach(table -> writeTableToS3(timestamp, table));
    }

    private void writeTableToS3(String timestamp, String table) {
        var objectKey = V1_PREFIX + "/csv/" + table + "-" + timestamp + ".csv";
        exportQueryToS3(objectKey, "SELECT * FROM datantuonti_export."  + table);
        reEncryptFile(objectKey);
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

    private void createExportTable(String table) {
        jdbcTemplate.execute("CREATE TABLE datantuonti_export_new." + table + " AS SELECT * FROM public."  + table);
    }

    private List<String> listTableNamesToBeExported() {
        var sql = """
          SELECT tablename
          FROM pg_catalog.pg_tables
          WHERE schemaname = 'public'
          AND tablename LIKE 'koodi%';
        """;
        return jdbcTemplate.queryForList(sql, String.class);
    }
}
