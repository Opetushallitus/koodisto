package fi.vm.sade.koodisto.datantuonti;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.koodisto.export.OpintopolkuAwsClients;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.DownloadFileRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatantuontiImportService {
    private final S3AsyncClient opintopolkuS3Client;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    @Value("${koodisto.tasks.datantuonti.import.bucket-name}")
    private String bucketName;

    @Transactional
    public void importTempTablesFromS3() throws IOException {
        var manifest = getManifest();
        jdbcTemplate.execute("DROP SCHEMA IF EXISTS datantuonti_import CASCADE");
        jdbcTemplate.execute("CREATE schema datantuonti_import");

        jdbcTemplate.execute("CREATE TABLE datantuonti_import.koodi AS TABLE public.koodi WITH NO DATA");
        jdbcTemplate.execute("CREATE TABLE datantuonti_import.koodimetadata AS TABLE public.koodimetadata WITH NO DATA");
        jdbcTemplate.execute("CREATE TABLE datantuonti_import.koodinsuhde AS TABLE public.koodinsuhde WITH NO DATA");
        jdbcTemplate.execute("CREATE TABLE datantuonti_import.koodisto AS TABLE public.koodisto WITH NO DATA");
        jdbcTemplate.execute("CREATE TABLE datantuonti_import.koodistometadata AS TABLE public.koodistometadata WITH NO DATA");
        jdbcTemplate.execute("CREATE TABLE datantuonti_import.koodistonsuhde AS TABLE public.koodistonsuhde WITH NO DATA");
        jdbcTemplate.execute("CREATE TABLE datantuonti_import.koodistoryhma AS TABLE public.koodistoryhma WITH NO DATA");
        jdbcTemplate.execute("CREATE TABLE datantuonti_import.koodistoryhma_koodisto AS TABLE public.koodistoryhma_koodisto WITH NO DATA");
        jdbcTemplate.execute("CREATE TABLE datantuonti_import.koodistoryhmametadata AS TABLE public.koodistoryhmametadata WITH NO DATA");
        jdbcTemplate.execute("CREATE TABLE datantuonti_import.koodistoversio AS TABLE public.koodistoversio WITH NO DATA");
        jdbcTemplate.execute("CREATE TABLE datantuonti_import.koodistoversio_koodiversio AS TABLE public.koodistoversio_koodiversio WITH NO DATA");
        jdbcTemplate.execute("CREATE TABLE datantuonti_import.koodiversio AS TABLE public.koodiversio WITH NO DATA");
        jdbcTemplate.execute("CREATE TABLE datantuonti_import.hibernate_sequence(last_value bigint)");

        importDataFromS3("datantuonti_import.koodi", manifest.koodi());
        importDataFromS3("datantuonti_import.koodimetadata", manifest.koodimetadata());
        importDataFromS3("datantuonti_import.koodinsuhde", manifest.koodinsuhde());
        importDataFromS3("datantuonti_import.koodisto", manifest.koodisto());
        importDataFromS3("datantuonti_import.koodistometadata", manifest.koodistometadata());
        importDataFromS3("datantuonti_import.koodistonsuhde", manifest.koodistonsuhde());
        importDataFromS3("datantuonti_import.koodistoryhma", manifest.koodistoryhma());
        importDataFromS3("datantuonti_import.koodistoryhma_koodisto", manifest.koodistoryhma_koodisto());
        importDataFromS3("datantuonti_import.koodistoryhmametadata", manifest.koodistoryhmametadata());
        importDataFromS3("datantuonti_import.koodistoversio", manifest.koodistoversio());
        importDataFromS3("datantuonti_import.koodistoversio_koodiversio", manifest.koodistoversio_koodiversio());
        importDataFromS3("datantuonti_import.koodiversio", manifest.koodiversio());
        importDataFromS3("datantuonti_import.hibernate_sequence", manifest.hibernate_sequence());
    }

    private void importDataFromS3(String table, String objectKey) {
        var query = "SELECT * from aws_s3.table_import_from_s3(?, '',  '(FORMAT CSV,HEADER true)', aws_commons.create_s3_uri(?, ?, ?))";
        var result = jdbcTemplate.queryForObject(query, String.class, table, bucketName, objectKey, OpintopolkuAwsClients.REGION.id());
        log.info("Importing {} from S3 returned {}", objectKey, result);
    }

    private DatantuontiManifest getManifest() throws IOException {
        var manifestObjectKey = "koodisto/v1/manifest.json";
        var temporaryFile = File.createTempFile("manifest", ".json");
        try {
            log.info("Downloading manifest from S3: {}/{}", bucketName, manifestObjectKey);
            try (var downloader = S3TransferManager.builder().s3Client(opintopolkuS3Client).build()) {
                var fileDownload = downloader.downloadFile(DownloadFileRequest.builder()
                        .getObjectRequest(b -> b.bucket(bucketName).key(manifestObjectKey))
                        .destination(temporaryFile)
                        .build());
                fileDownload.completionFuture().join();
                return objectMapper.readValue(temporaryFile, DatantuontiManifest.class);
            }
        } finally {
            Files.deleteIfExists(temporaryFile.toPath());
        }
    }

    @Transactional
    public void replaceData() {
        truncateTables();
        insertData();
        updateHibernateSequence();
    }

    private void truncateTables() {
        var sql = """
          TRUNCATE koodi,
                   koodimetadata,
                   koodinsuhde,
                   koodisto,
                   koodistometadata,
                   koodistonsuhde,
                   koodistoryhma,
                   koodistoryhma_koodisto,
                   koodistoryhmametadata,
                   koodistoversio,
                   koodistoversio_koodiversio,
                   koodiversio
        """;
        jdbcTemplate.execute(sql);
    }

    private void insertData() {
        jdbcTemplate.execute("SET CONSTRAINTS ALL DEFERRED");
        jdbcTemplate.execute("INSERT INTO public.koodi (SELECT * FROM datantuonti_import.koodi)");
        jdbcTemplate.execute("INSERT INTO public.koodimetadata (SELECT * FROM datantuonti_import.koodimetadata)");
        jdbcTemplate.execute("INSERT INTO public.koodinsuhde (SELECT * FROM datantuonti_import.koodinsuhde)");
        jdbcTemplate.execute("INSERT INTO public.koodisto (SELECT * FROM datantuonti_import.koodisto)");
        jdbcTemplate.execute("INSERT INTO public.koodistometadata (SELECT * FROM datantuonti_import.koodistometadata)");
        jdbcTemplate.execute("INSERT INTO public.koodistonsuhde (SELECT * FROM datantuonti_import.koodistonsuhde)");
        jdbcTemplate.execute("INSERT INTO public.koodistoryhma (SELECT * FROM datantuonti_import.koodistoryhma)");
        jdbcTemplate.execute("INSERT INTO public.koodistoryhma_koodisto (SELECT * FROM datantuonti_import.koodistoryhma_koodisto)");
        jdbcTemplate.execute("INSERT INTO public.koodistoryhmametadata (SELECT * FROM datantuonti_import.koodistoryhmametadata)");
        jdbcTemplate.execute("INSERT INTO public.koodistoversio (SELECT * FROM datantuonti_import.koodistoversio)");
        jdbcTemplate.execute("INSERT INTO public.koodistoversio_koodiversio (SELECT * FROM datantuonti_import.koodistoversio_koodiversio)");
        jdbcTemplate.execute("INSERT INTO public.koodiversio (SELECT * FROM datantuonti_import.koodiversio)");
    }

    private void updateHibernateSequence() {
        jdbcTemplate.execute("SELECT setval('hibernate_sequence' (SELECT last_value FROM datantuonti_import.hibernate_sequence))");
    }
};