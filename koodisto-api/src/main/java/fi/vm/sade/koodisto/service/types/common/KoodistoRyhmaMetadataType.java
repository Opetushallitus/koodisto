
package fi.vm.sade.koodisto.service.types.common;

import java.io.Serializable;


public class KoodistoRyhmaMetadataType implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected String nimi;
    protected KieliType kieli;

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String value) {
        this.nimi = value;
    }

    public KieliType getKieli() {
        return kieli;
    }

    public void setKieli(KieliType value) {
        this.kieli = value;
    }
}
