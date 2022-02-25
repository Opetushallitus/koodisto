package fi.vm.sade.koodisto.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * User: wuoti
 * Date: 21.5.2013
 * Time: 9.05
 */
public class KoodistoListDto extends AbstractKoodistoDto {

    private KoodistoVersioListDto latestKoodistoVersio;

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
