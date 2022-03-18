package fi.vm.sade.koodisto.dto;

public class FileFormatDto {
    @JsonView({JsonViews.Basic.class})
    private String format;
    @JsonView({JsonViews.Basic.class})
    private String encoding;

    public String getFormat() {
        return format;
    }

    public void setFormat(final String format) {
        this.format = format;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
}
