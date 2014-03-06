package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import org.codehaus.jackson.map.annotate.JsonView;

import java.util.HashSet;
import java.util.Set;

public class KoodistoRyhmaDto {
    @JsonView(JsonViews.Basic.class)
    private String koodistoRyhmaUri;
    @JsonView(JsonViews.Basic.class)
    private Set<KoodistoRyhmaMetadata> koodistoRyhmaMetadatas = new HashSet<KoodistoRyhmaMetadata>();

    public String getKoodistoRyhmaUri() {
        return koodistoRyhmaUri;
    }

    public void setKoodistoRyhmaUri(final String koodistoRyhmaUri) {
        this.koodistoRyhmaUri = koodistoRyhmaUri;
    }

    public Set<KoodistoRyhmaMetadata> getKoodistoRyhmaMetadatas() {
        return koodistoRyhmaMetadatas;
    }

    public void setKoodistoRyhmaMetadatas(final Set<KoodistoRyhmaMetadata> koodistoRyhmaMetadatas) {
        this.koodistoRyhmaMetadatas = koodistoRyhmaMetadatas;
    }
}
