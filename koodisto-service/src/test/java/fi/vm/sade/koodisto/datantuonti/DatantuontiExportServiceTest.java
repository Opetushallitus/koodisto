package fi.vm.sade.koodisto.datantuonti;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import static org.assertj.core.api.Assertions.assertThat;


import java.util.Arrays;

@SpringBootTest
public class DatantuontiExportServiceTest {
    @Autowired
    private DatantuontiExportService datantuontiExportService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Sql("/truncate_tables.sql")
    @Sql("/test-data.sql")
    void exportsTables() {
        String[] tables = {
                "koodi",
                "koodimetadata",
                "koodinsuhde",
                "koodisto",
                "koodistometadata",
                "koodistonsuhde",
                "koodistoryhma",
                "koodistoryhma_koodisto",
                "koodistoryhmametadata",
                "koodistoversio",
                "koodistoversio_koodiversio",
                "koodiversio"
        };
        Arrays.stream(tables).forEach(this::verifyTableExistsAndContainsRows);
        datantuontiExportService.createSchemaAndReturnTransactionTimestampFromEpoch();
        Arrays.stream(tables).forEach(this::verifyTableExported);
    }

    private void verifyTableExistsAndContainsRows(String table) {
        var rowCount = jdbcTemplate.queryForObject("SELECT count(*) FROM public." + table, Integer.class);
        assertThat(rowCount).isGreaterThan(0);
    }

    private void verifyTableExported(String table) {
        var expectedRowCount = jdbcTemplate.queryForObject("SELECT count(*) FROM public." + table, Integer.class);
        var actualRowCount = jdbcTemplate.queryForObject("SELECT count(*) FROM datantuonti_export." + table, Integer.class);
        assertThat(actualRowCount).isEqualTo(expectedRowCount);
    }
}
