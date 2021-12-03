package fi.vm.sade.koodisto.dao;

import fi.vm.sade.koodisto.dao.impl.JpaDAO;
import fi.vm.sade.koodisto.model.KoodiMetadata;

import java.util.Set;

public interface KoodiMetadataDAO extends JpaDAO<KoodiMetadata, Long> {

    boolean nimiExistsForSomeOtherKoodi(String koodiUri, String nimi);

    boolean nimiExistsInKoodisto(String koodistoUri, String nimi);

    /**
     * Initialises metadata for ExtendedKoodistoDto requirements
     *
     * @param koodiVersioIdSet koodi versio ids
     * @see KoodiMetadata @NamedEntityGraph koodiMetadataWithKoodiVersio for details
     */
    void initializeByKoodiVersioIds(Set<Long> koodiVersioIdSet);
}
