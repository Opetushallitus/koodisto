package fi.vm.sade.koodisto.repository;

public interface KoodistoRepositoryCustom {
    boolean koodistoUriExists(String koodistoUri);

    void flush();
}
