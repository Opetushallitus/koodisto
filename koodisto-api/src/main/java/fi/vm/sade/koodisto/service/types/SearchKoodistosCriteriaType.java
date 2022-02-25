
package fi.vm.sade.koodisto.service.types;

import fi.vm.sade.koodisto.service.types.common.TilaType;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchKoodistosCriteriaType implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected List<String> koodistoUris;
    protected Integer koodistoVersio;
    protected SearchKoodistosVersioSelectionType koodistoVersioSelection;
    protected List<TilaType> koodistoTilas;
    protected Date validAt;

    public List<String> getKoodistoUris() {
        if (koodistoUris == null) {
            koodistoUris = new ArrayList<String>();
        }
        return this.koodistoUris;
    }

    public Integer getKoodistoVersio() {
        return koodistoVersio;
    }

    public void setKoodistoVersio(Integer value) {
        this.koodistoVersio = value;
    }

    public SearchKoodistosVersioSelectionType getKoodistoVersioSelection() {
        return koodistoVersioSelection;
    }

    public void setKoodistoVersioSelection(SearchKoodistosVersioSelectionType value) {
        this.koodistoVersioSelection = value;
    }

    public List<TilaType> getKoodistoTilas() {
        if (koodistoTilas == null) {
            koodistoTilas = new ArrayList<TilaType>();
        }
        return this.koodistoTilas;
    }

    public Date getValidAt() {
        return validAt;
    }

    public void setValidAt(Date value) {
        this.validAt = value;
    }

}