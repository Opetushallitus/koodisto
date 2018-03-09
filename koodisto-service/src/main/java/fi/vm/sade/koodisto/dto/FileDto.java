package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;


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
