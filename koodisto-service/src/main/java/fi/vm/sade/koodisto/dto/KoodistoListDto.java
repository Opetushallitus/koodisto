package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

import java.util.ArrayList;
import java.util.List;

/**
 * User: wuoti
 * Date: 21.5.2013
 * Time: 9.05
 */
public class KoodistoListDto extends AbstractKoodistoDto {

    @JsonView(JsonViews.Basic.class)
    private KoodistoVersioListDto latestKoodistoVersio;

    @JsonView(JsonViews.Basic.class)
    private List<KoodistoVersioListDto> koodistoVersios = new ArrayList<KoodistoVersioListDto>();


    public KoodistoVersioListDto getLatestKoodistoVersio() {
        return latestKoodistoVersio;
    }

    public void setLatestKoodistoVersio(KoodistoVersioListDto latestKoodistoVersio) {
        this.latestKoodistoVersio = latestKoodistoVersio;
    }

    public List<KoodistoVersioListDto> getKoodistoVersios() {
        return koodistoVersios;
    }

    public void setKoodistoVersios(List<KoodistoVersioListDto> koodistoVersios) {
        this.koodistoVersios = koodistoVersios;
    }
}
