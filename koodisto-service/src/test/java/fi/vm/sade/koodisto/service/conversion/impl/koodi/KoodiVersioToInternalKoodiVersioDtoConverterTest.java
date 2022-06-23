package fi.vm.sade.koodisto.service.conversion.impl.koodi;

import fi.vm.sade.koodisto.dto.internal.InternalKoodiSuhdeDto;
import fi.vm.sade.koodisto.dto.internal.InternalKoodiVersioDto;
import fi.vm.sade.koodisto.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KoodiVersioToInternalKoodiVersioDtoConverterTest {
    KoodiVersio a;
    KoodiVersioToInternalKoodiVersioDtoConverter resource =
            new KoodiVersioToInternalKoodiVersioDtoConverter(
                    new KoodiMetadataToKoodiMetadataDtoConverter()
            );

    @BeforeEach
    void setUp() {
        a = new KoodiVersio();
        KoodistoVersioKoodiVersio koodistoVersioKoodiVersio = new KoodistoVersioKoodiVersio();
        KoodistoVersio koodistoVersio = new KoodistoVersio();
        koodistoVersio.setVersio(1);
        Koodisto koodisto = new Koodisto();
        koodisto.setKoodistoUri("koodistoUri");
        koodistoVersio.setKoodisto(koodisto);
        koodistoVersioKoodiVersio.setKoodistoVersio(koodistoVersio);
        koodisto.addKoodistoVersion(koodistoVersio);
        a.addKoodistoVersio(koodistoVersioKoodiVersio);
        Koodi koodi = new Koodi();
        koodi.setKoodisto(koodisto);
        a.setKoodi(koodi);
    }

    @Test
    @DisplayName("Test not null conversion")
    void convert1() {
        InternalKoodiVersioDto b = resource.convert(a);
        assertNotNull(b);
    }

    @Test
    @DisplayName("Test koodistoUri conversion")
    void convert2() {
        InternalKoodiVersioDto b = resource.convert(a);
        assert b != null;
        assertEquals("koodistoUri", b.getKoodistoUri());
    }

    @Test
    @DisplayName("Test koodistoUri conversion with two koodisto versions")
    void convert3() {
        KoodistoVersioKoodiVersio koodistoVersioKoodiVersio = new KoodistoVersioKoodiVersio();
        KoodistoVersio koodistoVersio = new KoodistoVersio();
        koodistoVersio.setVersio(2);
        Koodisto koodisto = new Koodisto();
        koodisto.setKoodistoUri("koodistoUri 2");
        koodistoVersio.setKoodisto(koodisto);
        koodisto.addKoodistoVersion(koodistoVersio);
        koodistoVersioKoodiVersio.setKoodistoVersio(koodistoVersio);
        a.addKoodistoVersio(koodistoVersioKoodiVersio);
        InternalKoodiVersioDto b = resource.convert(a);
        assert b != null;
        assertEquals("koodistoUri 2", b.getKoodistoUri());
    }

    @Test
    @DisplayName("Test koodistoUri conversion koodiArvo")
    void convert4() {
        a.setKoodiarvo("koodiArvo");
        InternalKoodiVersioDto b = resource.convert(a);
        assert b != null;
        assertEquals("koodiArvo", b.getKoodiArvo());
    }

    @Test
    @DisplayName("Test koodistoUri conversion rinnastuuKoodeihin")
    void convert5() {
        KoodinSuhde koodinSuhde = new KoodinSuhde();
        koodinSuhde.setSuhteenTyyppi(SuhteenTyyppi.RINNASTEINEN);
        KoodiVersio koodiVersio = new KoodiVersio();
        koodinSuhde.setYlakoodiVersio(koodiVersio);
        Koodi relatedKoodi = new Koodi();
        relatedKoodi.setKoodiUri("relatedKoodiUri");
        koodiVersio.setKoodiarvo("related");
        koodiVersio.setKoodi(relatedKoodi);
        KoodiMetadata metadata = new KoodiMetadata();
        metadata.setKieli(Kieli.FI);
        metadata.setNimi("fi-nimi");
        koodiVersio.addMetadata(metadata);
        Koodisto koodisto = new Koodisto();
        koodisto.setKoodistoUri("koodistoUri related");
        relatedKoodi.setKoodisto(koodisto);
        KoodistoVersio koodistoVersio = new KoodistoVersio();
        koodistoVersio.setKoodisto(koodisto);
        koodisto.addKoodistoVersion(koodistoVersio);
        koodinSuhde.setAlakoodiVersio(a);
        a.addYlakoodi(koodinSuhde);
        InternalKoodiVersioDto b = resource.convert(a);
        assert b != null;
        InternalKoodiSuhdeDto a1 = b.getRinnastuuKoodeihin().get(0);
        assertAll(
                () -> assertEquals("relatedKoodiUri", a1.getKoodiUri()),
                () -> assertEquals("fi-nimi", a1.getNimi().get("fi"))
        );
    }

}