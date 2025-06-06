package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodiVersio;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder

public class KoodiMetadataDto {

    @NotBlank(message = "error.nimi.empty")
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class, JsonViews.SimpleWithRelations.class})
    private String nimi;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class, JsonViews.SimpleWithRelations.class})
    private String kuvaus;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class, JsonViews.SimpleWithRelations.class})
    @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
    private String lyhytNimi;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String kayttoohje;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    private String kasite;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String sisaltaaMerkityksen;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String eiSisallaMerkitysta;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String huomioitavaKoodi;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String sisaltaaKoodiston;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class, JsonViews.SimpleWithRelations.class})
    @NotNull
    private Kieli kieli;

    private KoodiVersio koodiVersio;

}
