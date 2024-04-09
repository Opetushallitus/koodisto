package fi.vm.sade.koodisto.export;

import com.fasterxml.jackson.annotation.JsonInclude;
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
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.DownloadFileRequest;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExportService {
    private static final String S3_PREFIX = "fulldump/koodisto/v2";
    private final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    private final JdbcTemplate jdbcTemplate;
    private final S3AsyncClient opintopolkuS3Client;
    private final S3AsyncClient lampiS3Client;

    @Value("${koodisto.tasks.export.bucket-name}")
    private String bucketName;
    @Value("${koodisto.tasks.export.lampi-bucket-name}")
    private String lampiBucketName;
    @Value("${koodisto.tasks.export.upload-to-s3:true}")
    private boolean uploadToS3;

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

    private static final String KOODI_QUERY = "SELECT koodistouri, koodiuri, koodiarvo, koodiversio, tila, voimassaalkupvm, voimassaloppupvm, koodinimi_fi, koodinimi_sv, koodinimi_en, koodikuvaus_fi, koodikuvaus_sv, koodikuvaus_en, koodiversiocreated_at, koodiversioupdated_at FROM export.koodi";
    private static final String  RELAATIO_QUERY = "SELECT ylakoodiuri, ylakoodiversio, relaatiotyyppi, alakoodiuri, alakoodiversio, relaatioversio FROM export.relaatio";

    public void generateExportFiles() throws IOException {
        generateCsvExports();
        generateJsonExports();
    }

    void generateCsvExports() {
        exportQueryToS3(S3_PREFIX + "/csv/koodi.csv", KOODI_QUERY);
        exportQueryToS3(S3_PREFIX + "/csv/relaatio.csv", RELAATIO_QUERY);
    }

    List<File> generateJsonExports() throws IOException {
        var koodiFile = exportQueryToS3AsJson(KOODI_QUERY, S3_PREFIX + "/json/koodi.json", unchecked(rs ->
                new ExportedKoodi(
                        rs.getString("koodistouri"),
                        rs.getString("koodiuri"),
                        rs.getString("koodiarvo"),
                        rs.getLong("koodiversio"),
                        rs.getString("tila"),
                        rs.getString("voimassaalkupvm"), // date
                        rs.getString("voimassaloppupvm"), // date
                        rs.getString("koodinimi_fi"),
                        rs.getString("koodinimi_sv"),
                        rs.getString("koodinimi_en"),
                        rs.getString("koodikuvaus_fi"),
                        rs.getString("koodikuvaus_sv"),
                        rs.getString("koodikuvaus_en"),
                        rs.getString("koodiversiocreated_at"), // timestamp
                        rs.getString("koodiversioupdated_at") // timestamp
                )
        ));
        var relaatioFile = exportQueryToS3AsJson(RELAATIO_QUERY, S3_PREFIX + "/json/relaatio.json", unchecked(rs ->
                new ExportedRelaatio(
                        rs.getString("ylakoodiuri"),
                        rs.getLong("ylakoodiversio"),
                        rs.getString("relaatiotyyppi"),
                        rs.getString("alakoodiuri"),
                        rs.getLong("alakoodiversio"),
                        rs.getLong("relaatioversio")
                )
        ));
        return List.of(koodiFile, relaatioFile);
    }

    private interface ThrowingFunction<T, R, E extends Throwable> {
        R apply(T rs) throws E;
    }


    private <T> File exportQueryToS3AsJson(String query, String objectKey, Function<ResultSet, T> mapper) throws IOException {
        var tempFile = File.createTempFile("export", ".json");
        try {
            exportToFile(query, mapper, tempFile);
            uploadFile(opintopolkuS3Client, bucketName, objectKey, tempFile);
        } finally {
            if (uploadToS3) {
                Files.deleteIfExists(tempFile.toPath());
            } else {
                log.info("Not uploading file to S3, keeping it at {}", tempFile.getAbsolutePath());
            }
        }
        return tempFile;
    }

    private <T> void exportToFile(String query, Function<ResultSet, T> mapper, File file) throws IOException {
        log.info("Writing JSON export to {}", file.getAbsolutePath());
        try (var writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write("[\n");
            var firstElement = true;
            try (Stream<T> stream = jdbcTemplate.queryForStream(query, (rs, n) -> mapper.apply(rs))) {
                Iterable<T> iterable = stream::iterator;
                for (T jsonObject : iterable) {
                    if (firstElement) {
                        firstElement = false;
                    } else {
                        writer.write(",\n");
                    }
                    writer.write(objectMapper.writeValueAsString(jsonObject));
                }
            }
            writer.write("\n");
            writer.write("]\n");
        }
    }

    private <T, R, E extends Throwable> Function<T, R> unchecked(ThrowingFunction<T, R, E> f) {
        return t -> {
            try {
                return f.apply(t);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    private void exportQueryToS3(String objectKey, String query) {
        log.info("Exporting table to S3: {}/{}", bucketName, objectKey);
        var sql = "SELECT rows_uploaded FROM aws_s3.query_export_to_s3(?, aws_commons.create_s3_uri(?, ?, ?), options := 'FORMAT CSV, HEADER TRUE')";
        var rowsUploaded = jdbcTemplate.queryForObject(sql, Long.class, query, bucketName, objectKey, OpintopolkuAwsClients.REGION.id());
        log.info("Exported {} rows to S3 object {}", rowsUploaded, objectKey);
    }

    public void copyExportFilesToLampi() throws IOException {
        var csvManifest = new ArrayList<ExportManifest.ExportFileDetails>();
        csvManifest.add(copyFileToLampi(S3_PREFIX + "/csv/koodi.csv"));
        csvManifest.add(copyFileToLampi(S3_PREFIX + "/csv/relaatio.csv"));
        writeManifest(S3_PREFIX + "/csv/manifest.json", new ExportManifest(csvManifest));

        var jsonManifest = new ArrayList<ExportManifest.ExportFileDetails>();
        jsonManifest.add(copyFileToLampi(S3_PREFIX + "/json/koodi.json"));
        jsonManifest.add(copyFileToLampi(S3_PREFIX + "/json/relaatio.json"));
        writeManifest(S3_PREFIX + "/json/manifest.json", new ExportManifest(jsonManifest));
    }

    private void writeManifest(String objectKey, ExportManifest manifest) throws JsonProcessingException {
        var manifestJson = objectMapper.writeValueAsString(manifest);
        var response = lampiS3Client.putObject(
                b -> b.bucket(lampiBucketName).key(objectKey),
                AsyncRequestBody.fromString(manifestJson)
        ).join();
        log.info("Wrote manifest to S3: {}", response);
    }

    private ExportManifest.ExportFileDetails copyFileToLampi(String objectKey) throws IOException {
        var temporaryFile = File.createTempFile("export", ".csv");
        try {
            log.info("Downloading file from S3: {}/{}", bucketName, objectKey);
            try (var downloader = S3TransferManager.builder().s3Client(opintopolkuS3Client).build()) {
                var fileDownload = downloader.downloadFile(DownloadFileRequest.builder()
                        .getObjectRequest(b -> b.bucket(bucketName).key(objectKey))
                        .destination(temporaryFile)
                        .build());
                fileDownload.completionFuture().join();
            }

            var response = uploadFile(lampiS3Client, lampiBucketName, objectKey, temporaryFile);
            return new ExportManifest.ExportFileDetails(objectKey, response.versionId());
        } finally {
            Files.deleteIfExists(temporaryFile.toPath());
        }
    }

    private PutObjectResponse uploadFile(S3AsyncClient s3Client, String bucketName, String objectKey, File file) {
        if (!uploadToS3) {
            log.info("Skipping upload to S3");
            return null;
        }
        log.info("Uploading file to S3: {}/{}", bucketName, objectKey);
        try (var uploader = S3TransferManager.builder().s3Client(s3Client).build()) {
            var fileUpload = uploader.uploadFile(UploadFileRequest.builder()
                    .putObjectRequest(b -> b.bucket(bucketName).key(objectKey))
                    .source(file)
                    .build());
            var result = fileUpload.completionFuture().join();
            return result.response();
        }
    }
}
record ExportedKoodi(
    String koodistouri,
    String koodiuri,
    String koodiarvo,
    Long koodiversio,
    String tila,
    String voimassaalkupvm,
    String voimassaloppuvpm,
    String koodinimi_fi,
    String koodinimi_sv,
    String koodinimi_en,
    String koodikuvaus_fi,
    String koodikuvaus_sv,
    String koodikuvaus_en,
    String koodiversiocreated_at,
    String koodiversioupdated_at
){}

record ExportedRelaatio(
        String ylakoodiuri,
        Long ylakoodiversio,
        String relaatiotyyppi,
        String alakoodiuri,
        Long alakoodiversio,
        Long relaatioversio
){}
