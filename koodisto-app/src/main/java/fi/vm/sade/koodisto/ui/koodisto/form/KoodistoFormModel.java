/**
 * 
 */
package fi.vm.sade.koodisto.ui.koodisto.form;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.ui.util.UtilityMethods;

/**
 * @author tommiha
 * 
 */
public class KoodistoFormModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2551611715511402826L;

    private KoodistoType koodisto;
    private Date voimassaAlkuPvm;
    private Date voimassaLoppuPvm;
    private Date paivitysPvm;

    private KoodistoMetadataType fiMetadata;
    private KoodistoMetadataType svMetadata;
    private KoodistoMetadataType enMetadata;

    public KoodistoType getKoodisto() {
        return koodisto;
    }

    public void setKoodisto(KoodistoType koodisto) {
        this.koodisto = koodisto;
    }

    public Date getVoimassaAlkuPvm() {
        return voimassaAlkuPvm;
    }

    public void setVoimassaAlkuPvm(Date voimassaAlkuPvm) {
        this.voimassaAlkuPvm = voimassaAlkuPvm;
    }

    public Date getVoimassaLoppuPvm() {
        return voimassaLoppuPvm;
    }

    public void setVoimassaLoppuPvm(Date voimassaLoppuPvm) {
        this.voimassaLoppuPvm = voimassaLoppuPvm;
    }

    public Date getPaivitysPvm() {
        return paivitysPvm;
    }

    public void setPaivitysPvm(Date paivitysPvm) {
        this.paivitysPvm = paivitysPvm;
    }

    public KoodistoMetadataType getFiMetadata() {
        return fiMetadata;
    }

    public void setFiMetadata(KoodistoMetadataType fiMetadata) {
        this.fiMetadata = fiMetadata;
    }

    public KoodistoMetadataType getSvMetadata() {
        return svMetadata;
    }

    public void setSvMetadata(KoodistoMetadataType svMetadata) {
        this.svMetadata = svMetadata;
    }

    public KoodistoMetadataType getEnMetadata() {
        return enMetadata;
    }

    public void setEnMetadata(KoodistoMetadataType enMetadata) {
        this.enMetadata = enMetadata;
    }

    private boolean metadataFieldsAreNullOrEmpty(KoodistoMetadataType metadata) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        return UtilityMethods.allFieldsAreNullOrEmpty(KoodistoMetadataType.class, metadata, "kieli");
    }

    public List<KoodistoMetadataType> getMetadatasWithFieldsFilled() {
        try {
            List<KoodistoMetadataType> metadatas = new ArrayList<KoodistoMetadataType>();

            if (!metadataFieldsAreNullOrEmpty(fiMetadata)) {
                metadatas.add(fiMetadata);
            }

            if (!metadataFieldsAreNullOrEmpty(svMetadata)) {
                metadatas.add(svMetadata);
            }

            if (!metadataFieldsAreNullOrEmpty(enMetadata)) {
                metadatas.add(enMetadata);
            }

            return metadatas;
        } catch (Exception e) {
            // Should not happen
            throw new RuntimeException(e);
        }
    }

}
