package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;

import java.util.ArrayList;
import java.util.List;

/**
 * User: wuoti
 * Date: 21.5.2013
 * Time: 9.41
 */
public class KoodistoItemDto {

    private String koodistoUri;

    private String organisaatioOid;

    private List<Integer> koodistoVersios = new ArrayList<Integer>();

    public String getKoodistoUri() {
        return koodistoUri;
    }

    public void setKoodistoUri(String koodistoUri) {
        this.koodistoUri = koodistoUri;
    }

    public String getOrganisaatioOid() {
        return organisaatioOid;
    }

    public void setOrganisaatioOid(String organisaatioOid) {
        this.organisaatioOid = organisaatioOid;
    }

    public List<Integer> getKoodistoVersios() {
        return koodistoVersios;
    }

    public void setKoodistoVersios(List<Integer> koodistoVersios) {
        this.koodistoVersios = koodistoVersios;
    }
}
