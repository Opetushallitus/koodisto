package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.views.JsonViews;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodiVersio;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder

public class KoodiMetadataDto {

    @NotBlank(message = "error.nimi.empty")
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
    private String nimi;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    private String kuvaus;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    private String lyhytNimi;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String kayttoohje;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String kasite;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String sisaltaaMerkityksen;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String eiSisallaMerkitysta;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String huomioitavaKoodi;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String sisaltaaKoodiston;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
    @NotNull
    private Kieli kieli;

    private KoodiVersio koodiVersio;

}
