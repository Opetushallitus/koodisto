package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodiMetadata;

import java.util.Set;

public interface KoodiMetadataRepositoryCustom {
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
