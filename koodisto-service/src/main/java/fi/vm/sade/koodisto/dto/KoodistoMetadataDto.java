package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class KoodistoMetadataDto   {


    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Intetrnal.class })
    private Kieli kieli;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Intetrnal.class })
    private String nimi;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    private String kuvaus;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    private String kayttoohje;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    private String kasite;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    private String kohdealue;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    private String sitovuustaso;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    private String kohdealueenOsaAlue;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    private String toimintaymparisto;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    private String tarkentaaKoodistoa;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    private String huomioitavaKoodisto;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    private String koodistonLahde;

    private KoodistoVersio koodistoVersio;
}
