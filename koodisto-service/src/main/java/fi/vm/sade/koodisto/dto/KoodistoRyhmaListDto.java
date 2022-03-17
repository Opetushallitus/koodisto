package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * User: wuoti
 * Date: 21.5.2013
 * Time: 9.03
 */
public class KoodistoRyhmaListDto {
    private Long id;

    private String koodistoRyhmaUri;

    private List<KoodistoRyhmaMetadata> metadata = new ArrayList<KoodistoRyhmaMetadata>();

    private List<KoodistoListDto> koodistos = new ArrayList<KoodistoListDto>();

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getKoodistoRyhmaUri() {
        return koodistoRyhmaUri;
    }

    public void setKoodistoRyhmaUri(String koodistoRyhmaUri) {
        this.koodistoRyhmaUri = koodistoRyhmaUri;
    }

    public List<KoodistoRyhmaMetadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<KoodistoRyhmaMetadata> metadata) {
        this.metadata = metadata;
    }

    public List<KoodistoListDto> getKoodistos() {
        return koodistos;
    }

    public void setKoodistos(List<KoodistoListDto> koodistos) {
        this.koodistos = koodistos;
    }
}
