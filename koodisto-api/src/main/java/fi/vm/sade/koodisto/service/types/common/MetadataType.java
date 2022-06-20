package fi.vm.sade.koodisto.service.types.common;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.views.JsonViews;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class MetadataType implements Serializable {

    @JsonView({JsonViews.Internal.class, JsonViews.Extended.class})
    @NotNull(message = "error.validation.language")
    protected KieliType kieli;

    @JsonView({JsonViews.Internal.class, JsonViews.Extended.class})
    protected String nimi;
    protected String kuvaus;

    public KieliType getKieli() {
        return kieli;
    }

    public void setKieli(KieliType value) {
        this.kieli = value;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String value) {
        this.nimi = value;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String value) {
        this.kuvaus = value;
    }

}
