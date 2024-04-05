package fi.vm.sade.koodisto.export;

import lombok.Data;

import java.util.List;

@Data
public class ExportManifest {
    private final List<ExportFileDetails> exportFiles;

    @Data
    public static class ExportFileDetails {
        private final String objectKey;
        private final String objectVersion;
    }
}
