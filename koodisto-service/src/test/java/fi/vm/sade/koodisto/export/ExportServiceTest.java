package fi.vm.sade.koodisto.export;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Sql("/truncate_tables.sql")
@Sql("/test-data.sql")
@SpringBootTest(properties = {
        "koodisto.tasks.export.upload-to-s3=false",
})
class ExportServiceTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired ExportService exportService;
    @Autowired JdbcTemplate jdbcTemplate;

    @Test
    void exportSchemaIsCreated() {
        exportService.createSchema();
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM export.koodi", Long.class)).isEqualTo(215);
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM export.relaatio", Long.class)).isEqualTo(24);
    }

    @Test
    void jsonExport() throws IOException {
        exportService.createSchema();
        var files = exportService.generateJsonExports();
        assertThat(files).hasSize(2);

        var koodit = objectMapper.readValue(files.get(0), new TypeReference<List<ExportedKoodi>>(){});
        assertThat(koodit).hasSize(215);
        var relaatiot = objectMapper.readValue(files.get(1), new TypeReference<List<ExportedRelaatio>>(){});
        assertThat(relaatiot).hasSize(24);
    }
}