package fi.vm.sade.koodisto.service.impl.conversion;

import fi.jhs_suositukset.skeemat.oph._2012._05._03.Koodilistaus;
import fi.vm.sade.koodisto.service.types.common.KoodiCollectionType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KoodilistausConverterTest {

    private final KoodilistausConverter converter = new KoodilistausConverter();

    @Test
    public void convertsToKoodilistaus() {
        KoodiCollectionType collection = koodiCollectionType();
        assertEquals(1, collection.getKoodi().size());
        Koodilistaus listaus = converter.koodilistausFrom(collection);
        assertEquals(collection.getKoodi().size(), listaus.getKoodi().size());
        assertEquals(collection.getKoodi().get(0).getTila().value(), listaus.getKoodi().get(0).getTila().value());
        assertEquals(collection.getKoodi().get(0).getKoodiArvo(), listaus.getKoodi().get(0).getKoodiArvo());
    }

    @Test
    public void convertsToKoodiCollection() {
        fi.jhs_suositukset.skeemat.oph._2012._05._03.KoodiType koodiType = new fi.jhs_suositukset.skeemat.oph._2012._05._03.KoodiType();
        koodiType.setTila(fi.jhs_suositukset.skeemat.oph._2012._05._03.TilaType.LUONNOS);
        koodiType.setKoodiUri("koodiuri");
        Koodilistaus listaus = new Koodilistaus();
        listaus.getKoodi().add(koodiType);
        assertEquals(1, listaus.getKoodi().size());
        KoodiCollectionType collection = converter.koodiCollectionFrom(listaus);
        assertEquals(listaus.getKoodi().size(), collection.getKoodi().size());
        assertEquals(listaus.getKoodi().get(0).getTila().value(), collection.getKoodi().get(0).getTila().value());
        assertEquals(listaus.getKoodi().get(0).getKoodiUri(), collection.getKoodi().get(0).getKoodiUri());
    }

    @Test
    public void conversionRoundtripResultIsEqual() {
        KoodiCollectionType original = koodiCollectionType();
        Koodilistaus listaus = converter.koodilistausFrom(original);
        KoodiCollectionType result = converter.koodiCollectionFrom(listaus);
        assertEquals(original.getKoodi().size(), result.getKoodi().size());
        assertEquals(original.getKoodi().get(0).getTila(), result.getKoodi().get(0).getTila());
    }

    private KoodiCollectionType koodiCollectionType() {
        KoodiType koodiType = new KoodiType();
        koodiType.setTila(TilaType.HYVAKSYTTY);
        koodiType.setKoodiArvo("koodiarvo");
        KoodiCollectionType collection = new KoodiCollectionType();
        collection.getKoodi().add(koodiType);
        return collection;
    }
}
