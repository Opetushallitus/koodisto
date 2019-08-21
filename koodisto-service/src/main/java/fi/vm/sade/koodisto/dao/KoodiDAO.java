/**
 *
 */
package fi.vm.sade.koodisto.dao;

import java.util.List;

import fi.vm.sade.koodisto.dao.impl.JpaDAO;
import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.Tila;

/**
 * @author tommiha
 */
public interface KoodiDAO extends JpaDAO<Koodi, Long> {
    /**
     * Reads koodi by its unique URI.
     * 
     * @param koodiUri
     * @return
     */
    Koodi readByUri(String koodiUri);

    void delete(String koodiUri);

    boolean koodiUriExists(String koodiUri);

    List<KoodiVersio> getLatestCodeElementVersiosByUrisAndTila(List<String> koodiUris, Tila tila);

    Koodi insertNonFlush(Koodi koodi);
    
    void flush();
}
