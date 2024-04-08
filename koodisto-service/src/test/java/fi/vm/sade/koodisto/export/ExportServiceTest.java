package fi.vm.sade.koodisto.export;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Sql("/truncate_tables.sql")
@Sql("/test-data.sql")
@SpringBootTest(properties = {
        "koodisto.tasks.export.upload-to-s3=false",
})
class ExportServiceTest {
    @Autowired ExportService exportService;
    @Autowired JdbcTemplate jdbcTemplate;

    @Test
    void exportSchemaIsCreated() {
        exportService.createSchema();
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM export.koodi", Long.class)).isEqualTo(215);
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM export.relaatio", Long.class)).isEqualTo(24);
    }

    @Test
    void jsonExportDoesNotThrow() throws IOException {
        exportService.createSchema();
        exportService.generateJsonExports();
    }
}