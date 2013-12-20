/**
 *
 */
package fi.vm.sade.koodisto.service.business;

import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.types.CreateKoodiDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.UpdateKoodiDataType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.TilaType;

import java.util.List;

/**
 * @author tommiha
 */
public interface KoodiBusinessService {
    /**
     * Creates list of koodi objects to given koodisto.
     * 
     * @param koodistoUri
     * @param koodiList
     */
    void massCreate(String koodistoUri, List<UpdateKoodiDataType> koodiList);

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
     */
    void addRelation(String ylaKoodi, List<String> alaKoodis,
            fi.vm.sade.koodisto.model.SuhteenTyyppi st);

    /**
     * Adds a relation between provided ylaKoodi with its version and alaKoodi
     * with given type.
     * 
     * @return
     */
    void addRelation(String ylaKoodi, String alaKoodi,
                     SuhteenTyyppi st);

    /**
     * Removes relation between provided ylaKoodi and alaKoodis with given type.
     * 
     * @param ylaKoodiId
     * @param alaKoodiIds
     * @param st
     */
    void removeRelation(String ylaKoodi, List<String> alakoodis,
            fi.vm.sade.koodisto.model.SuhteenTyyppi st);

    List<KoodiVersioWithKoodistoItem> searchKoodis(SearchKoodisByKoodistoCriteriaType searchCriteria);

    List<KoodiVersioWithKoodistoItem> searchKoodis(SearchKoodisCriteriaType searchCriteria);

    KoodiVersioWithKoodistoItem createKoodi(String koodistoUri, CreateKoodiDataType createKoodiData);

    KoodiVersioWithKoodistoItem updateKoodi(UpdateKoodiDataType updateKoodiData);

    void delete(String koodiUri, Integer koodiVersio, boolean skipPassiivinenCheck);

    KoodiVersio createNewVersion(String koodiUri, boolean preserveOldRelations);

    void setKoodiTila(String koodiUri, TilaType tila);

    List<KoodiVersioWithKoodistoItem> getKoodisByKoodistoVersio(String koodistoUri, Integer koodistoVersio, boolean onlyValidKoodis);

    List<KoodiVersioWithKoodistoItem> getKoodisByKoodisto(String koodistoUri, boolean onlyValidKoodis);

    List<KoodiVersioWithKoodistoItem> getKoodisByKoodistoWithKoodiArvo(String koodistoUri, String koodiArvo);

    List<KoodiVersioWithKoodistoItem> getKoodisByKoodistoVersioWithKoodiArvo(String koodistoUri,
                                                                             Integer koodistoVersio, String koodiArvo);

    KoodiVersioWithKoodistoItem getKoodiByKoodisto(String koodistoUri, String koodiUri);

    KoodiVersioWithKoodistoItem getKoodiByKoodistoVersio(String koodistoUri, Integer koodistoVersio, String koodiUri);

    List<KoodiVersioWithKoodistoItem> listByRelation(String koodiUri, boolean child, SuhteenTyyppi suhteenTyyppi);

    List<KoodiVersioWithKoodistoItem> listByRelation(String koodiUri, Integer koodiVersio, boolean child, SuhteenTyyppi suhteenTyyppi);
}
