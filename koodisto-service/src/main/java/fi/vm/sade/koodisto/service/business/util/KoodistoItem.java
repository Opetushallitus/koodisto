package fi.vm.sade.koodisto.service.business.util;

import java.util.HashSet;
import java.util.Set;

public class KoodistoItem {

    private String koodistoUri;
    private String organisaatioOid;

    private Set<Integer> versios = new HashSet<Integer>();

    public KoodistoItem() {

    }

    public KoodistoItem(String koodistoUri, Set<Integer> versios) {
        this.koodistoUri = koodistoUri;
        this.versios = versios;
    }

    public String getKoodistoUri() {
        return koodistoUri;
    }

    public void setKoodistoUri(String koodistoUri) {
        this.koodistoUri = koodistoUri;
    }

    public Set<Integer> getVersios() {
        return versios;
    }

    public void addVersio(Integer versio) {
        this.versios.add(versio);
    }

    public String getOrganisaatioOid() {
        return organisaatioOid;
    }

    public void setOrganisaatioOid(String organisaatioOid) {
        this.organisaatioOid = organisaatioOid;
    }
}
