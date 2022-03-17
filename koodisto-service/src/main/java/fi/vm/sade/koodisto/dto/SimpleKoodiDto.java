package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodiMetadata;

import java.util.ArrayList;
import java.util.List;

public class SimpleKoodiDto {

    private String koodiUri;

    protected List<KoodiMetadata> metadata = new ArrayList<KoodiMetadata>();

    private int versio;

    private String koodiArvo;

    public String getKoodiUri() {
        return koodiUri;
    }

    public void setKoodiUri(final String koodiUri) {
        this.koodiUri = koodiUri;
    }

    public List<KoodiMetadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(final List<KoodiMetadata> metadata) {
        this.metadata = metadata;
    }

    public int getVersio() {
        return versio;
    }

    public void setVersio(final int versio) {
        this.versio = versio;
    }

    public String getKoodiArvo() {
        return koodiArvo;
    }

    public void setKoodiArvo(final String koodiArvo) {
        this.koodiArvo = koodiArvo;
    }
}
