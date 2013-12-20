package fi.vm.sade.koodisto.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.koodisto.dao.KoodistoRyhmaDAO;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.model.KoodistoVersio;

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
}