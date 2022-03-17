package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;


public class FileDto {
    private String data;

    public String getData() {
        return data;
    }

    public void setData(final String data) {
        this.data = data;
    }
}
