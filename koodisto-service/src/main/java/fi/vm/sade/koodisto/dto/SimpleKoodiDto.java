package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.views.JsonViews;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SimpleKoodiDto {

    @JsonView({JsonViews.Simple.class})
    private String koodiUri;

    @JsonView({JsonViews.Simple.class})
    protected List<KoodiMetadata> metadata = new ArrayList<KoodiMetadata>();

    @JsonView({JsonViews.Simple.class})
    private int versio;

    @JsonView({JsonViews.Simple.class})
    private String koodiArvo;

}
