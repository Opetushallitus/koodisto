package fi.vm.sade.koodisto.service.business;

import fi.vm.sade.javautils.opintopolku_spring_security.Authorizer;
import fi.vm.sade.authorization.NotAuthorizedException;

/**
 * @author Eetu Blomqvist
 */
public class MockAuthorizer implements Authorizer {

    @Override
    public void checkUserIsNotSame(String userOid) throws NotAuthorizedException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void checkOrganisationAccess(String targetOrganisationOid, String... roles) throws NotAuthorizedException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
