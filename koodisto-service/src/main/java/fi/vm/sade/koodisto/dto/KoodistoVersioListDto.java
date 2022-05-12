package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.Tila;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class KoodistoVersioListDto {

    @JsonView({JsonViews.Basic.class,JsonViews.Simple.class})
    private int versio;

    @JsonView(JsonViews.Basic.class)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date paivitysPvm;

    @JsonView({JsonViews.Basic.class,JsonViews.Simple.class})
    private Date voimassaAlkuPvm;

    @JsonView({JsonViews.Basic.class,JsonViews.Simple.class})
    private Date voimassaLoppuPvm;

    @JsonView(JsonViews.Basic.class)
    private Tila tila;

    @JsonView(JsonViews.Basic.class)
    private Long version;

    @JsonView({JsonViews.Basic.class,JsonViews.Simple.class})
    private List<KoodistoMetadata> metadata = new ArrayList<KoodistoMetadata>();

}
