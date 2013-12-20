/**
 * 
 */
package fi.vm.sade.koodisto.ui.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.vm.sade.koodisto.service.types.common.KoodistoRyhmaListType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;

/**
 * @author tommiha
 * 
 */
public interface KoodistoUiService {

    /**
     * Creates new koodisto.
     * 
     * @param koodistoDTO
     * @param selectedKoodistoRyhma
     * @return
     */
    KoodistoType create(KoodistoType koodistoDTO, KoodistoRyhmaListType selectedKoodistoRyhma);

    /**
     * Updates existing koodisto.
     * 
     * @param koodistoDTO
     * @return
     */
    KoodistoType update(KoodistoType koodistoDTO);

    /**
     * Lists all KoodistoJoukkos
     * 
     * @param locale
     * @return
     */
    List<KoodistoRyhmaListType> listKoodistoRyhmas();

    /**
     * Get Koodisto by id
     * 
     * @param id
     * @return
     */
    KoodistoType getKoodistoByUri(String koodistoUri);


    KoodistoType getKoodistoByUriIfAvailable(String koodistoUri);

    /**
     * Get Koodisto by id and version
     * 
     * @param id
     * @param version
     * @return
     */
    KoodistoType getKoodistoByUriAndVersion(String koodistoUri, Integer version);

    /**
     * Delete koodisto
     * 
     * @param koodisto
     */
    void delete(KoodistoType koodisto);

    List<KoodistoType> getKoodistosByUris(Set<String> koodistoUris);

    Map<String, KoodistoType> getKoodistosByUrisMap(Set<String> koodistoUris);

}
