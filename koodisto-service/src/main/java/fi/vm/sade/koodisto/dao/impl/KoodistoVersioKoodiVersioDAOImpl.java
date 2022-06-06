package fi.vm.sade.koodisto.dao.impl;

import fi.vm.sade.koodisto.dao.KoodistoVersioKoodiVersioDAO;
import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class KoodistoVersioKoodiVersioDAOImpl extends AbstractJpaDAOImpl<KoodistoVersioKoodiVersio, Long> implements
        KoodistoVersioKoodiVersioDAO {

    @Override
    public KoodistoVersioKoodiVersio findByKoodistoVersioAndKoodiVersio(Long koodistoVersioId, Long koodiVersioId) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<KoodistoVersioKoodiVersio> query = cb.createQuery(KoodistoVersioKoodiVersio.class);
        Root<KoodistoVersioKoodiVersio> root = query.from(KoodistoVersioKoodiVersio.class);
        Join<KoodistoVersioKoodiVersio, KoodistoVersio> koodistoVersio = root.join("koodistoVersio");
        Join<KoodistoVersioKoodiVersio, KoodiVersio> koodiVersio = root.join("koodiVersio");

        query.select(root).where(
                cb.and(cb.equal(koodistoVersio.get("id"), koodistoVersioId),
                        cb.equal(koodiVersio.get("id"), koodiVersioId)));

        List<KoodistoVersioKoodiVersio> result = em.createQuery(query).getResultList();
        if (result.size() == 0) {
            return null;
        }

        return result.get(0);
    }

    @Override
    public List<KoodistoVersioKoodiVersio> getByKoodiVersio(Long koodiVersioId) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<KoodistoVersioKoodiVersio> query = cb.createQuery(KoodistoVersioKoodiVersio.class);
        Root<KoodistoVersioKoodiVersio> root = query.from(KoodistoVersioKoodiVersio.class);
        Join<KoodistoVersioKoodiVersio, KoodiVersio> koodiVersio = root.join("koodiVersio");

        query.select(root).where(

        cb.equal(koodiVersio.get("id"), koodiVersioId));

        return em.createQuery(query).getResultList();
    }

    @Override
    public List<KoodistoVersioKoodiVersio> getByKoodistoVersio(Long koodistoVersioId) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<KoodistoVersioKoodiVersio> query = cb.createQuery(KoodistoVersioKoodiVersio.class);
        Root<KoodistoVersioKoodiVersio> root = query.from(KoodistoVersioKoodiVersio.class);
        Join<KoodistoVersioKoodiVersio, KoodistoVersio> koodistoVersio = root.join("koodistoVersio");

        query.select(root).where(cb.equal(koodistoVersio.get("id"), koodistoVersioId));

        return em.createQuery(query).getResultList();
    }

    @Override
    public List<KoodistoVersioKoodiVersio> getByKoodistoVersioAndKoodi(Long koodistoVersioId, Long koodiId) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<KoodistoVersioKoodiVersio> query = cb.createQuery(KoodistoVersioKoodiVersio.class);
        Root<KoodistoVersioKoodiVersio> root = query.from(KoodistoVersioKoodiVersio.class);
        Join<KoodistoVersioKoodiVersio, KoodistoVersio> koodistoVersio = root.join("koodistoVersio");
        Join<KoodiVersio, Koodi> koodi = root.join("koodiVersio").join("koodi");

        query.select(root)
                .distinct(true)
                .where(cb.and(cb.equal(koodistoVersio.get("id"), koodistoVersioId), cb.equal(koodi.get("id"), koodiId)));

        return em.createQuery(query).getResultList();
    }

    @Override
    public KoodistoVersioKoodiVersio insertNonFlush(KoodistoVersioKoodiVersio entity) {
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
