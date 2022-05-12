package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import lombok.Getter;
import lombok.Setter;

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
    private Set<KoodistoRyhmaMetadata> koodistoRyhmaMetadatas = new HashSet<KoodistoRyhmaMetadata>();
    @JsonView(JsonViews.Basic.class)
    private Set<Koodisto> koodistos = new HashSet<Koodisto>();

}
