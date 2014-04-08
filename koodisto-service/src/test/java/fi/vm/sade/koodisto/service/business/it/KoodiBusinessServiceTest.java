/**
 *
 */
package fi.vm.sade.koodisto.service.business.it;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.types.*;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author tommiha
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {JtaCleanInsertTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class KoodiBusinessServiceTest {

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Autowired
    private KoodistoBusinessService koodistoBusinessService;

    @Test
    public void testCreate() {
        KoodistoVersio koodistoVersio = createKoodisto("omistaja", "organisaatioOid", new Date(), null,
                "uusi luotu testikoodisto");
        KoodiVersioWithKoodistoItem koodi = createKoodi(koodistoVersio, "arvo", "koodi");
        assertNotNull(koodi.getKoodiVersio().getId());
        assertNotNull(koodi.getKoodiVersio().getKoodi());
        assertNotNull(koodi.getKoodiVersio().getKoodi().getId());

        SearchKoodisByKoodistoCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder
                .koodisByKoodistoUri(koodistoVersio.getKoodisto().getKoodistoUri());
        assertEquals(1, koodiBusinessService.searchKoodis(searchCriteria).size());
    }

    @Test
    public void testDeleteVersio() {
        final String koodistoUri = "http://koodisto21";
        final String koodiUri = "477";

        final int numberOfVersiosBeforeDelete = 2;
        final int numberOfVersiosAfterDelete = 1;

        final List<KoodiVersioWithKoodistoItem> before = listByUri(koodiUri);
        assertEquals(numberOfVersiosBeforeDelete, before.size());

        koodiBusinessService.delete(koodiUri, 2);

        KoodistoVersio koodistoVersio = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
        final List<KoodiVersioWithKoodistoItem> afterFirstDelete = listByUri(koodiUri);

        assertEquals(Tila.LUONNOS, koodistoVersio.getTila());
        assertEquals(numberOfVersiosBeforeDelete, afterFirstDelete.size());

        koodiBusinessService.delete(koodiUri, 1, true);

        final List<KoodiVersioWithKoodistoItem> afterSecondDelete = listByUri(koodiUri);
        assertEquals(numberOfVersiosAfterDelete, afterSecondDelete.size());
    }

    @Test
    public void changeTilaFromHyvaksyttyToLuonnos() throws Exception {
    	
    	final String koodistoUri = "http://koodisto19";
    	final Integer koodistoVersioBeforeTilaChange = 1;
    	final Integer koodistoVersioAfterTilaChange = 2;
    	
    	
    	KoodistoVersio beforeUpdate = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
    	assertEquals(koodistoVersioBeforeTilaChange, beforeUpdate.getVersio());
    	assertEquals(Tila.HYVAKSYTTY, beforeUpdate.getTila());
    	
    	List<KoodiVersioWithKoodistoItem> koodiVersioWithKoodistoItems =  koodiBusinessService.getKoodisByKoodisto("http://koodisto19", true);
    	for(KoodiVersioWithKoodistoItem koodiVersioWithKoodistoItem : koodiVersioWithKoodistoItems) {
    		if(Tila.HYVAKSYTTY.equals(koodiVersioWithKoodistoItem.getKoodiVersio().getTila())) {
    			UpdateKoodiDataType updateKoodiDataType = convert(koodiVersioWithKoodistoItem.getKoodiVersio());;
    			updateKoodiDataType.setTila(UpdateKoodiTilaType.LUONNOS);
    			koodiBusinessService.updateKoodi(updateKoodiDataType);
    		}
    	}
    	
    	KoodistoVersio afterUpdate = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
    	assertEquals(Tila.LUONNOS, afterUpdate.getTila());
    	assertEquals(koodistoVersioAfterTilaChange, afterUpdate.getVersio());
    }
    
    private List<KoodiVersioWithKoodistoItem> listByUri(String koodiUri) {
    	SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUri(koodiUri);
    	return koodiBusinessService.searchKoodis(searchType);
    }
    
    private KoodiVersioWithKoodistoItem createKoodi(KoodistoVersio koodisto, String koodiArvo, String nimi) {
        CreateKoodiDataType createKoodiData = fi.vm.sade.koodisto.service.it.DataUtils.createCreateKoodiDataType(koodiArvo,
                new Date(), null, nimi);

        KoodiVersioWithKoodistoItem koodi = koodiBusinessService.createKoodi(koodisto.getKoodisto().getKoodistoUri(), createKoodiData);
        return koodi;
    }

    private KoodistoVersio createKoodisto(String omistaja, String organisaatioOid, Date voimassaAlkuPvm, Date voimassaLoppuPvm, String nimi) {
        List<KoodistoRyhma> ryhmas = koodistoBusinessService.listAllKoodistoRyhmas();
        List<String> ryhmaUris = new ArrayList<String>();
        ryhmaUris.add(ryhmas.get(0).getKoodistoRyhmaUri());

        CreateKoodistoDataType createKoodistoDataType = fi.vm.sade.koodisto.service.it.DataUtils.createCreateKoodistoDataType(
                omistaja, organisaatioOid, voimassaAlkuPvm, voimassaLoppuPvm, nimi);
        KoodistoVersio koodisto = koodistoBusinessService.createKoodisto(ryhmaUris, createKoodistoDataType);
        return koodisto;
    }

    private static UpdateKoodiDataType convert(KoodiVersio koodiVersio) throws Exception {

        UpdateKoodiDataType updateKoodiDataType = new UpdateKoodiDataType();

        updateKoodiDataType.setKoodiArvo(koodiVersio.getKoodiarvo());
        updateKoodiDataType.setKoodiUri(koodiVersio.getKoodi().getKoodiUri());
        //updateKoodiDataType.setTila(UpdateKoodiTilaType.valueOf(koodiVersio.getTila().name()));
        updateKoodiDataType.setVoimassaAlkuPvm(convert(koodiVersio.getVoimassaAlkuPvm()));
        updateKoodiDataType.setVoimassaLoppuPvm(convert(koodiVersio.getVoimassaLoppuPvm()));
        updateKoodiDataType.getMetadata().clear();
        updateKoodiDataType.getMetadata().addAll(convert(koodiVersio.getMetadatas()));
        updateKoodiDataType.setVersio(koodiVersio.getVersio());
        updateKoodiDataType.setLockingVersion(koodiVersio.getVersion());

        return updateKoodiDataType;
    }

    private static XMLGregorianCalendar convert(Date date) throws Exception {

        if(date == null) return null;

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);

        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);

    }

    private static Set<KoodiMetadataType> convert(Set<KoodiMetadata> koodiMetadataSet) {

        Set<KoodiMetadataType> koodiMetadataTypeSet = new HashSet<KoodiMetadataType>();

        for(KoodiMetadata koodiMetadata : koodiMetadataSet) {

            KoodiMetadataType koodiMetadataType = new KoodiMetadataType();

            koodiMetadataType.setKieli(KieliType.valueOf(koodiMetadata.getKieli().name()));
            koodiMetadataType.setNimi(koodiMetadata.getNimi());
            koodiMetadataType.setLyhytNimi(koodiMetadata.getLyhytNimi());
            koodiMetadataType.setKuvaus(koodiMetadata.getKuvaus());

            koodiMetadataType.setKayttoohje(koodiMetadata.getKayttoohje());
            koodiMetadataType.setKasite(koodiMetadata.getKasite());
            koodiMetadataType.setHuomioitavaKoodi(koodiMetadata.getHuomioitavaKoodi());
            koodiMetadataType.setSisaltaaMerkityksen(koodiMetadata.getSisaltaaMerkityksen());
            koodiMetadataType.setEiSisallaMerkitysta(koodiMetadata.getEiSisallaMerkitysta());
            koodiMetadataType.setSisaltaaKoodiston(koodiMetadata.getSisaltaaKoodiston());

            koodiMetadataTypeSet.add(koodiMetadataType);
        }

        return koodiMetadataTypeSet;
    }

}
