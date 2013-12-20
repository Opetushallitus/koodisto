/**
 *
 */
package fi.vm.sade.koodisto.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.koodisto.dao.KoodistoDAO;
import fi.vm.sade.koodisto.model.Koodisto;

/**
 * @author tommiha
 */
@Repository
public class KoodistoDAOImpl extends AbstractJpaDAOImpl<Koodisto, Long> implements KoodistoDAO {

    @Override
    public Koodisto readByUri(String koodistoUri) {

        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Koodisto> query = cb.createQuery(Koodisto.class);
        Root<Koodisto> root = query.from(Koodisto.class);

        Predicate condition = cb.equal(root.get("koodistoUri"), koodistoUri);

        query.select(root).where(condition);

        return em.createQuery(query).getSingleResult();
    }

    @Override
    public void delete(String koodistoUri) {
        Koodisto koodisto = readByUri(koodistoUri);
        remove(koodisto);
    }

    @Override
    public boolean koodistoUriExists(String koodistoUri) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<Koodisto> k = query.from(Koodisto.class);
        query.select(cb.count(k.<String> get("koodistoUri"))).where(cb.equal(k.get("koodistoUri"), koodistoUri));
        return em.createQuery(query).getSingleResult() > 0;
    }
}
