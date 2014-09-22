package fi.vm.sade.koodisto.test.support;

import java.util.Date;

import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistonSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.test.support.builder.KoodiBuilder;
import fi.vm.sade.koodisto.test.support.builder.KoodiMetadataBuilder;
import fi.vm.sade.koodisto.test.support.builder.KoodiVersioBuilder;
import fi.vm.sade.koodisto.test.support.builder.KoodinSuhdeBuilder;
import fi.vm.sade.koodisto.test.support.builder.KoodistoVersioBuilder;
import fi.vm.sade.koodisto.test.support.builder.KoodistonSuhdeBuilder;

public class DtoFactory {

    public static final String KOODISTO_DESCRIPTION = "kuvaus";
    public static final String KOODISTO_NAME = "koodisto";
    public static final KoodiMetadata KOODI_METADATA = new KoodiMetadataBuilder().setKieli(Kieli.FI).setNimi("Name").setLyhytNimi("Short")
            .setKuvaus("Description").build();
    
    public static KoodiVersioBuilder createKoodiVersioWithUriAndVersio(String uri, Integer versio) {
        return createKoodiVersioWithUriAndVersioWithoutMetadatas(uri, versio).addMetadata(KOODI_METADATA);
    }
    
    public static KoodiVersioBuilder createKoodiVersioWithUriAndVersioWithoutMetadatas(String uri, Integer versio) {
        Koodisto koodisto = new Koodisto();
        Koodi koodi = new KoodiBuilder().setKoodiUri(uri).setKoodisto(koodisto).build();
        KoodistoVersio kv = createKoodistoVersio(koodisto, versio).build();
        return new KoodiVersioBuilder().setKoodiVersio(versio).addKoodistoVersio(kv).setKoodi(koodi).setStartDate(new Date()).setKoodiValue("value").setTila(Tila.HYVAKSYTTY);
    }
    
    public static KoodiVersioBuilder createKoodiVersioWithoutMetadatasWithStartAndEndDates(String uri, Integer versio, Date startDate, Date endDate) {
        return createKoodiVersioWithUriAndVersioWithoutMetadatas(uri, versio).setStartDate(startDate).setEndDate(endDate);
    }

    public static KoodiVersio createKoodiVersioWithUriAndVersioAndRelation(String uri, Integer versio, KoodiVersio koodiVersio, SuhteenTyyppi tyyppi) {
        KoodinSuhde relation = new KoodinSuhdeBuilder().setParentVersio(koodiVersio).setSuhteenTyyppi(tyyppi).build();
        return createKoodiVersioWithUriAndVersio(uri, versio).addParentRelation(relation).build();
    }
    
    public static KoodistoVersioBuilder createKoodistoVersio(Koodisto koodisto, Integer versio) {
        return new KoodistoVersioBuilder().addMetadata(Kieli.FI, KOODISTO_NAME, KOODISTO_DESCRIPTION).setVersio(versio).setKoodisto(koodisto).setTila(Tila.HYVAKSYTTY);
    }
    
    public static KoodiMetadata createKoodiMetadata(String name, String shortName, String description, Kieli language) {
        return new KoodiMetadataBuilder().setKieli(language).setNimi(name).setLyhytNimi(shortName).setKuvaus(description).build();
    }
    
    public static KoodinSuhde createKoodinSuhde(SuhteenTyyppi tyyppi, KoodiVersio child, KoodiVersio parent, boolean parentPassive, boolean childPassive) {
        return new KoodinSuhdeBuilder().setSuhteenTyyppi(tyyppi).setChildVersio(child).setParentVersio(parent).setParentPassive(parentPassive)
                .setChildPassive(childPassive).build();
    }

    public static KoodistoVersioBuilder createKoodistoVersioWithMetadata(Koodisto koodisto, Integer versio, String name, String description, Kieli kieli) {
        return new KoodistoVersioBuilder().setVersio(versio).setKoodisto(koodisto).setTila(Tila.HYVAKSYTTY).addMetadata(kieli, name, description);
    }

    public static KoodistoVersioBuilder createKoodistoVersioWithMetadata(Koodisto koodisto, Integer versio, KoodistoMetadata ... datas) {
        return new KoodistoVersioBuilder().setVersio(versio).setTila(Tila.HYVAKSYTTY).setKoodisto(koodisto).setMetadatas(datas);
    }

    public static KoodistoVersioBuilder createKoodistoVersioWithStartAndEndDates(Koodisto koodisto, int versio, Date startDate, Date endDate) {
        return createKoodistoVersio(koodisto, versio).setStartDate(startDate).setEndDate(endDate);
    }

    public static KoodistoVersioBuilder createKoodistoVersioWithRelations(Koodisto koodisto, int versio, KoodistonSuhde ... relations) {
        KoodistoVersioBuilder builder = createKoodistoVersio(koodisto, versio);
        for(KoodistonSuhde ks : relations){
            if(ks.getAlakoodistoVersio() == null) {
                builder.addParentRelation(ks);
            } else {
                builder.addChildRelation(ks);
            }
        }
        return builder;
    }

    public static KoodistonSuhdeBuilder createKoodistonSuhde(SuhteenTyyppi tyyppi, KoodistoVersio child, KoodistoVersio parent, boolean parentPassive,
            boolean childPassive) {
        return new KoodistonSuhdeBuilder().setSuhteenTyyppi(tyyppi).setChildVersio(child).setParentVersio(parent)
                .setParentPassive(parentPassive).setChildPassive(childPassive);
    }

    public static KoodistoVersioBuilder createKoodistoVersioWithKoodiVersios(int versio, String koodistoUri, KoodiVersio[] koodiVersios) {
        return new KoodistoVersioBuilder().setVersio(versio).setKoodisto(createKoodisto(koodistoUri));
    }
    
    public static Koodisto createKoodisto(String koodistoUri) {
        Koodisto koodisto = new Koodisto();
        koodisto.setKoodistoUri(koodistoUri);
        return koodisto;
    }

}
