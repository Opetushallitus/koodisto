package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.views.JsonViews;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class KoodistoRyhmaDto {
    @JsonView(JsonViews.Basic.class)
    private Long id;
    @JsonView(JsonViews.Basic.class)
    private String koodistoRyhmaUri;
    @JsonView(JsonViews.Basic.class)
    @NotEmpty(message = "error.metadata.empty")
    private Set<KoodistoRyhmaMetadata> koodistoRyhmaMetadatas = new HashSet<>();
    @JsonView(JsonViews.Basic.class)
    private Set<Koodisto> koodistos = new HashSet<>();
}
