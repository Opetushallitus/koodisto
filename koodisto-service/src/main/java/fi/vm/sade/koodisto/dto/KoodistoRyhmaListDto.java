package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;
import java.util.List;

/**
 * User: wuoti
 * Date: 21.5.2013
 * Time: 9.03
 */
public class KoodistoRyhmaListDto {
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class,JsonViews.Simple.class})
    private Long id;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class,JsonViews.Simple.class})
    private String koodistoRyhmaUri;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class,JsonViews.Simple.class})
    private List<KoodistoRyhmaMetadata> metadata = new ArrayList<KoodistoRyhmaMetadata>();

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class,JsonViews.Simple.class})
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
