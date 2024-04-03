package fi.vm.sade.koodisto.export;

import com.github.kagkarlsson.scheduler.task.Task;
import com.github.kagkarlsson.scheduler.task.TaskWithoutDataDescriptor;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import com.github.kagkarlsson.scheduler.task.schedule.FixedDelay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("dev")
public class ExportService {
    private final JdbcTemplate jdbcTemplate;

    @Bean
    @ConditionalOnProperty(name = "koodisto.tasks.export-enabled", matchIfMissing = false)
    Task<Void> createSchemaTask() {
        return Tasks.recurring(new TaskWithoutDataDescriptor("Data export: create schema"),
                FixedDelay.ofSeconds(5)).execute((taskInstance, executionContext) -> createSchemaAndWriteAsCsvToDisk());
    }

    void createSchemaAndWriteAsCsvToDisk() {
        createSchema();
        exportTableToS3();
    }

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

    void exportTableToS3() {
        var bucketName = "oph-yleiskayttoiset-export-" + System.getenv("ENV_NAME");
        var objectKey = "fulldump/v2/koodi.csv";
        var awsRegion = "eu-west-1";

        log.info("Exporting table to S3: {}/{}", bucketName, objectKey);
        var sql = """
                SELECT * FROM aws_s3.query_export_to_s3(
                    'SELECT koodistouri, koodiuri, koodiarvo, koodinimi_fi, koodinimi_sv FROM export.koodi',
                    aws_commons.create_s3_uri(?, ?, ?)
                )
                """;
        jdbcTemplate.update(sql, bucketName, objectKey, awsRegion);
    }
}