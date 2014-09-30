package fi.vm.sade.koodisto.dao.flushmode;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.hibernate.FlushMode;
import org.hibernate.Session;

@SuppressWarnings("serial")
public class HibernateJpaDialect extends org.springframework.orm.jpa.vendor.HibernateJpaDialect {
    private FlushMode flushMode;
    
    public String getFlushMode() {
        return flushMode!=null ? flushMode.toString() : null; 
    }

    public void setFlushMode(String aFlushMode) {
        flushMode = FlushMode.valueOf(aFlushMode);
        if (aFlushMode != null && flushMode == null) {
            throw new IllegalArgumentException (aFlushMode+" value invalid. See class org.hibernate.FlushMode for valid values");
        }
    }

    @Override
    public Object prepareTransaction(EntityManager entityManager, boolean readOnly, String name)
            throws PersistenceException {
        Session session = getSession(entityManager);
        FlushMode currentFlushMode = session.getFlushMode();
        FlushMode previousFlushMode = null;
        if (getFlushMode() != null) {
            session.setFlushMode(flushMode);
            previousFlushMode = currentFlushMode;
        } else if (readOnly) {
            // We should suppress flushing for a read-only transaction.
            session.setFlushMode(FlushMode.MANUAL);
            previousFlushMode = currentFlushMode;
        }
        else {
            // We need AUTO or COMMIT for a non-read-only transaction.
            if (currentFlushMode.lessThan(FlushMode.COMMIT)) {
                session.setFlushMode(FlushMode.AUTO);
                previousFlushMode = currentFlushMode;
            }
        }
        return new SessionTransactionData(session, previousFlushMode);
    }

    @Override
    public void cleanupTransaction(Object transactionData) {
        ((SessionTransactionData) transactionData).resetFlushMode();
    }

    private static class SessionTransactionData {

        private final Session session;

        private final FlushMode previousFlushMode;

        public SessionTransactionData(Session session, FlushMode previousFlushMode) {
            this.session = session;
            this.previousFlushMode = previousFlushMode;
        }

        public void resetFlushMode() {
            if (this.previousFlushMode != null) {
                this.session.setFlushMode(this.previousFlushMode);
            }
        }
    }
}