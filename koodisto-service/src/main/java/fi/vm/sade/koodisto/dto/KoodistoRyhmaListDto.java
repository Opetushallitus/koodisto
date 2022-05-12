package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class KoodistoRyhmaListDto {
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class,JsonViews.Simple.class})
    private Long id;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class,JsonViews.Simple.class})
    private String koodistoRyhmaUri;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class,JsonViews.Simple.class})
    private List<KoodistoRyhmaMetadata> metadata = new ArrayList<KoodistoRyhmaMetadata>();

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class,JsonViews.Simple.class})
    private List<KoodistoListDto> koodistos = new ArrayList<KoodistoListDto>();

}
