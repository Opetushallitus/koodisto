package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class KoodistoItemDto {

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String koodistoUri;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String organisaatioOid;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private List<Integer> koodistoVersios = new ArrayList<>();
}
