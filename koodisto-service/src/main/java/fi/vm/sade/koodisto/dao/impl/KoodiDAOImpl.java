/**
 *
 */
package fi.vm.sade.koodisto.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.koodisto.dao.KoodiDAO;
import fi.vm.sade.koodisto.dao.KoodistoVersioKoodiVersioDAO;
import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;
import fi.vm.sade.koodisto.model.Tila;

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
    
    @Override
    public List<KoodiVersio> getLatestCodeElementVersiosByUrisAndTila(List<String> koodiUris, Tila tila) {

        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        
        CriteriaQuery<KoodiVersio> cquery = cb.createQuery(KoodiVersio.class);
        Root<KoodiVersio> versio = cquery.from(KoodiVersio.class);
        Join<Koodi, KoodiVersio> join = versio.join("koodi", JoinType.LEFT);

        Predicate uriRestriction = join.<String> get("koodiUri").in(koodiUris);
        Predicate tilaRestriction = cb.equal(versio.get("tila"), Tila.LUONNOS);
        
        Predicate restrictions = cb.and(uriRestriction, tilaRestriction);
        cquery.distinct(true);
        
        cquery.select(versio).where(restrictions);
        List<KoodiVersio> results = em.createQuery(cquery).getResultList();

        return results;

    }

    @Override
    public Koodi insertNonFlush(Koodi entity) {
        validate(entity);
        EntityManager em = getEntityManager();
        em.persist(entity);
        // Database must be synchronized after this by flushing
        return entity;
    }

    @Override
    public void flush() {
        getEntityManager().flush();
    }
}
