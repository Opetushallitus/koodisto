/**
 *
 */
package fi.vm.sade.koodisto.dao.impl;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.koodisto.dao.KoodiDAO;
import fi.vm.sade.koodisto.dao.KoodistoVersioKoodiVersioDAO;
import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author tommiha
 */
@Repository
public class KoodiDAOImpl extends AbstractJpaDAOImpl<Koodi, Long> implements KoodiDAO {

    @Autowired
    private KoodistoVersioKoodiVersioDAO koodistoVersioKoodiVersioDAO;

    private void delete(Koodi k) {
        for (KoodiVersio kv : k.getKoodiVersios()) {
            for (KoodistoVersioKoodiVersio relation : kv.getKoodistoVersios()) {
                koodistoVersioKoodiVersioDAO.remove(relation);
            }

        }
        remove(read(k.getId()));
    }

    @Override
    public Koodi readByUri(String koodiUri) {

        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Koodi> query = cb.createQuery(Koodi.class);
        Root<Koodi> root = query.from(Koodi.class);

        Predicate uriCondition = cb.equal(root.<String>get("koodiUri"), koodiUri);

        query.select(root).where(uriCondition);

        return em.createQuery(query).getSingleResult();
    }

    @Override
    public void delete(String koodiUri) {
        delete(readByUri(koodiUri));
    }

    @Override
    public boolean koodiUriExists(String koodiUri) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<Koodi> k = query.from(Koodi.class);
        query.select(cb.count(k.<String>get("koodiUri"))).where(cb.equal(k.get("koodiUri"), koodiUri));
        return em.createQuery(query).getSingleResult() > 0;
    }
}
