package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;


public class FileDto {
    @JsonView({JsonViews.Basic.class})
    private String data;

    public String getData() {
        return data;
    }

    public void setData(final String data) {
        this.data = data;
    }
}
