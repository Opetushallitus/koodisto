package fi.vm.sade.koodisto.dao.impl;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.koodisto.dao.KoodistoRyhmaDAO;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class KoodistoRyhmaDAOImpl extends AbstractJpaDAOImpl<KoodistoRyhma, Long> implements KoodistoRyhmaDAO {

    @Override
    public List<KoodistoRyhma> listAllKoodistoRyhmas() {

        EntityManager em = getEntityManager();
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<KoodistoRyhma> c = qb.createQuery(KoodistoRyhma.class);
        Root<KoodistoRyhma> p = c.from(KoodistoRyhma.class);

        p.fetch("koodistoRyhmaMetadatas", JoinType.INNER);
        Fetch<KoodistoRyhma, Koodisto> join = p.fetch("koodistos", JoinType.LEFT);
        Fetch<Koodisto, KoodistoVersio> join2 = join.fetch("koodistoVersios", JoinType.LEFT);
        join2.fetch("metadatas", JoinType.LEFT);
        c.select(p);
        c.distinct(true);

        return em.createQuery(c).getResultList();

    }


    @Override
    public KoodistoRyhma read(String ryhmaUri) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<KoodistoRyhma> query = cb.createQuery(KoodistoRyhma.class);
        Root<KoodistoRyhma> root = query.from(KoodistoRyhma.class);

        Predicate condition = cb.equal(root.get("koodistoRyhmaUri"), ryhmaUri);

        query.select(root).where(condition);

        return em.createQuery(query).getSingleResult();
    }

    @Override
    public List<KoodistoRyhma> findByUri(List<String> koodistoRyhmaUris) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<KoodistoRyhma> c = cb.createQuery(KoodistoRyhma.class);
        
        Root<KoodistoRyhma> p = c.from(KoodistoRyhma.class);
        p.fetch("koodistoRyhmaMetadatas", JoinType.LEFT);
        Fetch<KoodistoRyhma, Koodisto> koodisto = p.fetch("koodistos", JoinType.LEFT);
        Fetch<Koodisto, KoodistoVersio> koodistoVersio = koodisto.fetch("koodistoVersios", JoinType.LEFT);
        koodistoVersio.fetch("metadatas", JoinType.LEFT);

        List<Predicate> restrictions = new ArrayList<Predicate>();
        for (String koodistoRyhmaUri : koodistoRyhmaUris) {
            restrictions.add(cb.equal(p.get("koodistoRyhmaUri"), koodistoRyhmaUri));
        }

        c.select(p).where(cb.or(restrictions.toArray(new Predicate[restrictions.size()])));
        c.distinct(true);

        return em.createQuery(c).getResultList();
    }

    @Override
    public KoodistoRyhma findById(Long id) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<KoodistoRyhma> c = cb.createQuery(KoodistoRyhma.class);

        Root<KoodistoRyhma> p = c.from(KoodistoRyhma.class);
        p.fetch("koodistoRyhmaMetadatas", JoinType.LEFT);
        Fetch<KoodistoRyhma, Koodisto> koodisto = p.fetch("koodistos", JoinType.LEFT);
        Fetch<Koodisto, KoodistoVersio> koodistoVersio = koodisto.fetch("koodistoVersios", JoinType.LEFT);
        koodistoVersio.fetch("metadatas", JoinType.LEFT);

        List<Predicate> restrictions = new ArrayList<Predicate>();
        restrictions.add(cb.equal(p.get("id"), id));

        c.select(p).where(cb.or(restrictions.toArray(new Predicate[restrictions.size()])));
        c.distinct(true);

        return em.createQuery(c).getSingleResult();
    }

    @Override
    public boolean koodistoRyhmaUriExists(String ryhmaUri) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<KoodistoRyhma> root = query.from(KoodistoRyhma.class);

        Predicate condition = cb.equal(root.get("koodistoRyhmaUri"), ryhmaUri);

        query.select(cb.count(root.<String> get("koodistoRyhmaUri"))).where(condition);

        return em.createQuery(query).getSingleResult() > 0;
    }
}