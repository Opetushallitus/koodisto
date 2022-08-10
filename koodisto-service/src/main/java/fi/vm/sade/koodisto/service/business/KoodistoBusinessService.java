/**
 *
 */
package fi.vm.sade.koodisto.service.business;

import fi.vm.sade.koodisto.dto.FindOrCreateWrapper;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.UpdateKoodistoDataType;

import java.util.List;

public interface KoodistoBusinessService {
    /**
     * Lists all koodisto joukko objects by kieli.
     * 
     * @param kieli
     * @return
     */
    List<KoodistoRyhma> listAllKoodistoRyhmas();

    /**
     * Deletes KoodistoVersio with given id and versio permanently
     * 
     * @param koodistoId
     * @param koodistoVersio
     */
    void delete(String koodistoUri, Integer koodistoVersio);

    /**
     * Creates new version.
     *
     * Takes latest LUONNOS as parameter, makes new locked HYVAKSYTTY version of it,
     * creates new LUONNOS version and returns it. Basically this bypasses the approval
     * process.
     *
     * @param latest koodisto with LUONNOS state
     * @return new ver
     */
    FindOrCreateWrapper<KoodistoVersio> newVersion(KoodistoVersio latest);

    /**
     * Creates new version if latest version is HYVAKSYTTY, otherwise just returns latest version.
     *
     * @param koodistoUri koodisto uri
     * @return latest version
     */
    FindOrCreateWrapper<KoodistoVersio> createNewVersion(String koodistoUri);

    FindOrCreateWrapper<KoodistoVersio> createNewVersion(KoodistoVersio latest);

    List<KoodistoVersio> searchKoodistos(SearchKoodistosCriteriaType searchCriteria);

    KoodistoVersio getLatestKoodistoVersio(String koodistoUri);

    KoodistoVersio getLatestKoodistoVersio(String koodistoUri, boolean initialize);

    KoodistoVersio getKoodistoVersio(String koodistoUri, Integer koodistoVersio);

    KoodistoVersio createKoodisto(List<String> koodistoRyhmaUris, CreateKoodistoDataType createKoodistoData);

    KoodistoVersio updateKoodisto(UpdateKoodistoDataType updateKoodistoData);

    boolean koodistoExists(String koodistoUri);

    boolean koodistoExists(String koodistoUri, Integer koodistoVersio);


    KoodistoRyhma getKoodistoGroup(String koodistoGroupUri);

    Koodisto getKoodistoByKoodistoUri(String koodistoUri);

    void addRelation(String ylaKoodisto, String alaKoodisto, SuhteenTyyppi suhteenTyyppi);
    void removeRelation(String ylakoodistoUri, List<String> alakoodistoUris, SuhteenTyyppi st);

    boolean hasAnyRelation(String koodistoUri, String anotherKoodistoUri);

    KoodistoVersio saveKoodisto(KoodistoDto codesDTO);

}
