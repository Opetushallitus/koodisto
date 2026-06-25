package fi.vm.sade.koodisto.audit;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

public interface Event {

    EntityPersister getEntityPersister();

    SharedSessionContractImplementor getSessionImplementor();

    Object getEntity();

    Object getOldState(int index);

    Object getNewState(int index);

}
