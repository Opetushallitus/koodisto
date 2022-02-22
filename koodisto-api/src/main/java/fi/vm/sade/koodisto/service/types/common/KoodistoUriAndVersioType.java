
package fi.vm.sade.koodisto.service.types.common;

import java.io.Serializable;

public class KoodistoUriAndVersioType implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected String koodistoUri;
    protected int versio;

    public String getKoodistoUri() {
        return koodistoUri;
    }

    public void setKoodistoUri(String value) {
        this.koodistoUri = value;
    }

    public int getVersio() {
        return versio;
    }

    public void setVersio(int value) {
        this.versio = value;
    }

}
