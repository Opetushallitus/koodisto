package fi.vm.sade.koodisto.repository;

public interface KoodistoMetadataRepositoryCustom {
    boolean nimiExistsForSomeOtherKoodisto(String koodistoUri, String nimi);

    boolean nimiExists(String nimi);
}
