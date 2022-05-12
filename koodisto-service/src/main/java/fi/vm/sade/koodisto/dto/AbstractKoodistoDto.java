package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractKoodistoDto {

    @JsonView({JsonViews.Extended.class,JsonViews.Basic.class,JsonViews.Simple.class})
    private String koodistoUri;

    @JsonView({JsonViews.Extended.class,JsonViews.Basic.class,JsonViews.Basic.class})
    private String resourceUri;

    @JsonView({JsonViews.Extended.class,JsonViews.Basic.class})
    private String omistaja;

    @JsonView({JsonViews.Extended.class,JsonViews.Basic.class,JsonViews.Simple.class})
    private String organisaatioOid;

    @JsonView({JsonViews.Extended.class,JsonViews.Basic.class})
    private Boolean lukittu;

}
