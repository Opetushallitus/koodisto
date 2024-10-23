package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public abstract class AbstractKoodistoDto {

    @NotBlank(message = "error.koodistoUri.blank")
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
    private String koodistoUri;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Basic.class})
    private String resourceUri;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String omistaja;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    @NotBlank(message = "error.organisaatioOid.blank")
    private String organisaatioOid;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private Boolean lukittu;

}
