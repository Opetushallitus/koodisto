package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodiMetadata;
import org.springframework.data.repository.CrudRepository;

public interface KoodiMetadataRepository extends CrudRepository<KoodiMetadata, Long>, CustomKoodiMetadataRepository {

    boolean existsByNimiIgnoreCase(String nimi);

    boolean existsByNimiIgnoreCaseAndKoodiVersioKoodiKoodiUriIsNot(String nimi, String koodiUri);

    boolean existsByNimiIgnoreCaseAndKoodiVersioKoodiKoodistoKoodistoUri(String nimi, String koodiUri);

    boolean existsByNimiIgnoreCaseAndKoodiVersioKoodiKoodistoKoodistoUriAndKoodiVersioKoodiKoodiUriIsNot(String nimi,
                                                                                               String koodistoUri,
                                                                                               String koodiUri);

    default boolean existsByNimiAndKoodiUriOtherThan(String nimi, String koodiUri) {
        return existsByNimiIgnoreCaseAndKoodiVersioKoodiKoodiUriIsNot(nimi, koodiUri);
    }

    default boolean existsByNimiAndKoodistoUri(String nimi, String koodistoUri) {
        return existsByNimiIgnoreCaseAndKoodiVersioKoodiKoodistoKoodistoUri(nimi, koodistoUri);
    }

    default boolean existsByNimiAndKoodistoUriAndKoodiUriOtherThan(String nimi, String koodistoUri, String koodiUri) {
        return existsByNimiIgnoreCaseAndKoodiVersioKoodiKoodistoKoodistoUriAndKoodiVersioKoodiKoodiUriIsNot(
                nimi, koodistoUri, koodiUri);
    }

}
