/**
 *
 */
package fi.vm.sade.koodisto.dao;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.koodisto.model.Koodi;

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
}
