/**
 *
 */
package fi.vm.sade.koodisto.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.koodisto.dao.KoodinSuhdeDAO;
import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.exception.KoodiNotFoundException;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;

/**
 * @author tommiha
 */
@Repository
public class KoodinSuhdeDAOImpl extends AbstractJpaDAOImpl<KoodinSuhde, Long> implements KoodinSuhdeDAO {

    private static final String VERSIO = "versio";
    private static final String KOODI_URI = "koodiUri";
    private static final String SEPARATOR = "$%$";

    @Override
    public Long getRelationsCount(KoodiUriAndVersioType ylaKoodi, List<KoodiUriAndVersioType> alaKoodis,
            SuhteenTyyppi st) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> cquery = cb.createQuery(Long.class);
        Root<KoodinSuhde> root = cquery.from(KoodinSuhde.class);

        Predicate restrictions = addRestrictions(cquery, cb, root, ylaKoodi, alaKoodis, st);
        cquery.select(cb.count(root)).where(restrictions);

        return em.createQuery(cquery).getSingleResult();
    }

    private static Predicate addRestrictions(CriteriaQuery<?> cquery, CriteriaBuilder cb, Root<KoodinSuhde> root, KoodiUriAndVersioType ylaKoodi,
            List<KoodiUriAndVersioType> alaKoodis, SuhteenTyyppi st) {

        Join<KoodinSuhde, KoodiVersio> ylakoodiVersioJoin = root.join("ylakoodiVersio");
        Join<KoodinSuhde, KoodiVersio> alakoodiVersioJoin = root.join("alakoodiVersio");
        Join<KoodiVersio, Koodi> alakoodiJoin = alakoodiVersioJoin.join("koodi");
        Join<KoodiVersio, Koodi> ylakoodiJoin = ylakoodiVersioJoin.join("koodi");

        Predicate suhteenTyyppiRestriction = cb.equal(root.get("suhteenTyyppi"), st);

        ArrayList<String> concatenatedAlaList = new ArrayList<String>();
        for (KoodiUriAndVersioType ak : alaKoodis) {
            concatenatedAlaList.add(ak.getKoodiUri() + SEPARATOR + ak.getVersio());
        }
        if (concatenatedAlaList.isEmpty()) {
            throw new KoodiNotFoundException("Alakoodi list was empty");
        }
        Predicate concatenatedAlakoodiUriAndVersioRestriction =
                cb.concat(alakoodiJoin.<String> get(KOODI_URI), cb.concat(SEPARATOR, alakoodiVersioJoin.<String> get(VERSIO)))
                        .in(concatenatedAlaList);

        Predicate ylakoodiRestriction = cb.and(
                cb.equal(ylakoodiJoin.get(KOODI_URI), ylaKoodi.getKoodiUri()),
                cb.equal(ylakoodiVersioJoin.get(VERSIO), ylaKoodi.getVersio()));

        cquery.distinct(true);

        if (st == SuhteenTyyppi.RINNASTEINEN) {

            Predicate ylaAndAla = cb.and(
                    ylakoodiRestriction,
                    concatenatedAlakoodiUriAndVersioRestriction);

            ArrayList<String> concatenatedYlaList = new ArrayList<String>();
            for (KoodiUriAndVersioType yk : alaKoodis) {
                concatenatedYlaList.add(yk.getKoodiUri() + SEPARATOR + yk.getVersio());
            }
            Predicate concatenatedYlakoodiUriAndVersioRestriction =
                    cb.concat(ylakoodiJoin.<String> get(KOODI_URI), cb.concat(SEPARATOR, ylakoodiVersioJoin.<String> get(VERSIO)))
                            .in(concatenatedYlaList);

            Predicate alakoodiRestriction = cb.and(
                    cb.equal(alakoodiJoin.get(KOODI_URI), ylaKoodi.getKoodiUri()),
                    cb.equal(alakoodiVersioJoin.get(VERSIO), ylaKoodi.getVersio()));

            Predicate alaAndYla = cb.and(
                    alakoodiRestriction,
                    concatenatedYlakoodiUriAndVersioRestriction);

            Predicate or = cb.or(ylaAndAla, alaAndYla);
            return cb.and(suhteenTyyppiRestriction, or);

        } else {
            return cb.and(suhteenTyyppiRestriction,
                    concatenatedAlakoodiUriAndVersioRestriction,
                    ylakoodiRestriction);
        }
    }
    
    private static Predicate addWithinRestrictions(CriteriaQuery<?> cquery, CriteriaBuilder cb, Root<KoodinSuhde> root, KoodiUriAndVersioType alaKoodi,
            List<KoodiUriAndVersioType> ylaKoodis, SuhteenTyyppi st) {

        Join<KoodinSuhde, KoodiVersio> ylakoodiVersioJoin = root.join("ylakoodiVersio");
        Join<KoodinSuhde, KoodiVersio> alakoodiVersioJoin = root.join("alakoodiVersio");
        Join<KoodiVersio, Koodi> alakoodiJoin = alakoodiVersioJoin.join("koodi");
        Join<KoodiVersio, Koodi> ylakoodiJoin = ylakoodiVersioJoin.join("koodi");

        Predicate suhteenTyyppiRestriction = cb.equal(root.get("suhteenTyyppi"), st);

        ArrayList<String> concatenatedYlaList = new ArrayList<String>();
        for (KoodiUriAndVersioType ak : ylaKoodis) {
            concatenatedYlaList.add(ak.getKoodiUri() + SEPARATOR + ak.getVersio());
        }
        if (concatenatedYlaList.isEmpty()) {
            throw new IllegalArgumentException("Ylakoodi list was empty");
        }
        Predicate concatenatedAlakoodiUriAndVersioRestriction =
                cb.concat(ylakoodiJoin.<String> get(KOODI_URI), cb.concat(SEPARATOR, ylakoodiVersioJoin.<String> get(VERSIO)))
                        .in(concatenatedYlaList);

        Predicate ylakoodiRestriction = cb.and(
                cb.equal(alakoodiJoin.get(KOODI_URI), alaKoodi.getKoodiUri()),
                cb.equal(alakoodiVersioJoin.get(VERSIO), alaKoodi.getVersio()));

        cquery.distinct(true);

        return cb.and(suhteenTyyppiRestriction,
                concatenatedAlakoodiUriAndVersioRestriction,
                ylakoodiRestriction);

    }

    @Override
    public List<KoodinSuhde> getRelations(KoodiUriAndVersioType ylaKoodi, List<KoodiUriAndVersioType> alaKoodis,
            SuhteenTyyppi st) {
        return privateGetRelations(ylaKoodi, alaKoodis, st, false);
    }
    
    @Override
    public List<KoodinSuhde> getWithinRelations(KoodiUriAndVersioType ylaKoodi, List<KoodiUriAndVersioType> alaKoodis,
            SuhteenTyyppi st) {
        return privateGetRelations(ylaKoodi, alaKoodis, st, true);
    }

    private List<KoodinSuhde> privateGetRelations(KoodiUriAndVersioType singleKoodi, List<KoodiUriAndVersioType> multipleKoodis, SuhteenTyyppi st, boolean isChild) {

        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<KoodinSuhde> cquery = cb.createQuery(KoodinSuhde.class);
        Root<KoodinSuhde> root = cquery.from(KoodinSuhde.class);

        root.fetch("ylakoodiVersio");
        root.fetch("alakoodiVersio");

        Predicate restrictions = null;
        if(!isChild){
            restrictions = addRestrictions(cquery, cb, root, singleKoodi, multipleKoodis, st);
        } else {
            restrictions = addWithinRestrictions(cquery, cb, root, singleKoodi, multipleKoodis, st);
        }
        cquery.select(root).where(restrictions);

        List<KoodinSuhde> results = em.createQuery(cquery).getResultList();

        return results;
    }
    
    @Override
    public void remove(KoodinSuhde entity) {
        EntityManager em = getEntityManager();

        // FIXME: This is kinda ugly but it works
        em.createQuery("delete from KoodinSuhde k where k.id = :id").setParameter("id", entity.getId()).executeUpdate();
    }

    public void massRemove(List<KoodinSuhde> entityList) {
        if (entityList.isEmpty())
            throw new IllegalArgumentException("EntityList was empty.");
        ArrayList<Long> idList = new ArrayList<Long>();
        for (KoodinSuhde entity : entityList) {
            idList.add(entity.getId());
        }
        EntityManager em = getEntityManager();
        em.createQuery("delete from KoodinSuhde k where k.id in (:idList)").setParameter("idList", idList).executeUpdate();
    }

    @Override
    public List<KoodinSuhde> getRelations(String ylakoodiUri) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<KoodinSuhde> cquery = cb.createQuery(KoodinSuhde.class);
        Root<KoodinSuhde> root = cquery.from(KoodinSuhde.class);
        Join<KoodinSuhde, KoodiVersio> ylakoodiVersio = root.join("ylakoodiVersio", JoinType.LEFT);
        Join<Object, Object> ylakoodi = ylakoodiVersio.join("koodi", JoinType.LEFT);

        cquery.select(root).where(cb.equal(ylakoodi.get(KOODI_URI), ylakoodiUri));

        return em.createQuery(cquery).getResultList();
    }

    public KoodinSuhde insertNonFlush(KoodinSuhde entity) {
        validate(entity);
        EntityManager entityManager = getEntityManager();
        entityManager.persist(entity);
        // Database must be synchronized at after this by flushing!
        return entity;
    }

    public void flush() {
        EntityManager entityManager = getEntityManager();
        entityManager.flush();
    }
}
