package fi.vm.sade.koodisto.service.business;

public interface UserDetailService {
    /**
     * Hakee kirjautuneen käyttäjän oidin.
     * @return Henkilö oid
     */
    String getCurrentUserOid();
}
