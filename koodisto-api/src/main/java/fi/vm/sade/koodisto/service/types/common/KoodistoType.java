
package fi.vm.sade.koodisto.service.types.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KoodistoType implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String koodistoUri;
    protected String resourceUri;
    protected int versio;
    protected Date paivitysPvm;
    protected String paivittajaOid;
    protected Date voimassaAlkuPvm;
    protected Date voimassaLoppuPvm;
    protected TilaType tila;
    protected String omistaja;
    protected String organisaatioOid;
    protected Boolean lukittu;
    private List<KoodistoMetadataType> metadataList;
    protected long lockingVersion;

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
     * Gets the value of the resourceUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResourceUri() {
        return resourceUri;
    }

    /**
     * Sets the value of the resourceUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResourceUri(String value) {
        this.resourceUri = value;
    }

    /**
     * Gets the value of the versio property.
     * 
     */
    public int getVersio() {
        return versio;
    }

    /**
     * Sets the value of the versio property.
     * 
     */
    public void setVersio(int value) {
        this.versio = value;
    }

    /**
     * Gets the value of the paivitysPvm property.
     * 
     * @return
     *     possible object is
     *
     *     
     */
    public Date getPaivitysPvm() {
        return paivitysPvm;
    }

    public void setPaivitysPvm(Date value) {
        this.paivitysPvm = value;
    }

    public String getPaivittajaOid() {
        return paivittajaOid;
    }

    public void setPaivittajaOid(String value) {
        this.paivittajaOid = value;
    }

    public Date getVoimassaAlkuPvm() {
        return voimassaAlkuPvm;
    }

    public void setVoimassaAlkuPvm(Date value) {
        this.voimassaAlkuPvm = value;
    }

    public Date getVoimassaLoppuPvm() {
        return voimassaLoppuPvm;
    }

    public void setVoimassaLoppuPvm(Date value) {
        this.voimassaLoppuPvm = value;
    }

    public TilaType getTila() {
        return tila;
    }

    public void setTila(TilaType value) {
        this.tila = value;
    }

    public String getOmistaja() {
        return omistaja;
    }

    public void setOmistaja(String value) {
        this.omistaja = value;
    }

    public String getOrganisaatioOid() {
        return organisaatioOid;
    }

    public void setOrganisaatioOid(String value) {
        this.organisaatioOid = value;
    }

    public Boolean isLukittu() {
        return lukittu;
    }

    public void setLukittu(Boolean value) {
        this.lukittu = value;
    }

    public List<KoodistoMetadataType> getMetadataList() {
        if (metadataList == null) {
            metadataList = new ArrayList<KoodistoMetadataType>();
        }
        return this.metadataList;
    }

    public long getLockingVersion() {
        return lockingVersion;
    }

    public void setLockingVersion(long value) {
        this.lockingVersion = value;
    }

}
