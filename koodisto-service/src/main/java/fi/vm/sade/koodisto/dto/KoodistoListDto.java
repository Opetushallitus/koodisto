package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class KoodistoListDto extends AbstractKoodistoDto {

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    private KoodistoVersioListDto latestKoodistoVersio;

    @JsonView(JsonViews.Basic.class)
    private List<KoodistoVersioListDto> koodistoVersios = new ArrayList<>();
}
