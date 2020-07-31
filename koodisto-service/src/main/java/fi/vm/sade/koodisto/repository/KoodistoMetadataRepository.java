package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoMetadata;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface KoodistoMetadataRepository extends CrudRepository<KoodistoMetadata, Long> {

    List<KoodistoMetadata> findByKoodistoVersioKoodistoKoodistoUri(String koodistoUri);

    boolean existsByNimi(String nimi);

    boolean existsByKoodistoVersioKoodistoKoodistoUriIsNotAndNimi(String koodistoUri, String nimi);

    default List<KoodistoMetadata> findByKoodistoUri(String koodistoUri) {
        return findByKoodistoVersioKoodistoKoodistoUri(koodistoUri);
    }

    default boolean existsByKoodistoUriOtherThanAndNimi(String koodistoUri, String nimi) {
        return existsByKoodistoVersioKoodistoKoodistoUriIsNotAndNimi(koodistoUri, nimi);
    }

}
