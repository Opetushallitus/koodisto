
package fi.vm.sade.koodisto.service.types;

import fi.vm.sade.koodisto.service.types.common.TilaType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KoodiBaseSearchCriteriaType implements Serializable
{

    private static final long serialVersionUID = 1L;
    private List<String> koodiUris;
    protected String koodiArvo;

    private List<TilaType> koodiTilas;
    protected Date validAt;

    public List<String> getKoodiUris() {
        if (koodiUris == null) {
            koodiUris = new ArrayList<String>();
        }
        return this.koodiUris;
    }

    public String getKoodiArvo() {
        return koodiArvo;
    }

    public void setKoodiArvo(String value) {
        this.koodiArvo = value;
    }

    public List<TilaType> getKoodiTilas() {
        if (koodiTilas == null) {
            koodiTilas = new ArrayList<TilaType>();
        }
        return this.koodiTilas;
    }

    public Date getValidAt() {
        return validAt;
    }

    public void setValidAt(Date value) {
        this.validAt = value;
    }

}
