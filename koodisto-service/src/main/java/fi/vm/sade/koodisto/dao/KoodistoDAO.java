/**
 *
 */
package fi.vm.sade.koodisto.dao;

import fi.vm.sade.koodisto.dao.impl.JpaDAO;
import fi.vm.sade.koodisto.model.Koodisto;

/**
 * @author tommiha
 */
public interface KoodistoDAO extends JpaDAO<Koodisto, Long> {
    /**
     * Reads koodisto by its URI.
     * 
     * @param koodistoUri
     * @return
     */
    Koodisto readByUri(String koodistoUri);

    void delete(String koodistoUri);

    boolean koodistoUriExists(String koodistoUri);

    void flush();
}
