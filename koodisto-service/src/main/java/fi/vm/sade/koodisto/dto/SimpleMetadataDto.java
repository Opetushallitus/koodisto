package fi.vm.sade.koodisto.dto;

import org.codehaus.jackson.map.annotate.JsonView;

import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Kieli;

public class SimpleMetadataDto {
    
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    public final String nimi;
    
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    public final Kieli kieli;
    
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    public final String kuvaus;

    public SimpleMetadataDto(String nimi, Kieli kieli, String kuvaus) {
        this.nimi = nimi;
        this.kieli = kieli;
        this.kuvaus = kuvaus;
    }
    
    @SuppressWarnings("unused")
    private SimpleMetadataDto() {
        nimi = null;
        kieli = null;
        kuvaus = null;
    }
}
