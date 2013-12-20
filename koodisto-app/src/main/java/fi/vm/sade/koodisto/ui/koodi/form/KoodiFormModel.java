/**
 * 
 */
package fi.vm.sade.koodisto.ui.koodi.form;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.ui.util.UtilityMethods;

/**
 * @author tommiha
 * 
 */
@SuppressWarnings("serial")
public class KoodiFormModel implements Serializable {
    private KoodiType koodi;
    private Date voimassaAlkuPvm;
    private Date voimassaLoppuPvm;
    private Date paivitysPvm;

    private KoodiMetadataType fiMetadata;
    private KoodiMetadataType svMetadata;
    private KoodiMetadataType enMetadata;

    public KoodiType getKoodi() {
        return koodi;
    }

    public void setKoodi(KoodiType koodi) {
        this.koodi = koodi;
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

    public KoodiMetadataType getFiMetadata() {
        return fiMetadata;
    }

    public void setFiMetadata(KoodiMetadataType fiMetadata) {
        this.fiMetadata = fiMetadata;
    }

    public KoodiMetadataType getSvMetadata() {
        return svMetadata;
    }

    public void setSvMetadata(KoodiMetadataType svMetadata) {
        this.svMetadata = svMetadata;
    }

    public KoodiMetadataType getEnMetadata() {
        return enMetadata;
    }

    public void setEnMetadata(KoodiMetadataType enMetadata) {
        this.enMetadata = enMetadata;
    }

    private boolean metadataFieldsAreNullOrEmpty(KoodiMetadataType metadata) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return UtilityMethods.allFieldsAreNullOrEmpty(KoodiMetadataType.class, metadata, "kieli");
    }

    public List<KoodiMetadataType> getMetadatasWithFieldsFilled() {
        try {
            List<KoodiMetadataType> metadatas = new ArrayList<KoodiMetadataType>();

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
