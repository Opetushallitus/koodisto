/**
 *
 */
package fi.vm.sade.koodisto.service.business;

import java.io.File;
import java.util.List;
import java.util.Optional;

import fi.vm.sade.koodisto.dto.FindOrCreateWrapper;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.model.Format;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.UpdateKoodistoDataType;

/**
 * @author tommiha
 */
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
     * Creates new version if latest version is HYVAKSYTTY, otherwise just returns latest version.
     *
     * @param koodistoUri koodisto uri
     * @return latest version
     */
    FindOrCreateWrapper<KoodistoVersio> createNewVersion(String koodistoUri);

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

    File downloadFile(String codesUri, int codesVersion, Format fileFormat, String encoding);

    KoodistoVersio saveKoodisto(KoodistoDto codesDTO);

}
