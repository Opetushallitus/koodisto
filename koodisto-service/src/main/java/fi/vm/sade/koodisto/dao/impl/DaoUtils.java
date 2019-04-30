package fi.vm.sade.koodisto.dao.impl;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;

public final class DaoUtils {

    private DaoUtils() {
    }

    public static <T>Optional<T> optional(TypedQuery<T> query) {
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

}
