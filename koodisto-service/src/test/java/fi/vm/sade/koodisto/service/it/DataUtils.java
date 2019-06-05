package fi.vm.sade.koodisto.service.it;

import java.util.Calendar;
import java.util.Date;

import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.impl.conversion.koodisto.KoodistoMetadataToKoodistoMetadataTypeConverter;
import fi.vm.sade.koodisto.service.types.CreateKoodiDataType;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.UpdateKoodiDataType;
import fi.vm.sade.koodisto.service.types.UpdateKoodiTilaType;
import fi.vm.sade.koodisto.service.types.UpdateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.service.types.common.TilaType;

public final class DataUtils {

    private DataUtils() {

    }

    public static CreateKoodistoDataType createCreateKoodistoDataType(String omistaja,
                                                                      String organisaatioOid, Date voimassaAlkuPvm, Date voimassaLoppuPvm, String nimi) {
        CreateKoodistoDataType type = new CreateKoodistoDataType();
        type.setOmistaja(omistaja);
        type.setOrganisaatioOid(organisaatioOid);
        type.setVoimassaAlkuPvm(voimassaAlkuPvm != null ? DateHelper.DateToXmlCal(voimassaAlkuPvm) : null);
        type.setVoimassaLoppuPvm(voimassaLoppuPvm != null ? DateHelper.DateToXmlCal(voimassaLoppuPvm) : null);

        for (KieliType k : KieliType.values()) {
            KoodistoMetadataType m = new KoodistoMetadataType();
            m.setNimi(nimi);
            m.setKieli(k);
            m.setKuvaus(nimi);
            type.getMetadataList().add(m);
        }

        return type;
    }

    public static UpdateKoodistoDataType createUpdateKoodistoDataType(String koodistoUri, String omistaja, TilaType tila,
                                                                      String organisaatioOid, Date voimassaAlkuPvm, Date voimassaLoppuPvm, String nimi, int versio, long version) {
        UpdateKoodistoDataType type = new UpdateKoodistoDataType();
        type.setKoodistoUri(koodistoUri);
        type.setOmistaja(omistaja);
        type.setTila(tila);
        type.setOrganisaatioOid(organisaatioOid);
        type.setVoimassaAlkuPvm(voimassaAlkuPvm != null ? DateHelper.DateToXmlCal(voimassaAlkuPvm) : null);
        type.setVoimassaLoppuPvm(voimassaLoppuPvm != null ? DateHelper.DateToXmlCal(voimassaLoppuPvm) : null);

        for (KieliType k : KieliType.values()) {
            KoodistoMetadataType m = new KoodistoMetadataType();
            m.setNimi(nimi);
            m.setKieli(k);
            m.setKuvaus(nimi);
            type.getMetadataList().add(m);
        }

        type.setVersio(versio);
        type.setLockingVersion(version);

        return type;
    }

    public static void copyFields(KoodiType from, UpdateKoodiDataType to) {
        to.setKoodiArvo(from.getKoodiArvo());
        to.setKoodiUri(from.getKoodiUri());

        if (!TilaType.HYVAKSYTTY.equals(from.getTila())) {
            to.setTila(UpdateKoodiTilaType.valueOf(from.getTila().name()));
        }
        to.setVoimassaAlkuPvm(from.getVoimassaAlkuPvm());
        to.setVoimassaLoppuPvm(from.getVoimassaLoppuPvm());
        to.getMetadata().clear();
        to.getMetadata().addAll(from.getMetadata());
        to.setVersio(from.getVersio());
        to.setLockingVersion(from.getLockingVersion());
    }

    public static CreateKoodiDataType createCreateKoodiDataType(String koodiArvo,
                                                                Date voimassaAlkuPvm, Date voimassaLoppuPvm,
                                                                String nimi) {
        CreateKoodiDataType koodiDataType = new CreateKoodiDataType();
        koodiDataType.setKoodiArvo(koodiArvo);
        koodiDataType.setVoimassaAlkuPvm(voimassaAlkuPvm != null ? DateHelper.DateToXmlCal(voimassaAlkuPvm) : null);
        koodiDataType.setVoimassaLoppuPvm(voimassaLoppuPvm != null ? DateHelper.DateToXmlCal(voimassaLoppuPvm) : null);
        for (KieliType k : KieliType.values()) {
            KoodiMetadataType metadataType = new KoodiMetadataType();
            metadataType.setNimi(nimi);
            metadataType.setLyhytNimi(nimi);
            metadataType.setKuvaus(nimi);
            metadataType.setKieli(k);
            koodiDataType.getMetadata().add(metadataType);
        }

        return koodiDataType;
    }

    public static UpdateKoodiDataType createUpdateKoodiDataType(String koodiUri, String koodiArvo,
                                                                UpdateKoodiTilaType tila, Date voimassaAlkuPvm,
                                                                Date voimassaLoppuPvm, String nimi, int versio, long version) {
        UpdateKoodiDataType koodiDataType = new UpdateKoodiDataType();
        koodiDataType.setKoodiUri(koodiUri);
        koodiDataType.setKoodiArvo(koodiArvo);
        koodiDataType.setTila(tila);
        koodiDataType.setVoimassaAlkuPvm(voimassaAlkuPvm != null ? DateHelper.DateToXmlCal(voimassaAlkuPvm) : null);
        koodiDataType.setVoimassaLoppuPvm(voimassaLoppuPvm != null ? DateHelper.DateToXmlCal(voimassaLoppuPvm) : null);
        for (KieliType k : KieliType.values()) {
            KoodiMetadataType metadataType = new KoodiMetadataType();
            metadataType.setNimi(nimi);
            metadataType.setLyhytNimi(nimi);
            metadataType.setKuvaus(nimi);
            metadataType.setKieli(k);
            koodiDataType.getMetadata().add(metadataType);
        }

        koodiDataType.setVersio(versio);
        koodiDataType.setLockingVersion(version);

        return koodiDataType;
    }
    
    public static UpdateKoodistoDataType convert(KoodistoVersio from) {
        UpdateKoodistoDataType to = new UpdateKoodistoDataType();
        Koodisto koodisto = from.getKoodisto();
        to.setKoodistoUri(koodisto.getKoodistoUri());
        to.setLukittu(koodisto.getLukittu());
        to.setOmistaja(koodisto.getOmistaja());
        to.setOrganisaatioOid(koodisto.getOrganisaatioOid());
        to.setVoimassaAlkuPvm(DateHelper.DateToXmlCal(from.getVoimassaAlkuPvm()));
        if (from.getVoimassaLoppuPvm() != null) to.setVoimassaLoppuPvm(DateHelper.DateToXmlCal(from.getVoimassaLoppuPvm()));
        KoodistoMetadataToKoodistoMetadataTypeConverter converter = new KoodistoMetadataToKoodistoMetadataTypeConverter();
        for (KoodistoMetadata metaData : from.getMetadatas()) {
            to.getMetadataList().add(converter.convert(metaData));
        }
        to.setVersio(from.getVersio());
        to.setLockingVersion(from.getVersion());
        return to;
    }

    public static void copyFields(KoodistoType from, UpdateKoodistoDataType to) {
        to.setKoodistoUri(from.getKoodistoUri());
        to.setLukittu(from.isLukittu());
        to.setOmistaja(from.getOmistaja());
        to.setOrganisaatioOid(from.getOrganisaatioOid());
        to.setTila(from.getTila());
        to.setVoimassaAlkuPvm(from.getVoimassaAlkuPvm());
        to.setVoimassaLoppuPvm(from.getVoimassaLoppuPvm());

        to.getMetadataList().clear();
        to.getMetadataList().addAll(from.getMetadataList());
        to.setVersio(from.getVersio());
        to.setLockingVersion(from.getLockingVersion());
    }

    public static boolean datePartIsEqual(Date first, Date second) {
        Calendar firstCal = Calendar.getInstance();
        firstCal.setTime(first);

        Calendar secondCal = Calendar.getInstance();
        secondCal.setTime(second);

        return firstCal.get(Calendar.YEAR) == secondCal.get(Calendar.YEAR)
                && firstCal.get(Calendar.MONTH) == secondCal.get(Calendar.MONTH)
                && firstCal.get(Calendar.DATE) == secondCal.get(Calendar.DATE);
    }
}
