
package fi.vm.sade.koodisto.service.types.common;

import java.io.Serializable;

public class KoodiUriAndVersioType implements Serializable
{

    private final static long serialVersionUID = 1L;

    protected String koodiUri;
    protected int versio;

    public String getKoodiUri() {
        return koodiUri;
    }

    public void setKoodiUri(String value) {
        this.koodiUri = value;
    }

    public int getVersio() {
        return versio;
    }

    public void setVersio(int value) {
        this.versio = value;
    }

}
