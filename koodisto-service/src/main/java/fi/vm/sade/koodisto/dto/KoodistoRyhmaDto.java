package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;

import java.util.HashSet;
import java.util.Set;

public class KoodistoRyhmaDto {
    private Long id;
    private String koodistoRyhmaUri;
    private Set<KoodistoRyhmaMetadata> koodistoRyhmaMetadatas = new HashSet<KoodistoRyhmaMetadata>();
    private Set<Koodisto> koodistos = new HashSet<Koodisto>();

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

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

    public Set<Koodisto> getKoodistos() {
        return koodistos;
    }

    public void setKoodistos(Set<Koodisto> koodistos) {
        this.koodistos = koodistos;
    }

}
