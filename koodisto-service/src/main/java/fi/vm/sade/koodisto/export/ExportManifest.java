package fi.vm.sade.koodisto.export;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExportManifest {
    private final List<ExportFileDetails> exportFiles;

    @Data
    public static class ExportFileDetails {
        private final String objectKey;
        private final String objectVersion;
    }
}
