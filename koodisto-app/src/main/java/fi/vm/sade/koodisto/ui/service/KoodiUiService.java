/**
 * 
 */
package fi.vm.sade.koodisto.ui.service;

import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.koodisto.ui.koodisto.Encoding;
import fi.vm.sade.koodisto.ui.koodisto.Format;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * @author tommiha
 * 
 */
public interface KoodiUiService {

    /**
     * Lists all koodi objects by parent koodisto.
     * 
     * @param koodistoId
     * @param version
     *            TODO
     * @return
     */
    List<KoodiType> listKoodisByKoodisto(String koodistoUri, Integer version);

    /**
     * Creates koodi
     * 
     * @param koodi
     * @return TODO
     */
    KoodiType create(KoodistoType koodistoDTO, KoodiType koodi);

    /**
     * Updates koodi
     * 
     * @param koodi
     * @return
     */
    KoodiType update(KoodiType koodi);

    /**
     * Download koodi objects as CSV data.
     * 
     * 
     * @param koodistoVersion
     * @param format
     * @param encoding
     * @return
     * @throws IOException
     */
    InputStream download(String koodistoUri, Integer koodistoVersion, Format format, String encoding) throws IOException;

    /**
     * Returns a single KoodiType with ALL metainformation (all languages)
     * 
     * @param Id
     */
    KoodiType getKoodiByUri(String koodiUri);

    /**
     * Returns a single {@link KoodiType} with ALL metainformation by ID and
     * version
     * 
     * @param id
     * @param versio
     * @return
     */
    KoodiType getKoodiByUriAndVersio(String koodiUri, Integer versio);

    /***
     * 
     * @param koodiUri
     * @param koodiVersio
     * @param isChild
     * @param suhteenTyyppi
     * @return
     */
    List<KoodiType> listKoodiByRelation(String koodiUri, Integer koodiVersio, Boolean isChild, SuhteenTyyppiType suhteenTyyppi);

    /**
     * Deletes koodi object
     * 
     * @param koodi
     */
    void delete(KoodiType koodi);

    /**
     * Adds relation between {@link KoodiType}s
     * 
     * @param ylaKoodiId
     * @param version
     * @param alaKoodiIds
     * @param suhteenTyyppi
     */
    void addRelation(KoodiUriAndVersioType ylakoodi, Set<KoodiUriAndVersioType> alakoodis, SuhteenTyyppiType suhteenTyyppi);

    void addRelation(KoodiUriAndVersioType ylakoodi, KoodiUriAndVersioType alakoodi, SuhteenTyyppiType suhteenTyyppi);

    void addRelation(KoodiUriAndVersioType ylaKoodi, List<KoodiUriAndVersioType> alakoodis, SuhteenTyyppiType suhteenTyyppi);

    /**
     * Remove relation between {@link KoodiType}s
     * 
     * @param ylaKoodiId
     * @param version
     * @param alaKoodiIds
     * @param suhteenTyyppi
     */
    void removeRelation(KoodiUriAndVersioType ylaKoodi, Set<KoodiUriAndVersioType> alakoodis, SuhteenTyyppiType suhteenTyyppi);

    void removeRelation(KoodiUriAndVersioType ylakoodi, List<KoodiUriAndVersioType> alakoodis, SuhteenTyyppiType suhteenTyyppi);

    void removeRelation(KoodiUriAndVersioType ylakoodi, KoodiUriAndVersioType alakoodi, SuhteenTyyppiType suhteenTyyppi);

    /**
     * Returns all versios of the given koodi
     * 
     * @param koodiId
     * @return
     */
    List<KoodiType> listAllKoodiVersiosByUri(String koodiUri);

    /**
     * Upload koodisto data for koodisto given as id, in {@link Format} format
     * and {@link Encoding} encoding
     * 
     */
    void upload(InputStream csvData, String koodistoUri, Format format, Encoding encoding);

    List<KoodiType> listKoodisByKoodisto(Set<String> selectedKoodiUris, String koodistoUri, Integer versio);
}