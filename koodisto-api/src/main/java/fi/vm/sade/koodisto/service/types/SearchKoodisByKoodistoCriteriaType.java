
package fi.vm.sade.koodisto.service.types;

import fi.vm.sade.koodisto.service.types.common.TilaType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchKoodisByKoodistoCriteriaType implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String koodistoUri;
    protected Integer koodistoVersio;

    protected SearchKoodisByKoodistoVersioSelectionType koodistoVersioSelection;

    private List<TilaType> koodistoTilas;

    protected Date validAt;
    protected KoodiBaseSearchCriteriaType koodiSearchCriteria;

    /**
     * Gets the value of the koodistoUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKoodistoUri() {
        return koodistoUri;
    }

    /**
     * Sets the value of the koodistoUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKoodistoUri(String value) {
        this.koodistoUri = value;
    }

    /**
     * Gets the value of the koodistoVersio property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getKoodistoVersio() {
        return koodistoVersio;
    }

    /**
     * Sets the value of the koodistoVersio property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setKoodistoVersio(Integer value) {
        this.koodistoVersio = value;
    }

    /**
     * Gets the value of the koodistoVersioSelection property.
     * 
     * @return
     *     possible object is
     *     {@link SearchKoodisByKoodistoVersioSelectionType }
     *     
     */
    public SearchKoodisByKoodistoVersioSelectionType getKoodistoVersioSelection() {
        return koodistoVersioSelection;
    }

    /**
     * Sets the value of the koodistoVersioSelection property.
     * 
     * @param value
     *     allowed object is
     *     {@link SearchKoodisByKoodistoVersioSelectionType }
     *     
     */
    public void setKoodistoVersioSelection(SearchKoodisByKoodistoVersioSelectionType value) {
        this.koodistoVersioSelection = value;
    }

    public List<TilaType> getKoodistoTilas() {
        if (koodistoTilas == null) {
            koodistoTilas = new ArrayList<>();
        }
        return this.koodistoTilas;
    }

    public Date getValidAt() {
        return validAt;
    }

    public void setValidAt(Date value) {
        this.validAt = value;
    }

    public KoodiBaseSearchCriteriaType getKoodiSearchCriteria() {
        return koodiSearchCriteria;
    }

    public void setKoodiSearchCriteria(KoodiBaseSearchCriteriaType value) {
        this.koodiSearchCriteria = value;
    }

}
