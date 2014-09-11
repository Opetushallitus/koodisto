/**
 *
 */
package fi.vm.sade.koodisto.service.business;

import java.util.List;

import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.KoodiRelaatioListaDto;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.types.CreateKoodiDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.UpdateKoodiDataType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.TilaType;

/**
 * @author tommiha
 */
public interface KoodiBusinessService {
    /**
     * Creates list of koodi objects to given koodisto.
     * 
     * @param koodistoUri
     * @param koodiList
     * @return 
     */
    KoodistoVersio massCreate(String koodistoUri, List<UpdateKoodiDataType> koodiList);

    /**
     * Lists koodis by the relation to given koodi.
     * 
     * @param koodiId
     * @param suhdeTyyppi
     * @return
     */
    List<KoodiVersioWithKoodistoItem> listByRelation(KoodiUriAndVersioType koodi, SuhteenTyyppi suhdeTyyppi,
            Boolean isChild);

    /**
     * Deletes given code version permanently
     * 
     * @param koodiId
     * @param koodiVersio
     */
    void delete(String koodiUri, Integer koodiVersio);

    /**
     * adds a relation between provided ylaKoodi with its version and alaKoodis
     * with given type.
     * 
     * @param ylaKoodiId
     * @param version
     * @param alaKoodiIds
     * @param suhteenTyyppi
     * @param isChild 
     */
    void addRelation(String ylaKoodi, List<String> alaKoodis, SuhteenTyyppi st, boolean isChild);

    void addRelation(KoodiRelaatioListaDto koodiRelaatioDto);

    /**
     * Removes relation between provided codeElement and relations with given type.
     * @param st type of relation
     * @param isChild is provided codeElementUri child or parent
     * @param codeElementUri
     * @param relatedCodeElements
     */
    void removeRelation(String codeElementUri, List<String> relatedCodeElements, SuhteenTyyppi st, boolean isChild);

    void removeRelation(KoodiRelaatioListaDto koodiRelaatioDto);

    List<KoodiVersioWithKoodistoItem> searchKoodis(SearchKoodisByKoodistoCriteriaType searchCriteria);

    List<KoodiVersioWithKoodistoItem> searchKoodis(SearchKoodisCriteriaType searchCriteria);

    KoodiVersioWithKoodistoItem createKoodi(String koodistoUri, CreateKoodiDataType createKoodiData);

    KoodiVersioWithKoodistoItem updateKoodi(UpdateKoodiDataType updateKoodiData);

    void delete(String koodiUri, Integer koodiVersio, boolean skipPassiivinenCheck);

    KoodiVersio createNewVersion(String koodiUri);

    void setKoodiTila(String koodiUri, TilaType tila);

    void setKoodiTila(KoodiVersio latest, TilaType tila);

    List<KoodiVersioWithKoodistoItem> getKoodisByKoodistoVersio(String koodistoUri, Integer koodistoVersio, boolean onlyValidKoodis);

    List<KoodiVersioWithKoodistoItem> getKoodisByKoodisto(String koodistoUri, boolean onlyValidKoodis);

    List<KoodiVersioWithKoodistoItem> getKoodisByKoodistoWithKoodiArvo(String koodistoUri, String koodiArvo);

    List<KoodiVersioWithKoodistoItem> getKoodisByKoodistoVersioWithKoodiArvo(String koodistoUri,
                                                                             Integer koodistoVersio, String koodiArvo);

    KoodiVersioWithKoodistoItem getKoodiByKoodisto(String koodistoUri, String koodiUri);

    KoodiVersioWithKoodistoItem getKoodiByKoodistoVersio(String koodistoUri, Integer koodistoVersio, String koodiUri);

    List<KoodiVersioWithKoodistoItem> listByRelation(String koodiUri, boolean child, SuhteenTyyppi suhteenTyyppi);

    List<KoodiVersioWithKoodistoItem> listByRelation(String koodiUri, Integer koodiVersio, boolean child, SuhteenTyyppi suhteenTyyppi);

    KoodiVersio getLatestKoodiVersio(String koodiUri);

    boolean hasRelationBetweenCodeElements(KoodiVersio ylaKoodiVersio, final KoodiVersio alaKoodiVersio);
    
    boolean isLatestKoodiVersio(String koodiUri, Integer versio);

    KoodiVersio saveKoodi(ExtendedKoodiDto koodiDTO);

}
