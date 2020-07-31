package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodiMetadata;

import java.util.Set;

public interface CustomKoodiMetadataRepository {

    /**
     * Initializes metadata for ExtendedKoodistoDto requirements
     * @see KoodiMetadata @NamedEntityGraph koodiMetadataWithKoodiVersio for details
     * @param koodiVersioIdSet koodi versio ids
     */
    void initializeByKoodiVersioIds(Set<Long> koodiVersioIdSet);

}
