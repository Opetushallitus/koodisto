package fi.vm.sade.koodisto.service.business.impl;

import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.service.types.*;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import fi.vm.sade.koodisto.service.types.common.TilaType;

public final class EntityUtils {

    private EntityUtils() {

    }

    public static void copyFields(KoodiMetadataType from, KoodiMetadata to) {
        to.setKieli(Kieli.valueOf(from.getKieli().name()));
        to.setNimi(from.getNimi());
        to.setLyhytNimi(from.getLyhytNimi());
        to.setKuvaus(from.getKuvaus());

        to.setKayttoohje(from.getKayttoohje());
        to.setKasite(from.getKasite());
        to.setHuomioitavaKoodi(from.getHuomioitavaKoodi());
        to.setSisaltaaMerkityksen(from.getSisaltaaMerkityksen());
        to.setEiSisallaMerkitysta(from.getEiSisallaMerkitysta());
        to.setSisaltaaKoodiston(from.getSisaltaaKoodiston());
    }

    public static void copyFields(KoodistoMetadataType from, KoodistoMetadata to) {
        to.setHuomioitavaKoodisto(from.getHuomioitavaKoodisto());
        to.setKasite(from.getKasite());
        to.setKayttoohje(from.getKayttoohje());
        to.setKieli(Kieli.valueOf(from.getKieli().name()));
        to.setKohdealue(from.getKohdealue());
        to.setKohdealueenOsaAlue(from.getKohdealueenOsaAlue());
        to.setKoodistonLahde(from.getKoodistonLahde());
        to.setKuvaus(from.getKuvaus());
        to.setNimi(from.getNimi());
        to.setSitovuustaso(from.getSitovuustaso());
        to.setTarkentaaKoodistoa(from.getTarkentaaKoodistoa());
        to.setToimintaymparisto(from.getToimintaymparisto());
    }

    public static void copyFields(CreateKoodiDataType from, KoodiVersio to) {
        to.setKoodiarvo(from.getKoodiArvo());
        to.setVoimassaAlkuPvm(from.getVoimassaAlkuPvm() != null ? DateHelper.xmlCalToDate(from.getVoimassaAlkuPvm())
                : null);
        to.setVoimassaLoppuPvm(from.getVoimassaLoppuPvm() != null ? DateHelper.xmlCalToDate(from.getVoimassaLoppuPvm())
                : null);
    }

    public static void copyFields(CreateKoodistoDataType from, KoodistoVersio to) {
        to.setVoimassaAlkuPvm(from.getVoimassaAlkuPvm() != null ? DateHelper.xmlCalToDate(from.getVoimassaAlkuPvm())
                : null);
        to.setVoimassaLoppuPvm(from.getVoimassaLoppuPvm() != null ? DateHelper.xmlCalToDate(from.getVoimassaLoppuPvm())
                : null);
    }

    public static void copyFields(CreateKoodistoDataType from, Koodisto to) {
        to.setLukittu(from.isLukittu());
        to.setOmistaja(from.getOmistaja());
        to.setOrganisaatioOid(from.getOrganisaatioOid());
    }

    public static void copyFields(KoodistoVersio from, KoodistoVersio to) {
        to.setPaivitysPvm(from.getPaivitysPvm());
        to.setTila(from.getTila());
        to.setVersio(from.getVersio());
        to.setVoimassaAlkuPvm(from.getVoimassaAlkuPvm());
        to.setVoimassaLoppuPvm(from.getVoimassaLoppuPvm());
    }

    public static void copyFields(KoodistoMetadata from, KoodistoMetadata to) {
        to.setHuomioitavaKoodisto(from.getHuomioitavaKoodisto());
        to.setKasite(from.getKasite());
        to.setKayttoohje(from.getKayttoohje());
        to.setKieli(from.getKieli());
        to.setKohdealue(from.getKohdealue());
        to.setKohdealueenOsaAlue(from.getKohdealueenOsaAlue());
        to.setKoodistonLahde(from.getKoodistonLahde());
        to.setKuvaus(from.getKuvaus());
        to.setNimi(from.getNimi());
        to.setSitovuustaso(from.getSitovuustaso());
        to.setTarkentaaKoodistoa(from.getTarkentaaKoodistoa());
        to.setToimintaymparisto(from.getToimintaymparisto());
    }

    public static void copyFields(UpdateKoodiDataType from, KoodiVersio to) {
        to.setKoodiarvo(from.getKoodiArvo());
        if (from.getTila() != null) {
            to.setTila(Tila.valueOf(from.getTila().name()));
        }
        to.setVoimassaAlkuPvm(from.getVoimassaAlkuPvm() != null ? DateHelper.xmlCalToDate(from.getVoimassaAlkuPvm())
                : null);
        to.setVoimassaLoppuPvm(from.getVoimassaLoppuPvm() != null ? DateHelper.xmlCalToDate(from.getVoimassaLoppuPvm())
                : null);
    }

    public static void copyFields(UpdateKoodistoDataType from, KoodistoVersio to) {
        to.setTila(Tila.valueOf(from.getTila().name()));
        to.setVoimassaAlkuPvm(from.getVoimassaAlkuPvm() != null ? DateHelper.xmlCalToDate(from.getVoimassaAlkuPvm())
                : null);
        to.setVoimassaLoppuPvm(from.getVoimassaLoppuPvm() != null ? DateHelper.xmlCalToDate(from.getVoimassaLoppuPvm())
                : null);
    }

    public static void copyFields(UpdateKoodistoDataType from, Koodisto to) {
        to.setLukittu(from.isLukittu());
        to.setOmistaja(from.getOmistaja());
        to.setOrganisaatioOid(from.getOrganisaatioOid());
    }

    public static void copyFields(KoodiVersio from, KoodiVersio to) {
        to.setPaivitysPvm(from.getPaivitysPvm());
        to.setTila(from.getTila());
        to.setKoodiarvo(from.getKoodiarvo());
        to.setVoimassaAlkuPvm(from.getVoimassaAlkuPvm());
        to.setVoimassaLoppuPvm(from.getVoimassaLoppuPvm());
    }

    public static void copyFields(KoodiMetadata from, KoodiMetadata to) {
        to.setKieli(from.getKieli());
        to.setNimi(from.getNimi());
        to.setLyhytNimi(from.getLyhytNimi());
        to.setKuvaus(from.getKuvaus());

        to.setKayttoohje(from.getKayttoohje());
        to.setKasite(from.getKasite());
        to.setHuomioitavaKoodi(from.getHuomioitavaKoodi());
        to.setSisaltaaMerkityksen(from.getSisaltaaMerkityksen());
        to.setEiSisallaMerkitysta(from.getEiSisallaMerkitysta());
        to.setSisaltaaKoodiston(from.getSisaltaaKoodiston());
    }

    public static void copyFields(UpdateKoodiDataType from, CreateKoodiDataType to) {
        to.setKoodiArvo(from.getKoodiArvo());
        to.setVoimassaAlkuPvm(from.getVoimassaAlkuPvm());
        to.setVoimassaLoppuPvm(from.getVoimassaLoppuPvm());

        to.getMetadata().addAll(from.getMetadata());
    }

    public static void copyFields(KoodiType from, UpdateKoodiDataType to) {
        to.setKoodiArvo(from.getKoodiArvo());
        to.setKoodiUri(from.getKoodiUri());

        if (from.getTila() != null && !TilaType.HYVAKSYTTY.equals(from.getTila())) {
            to.setTila(UpdateKoodiTilaType.valueOf(from.getTila().name()));
        }

        to.setVoimassaAlkuPvm(from.getVoimassaAlkuPvm());
        to.setVoimassaLoppuPvm(from.getVoimassaLoppuPvm());
    }
}
