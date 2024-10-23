package fi.vm.sade.koodisto.configuration.authorizer;

public interface Authorizer {
    void checkUserIsNotSame(String userOid) throws NotAuthorizedException;

    void checkOrganisationAccess(String targetOrganisationOid, String... roles) throws NotAuthorizedException;
}