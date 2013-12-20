/**
 *
 */
package fi.vm.sade.koodisto.dao;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.koodisto.model.KoodiMetadata;

/**
 * @author tommiha
 */
public interface KoodiMetadataDAO extends JpaDAO<KoodiMetadata, Long> {

    boolean nimiExistsForSomeOtherKoodi(String koodiUri, String nimi);

    boolean nimiExists(String nimi);

    boolean nimiExistsInKoodisto(String koodistoUri, String nimi);

    boolean nimiExistsInKoodistoForSomeOtherKoodi(String koodistoUri, String koodiUri, String nimi);
}
