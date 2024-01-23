package fi.vm.sade.koodisto.export;

import com.github.kagkarlsson.scheduler.task.Task;
import com.github.kagkarlsson.scheduler.task.TaskWithoutDataDescriptor;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import com.github.kagkarlsson.scheduler.task.schedule.FixedDelay;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        writeSchemaAsCsvToDisk();
    }

    @Transactional
    void createSchema() {
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

    @SneakyThrows
    void writeSchemaAsCsvToDisk() {
        var valueSeparator = ",";
        var csvString = jdbcTemplate.query("SELECT * FROM export.koodi", new ResultSetExtractor<String>() {
            @Override
            public String extractData(ResultSet rs) throws SQLException, DataAccessException {
                var columnCount = rs.getMetaData().getColumnCount();
                var buffer = new StringBuffer();

                for (int col = 1; col <= columnCount; ++col) {
                    buffer.append(rs.getMetaData().getColumnName(col));

                    if (col < columnCount) {
                        buffer.append(valueSeparator);
                    }
                }

                while (rs.next()) {
                    buffer.append("\n");

                    for (int col = 1; col <= columnCount; ++col) {
                        var rawString = rs.getString(col);

                        if (rawString != null) {
                            buffer.append(escapeCharacters(replaceNewLinesWithSpaces(rawString)));
                        } else {
                            buffer.append("");
                        }

                        if (col < columnCount) {
                            buffer.append(valueSeparator);
                        }
                    }
                }

                return buffer.toString();
            }
        });

        var tmpFile = File.createTempFile("koodi", ".csv");
        try (var writer = new FileWriter(tmpFile)) {
            writer.write(csvString);
        }
        log.info("Data Export: data written to " + tmpFile);
    }

    private String replaceNewLinesWithSpaces(String s) {
        return s.replaceAll("\\R", " ");
    }

    private String escapeCharacters(String s) {
        if (s.contains(",") || s.contains("\"") || s.contains("'")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        } else {
            return s;
        }
    }
}