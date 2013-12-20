/**
 *
 */
package fi.vm.sade.koodisto.dao.impl;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.koodisto.dao.KoodinSuhdeDAO;
import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tommiha
 */
@Repository
public class KoodinSuhdeDAOImpl extends AbstractJpaDAOImpl<KoodinSuhde, Long> implements KoodinSuhdeDAO {

    private static final String VERSIO = "versio";
    private static final String KOODI_URI = "koodiUri";

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

    private static Predicate addRestrictions(CriteriaQuery<?> cquery, CriteriaBuilder cb, Root<KoodinSuhde> root,
                                             KoodiUriAndVersioType ylaKoodi,
                                             List<KoodiUriAndVersioType> alaKoodis,
                                             SuhteenTyyppi st) {

        Join<KoodinSuhde, KoodiVersio> ylakoodiVersioJoin = root.join("ylakoodiVersio");
        Join<KoodinSuhde, KoodiVersio> alakoodiVersioJoin = root.join("alakoodiVersio");
        Join<KoodiVersio, Koodi> alakoodiJoin = alakoodiVersioJoin.join("koodi");
        Join<KoodiVersio, Koodi> ylakoodiJoin = ylakoodiVersioJoin.join("koodi");

        Predicate suhteenTyyppiRestriction = cb.equal(root.get("suhteenTyyppi"), st);

        List<Predicate> alakoodiRestrictions = new ArrayList<Predicate>();
        for (KoodiUriAndVersioType ak : alaKoodis) {
            alakoodiRestrictions.add(cb.and(cb.equal(alakoodiJoin.<String>get(KOODI_URI), ak.getKoodiUri()),
                    cb.equal(alakoodiVersioJoin.get(VERSIO), ak.getVersio())));
        }

        Predicate ylakoodiRestriction = cb.and(cb.equal(ylakoodiJoin.get(KOODI_URI),
                ylaKoodi.getKoodiUri()), cb.equal(ylakoodiVersioJoin.get(VERSIO), ylaKoodi.getVersio()));

        cquery.distinct(true);

        Predicate and = null;
        if (st == SuhteenTyyppi.RINNASTEINEN) {

            Predicate ylaAndAla = cb.and(ylakoodiRestriction,
                    cb.or(alakoodiRestrictions.toArray(new Predicate[alakoodiRestrictions.size()])));

            List<Predicate> ylakoodiRestrictions = new ArrayList<Predicate>();
            for (KoodiUriAndVersioType ak : alaKoodis) {
                ylakoodiRestrictions.add(cb.and(cb.equal(ylakoodiJoin.<String>get(KOODI_URI),
                        ak.getKoodiUri()), cb.equal(ylakoodiVersioJoin.get(VERSIO), ak.getVersio())));
            }

            Predicate alakoodiRestriction = cb.and(cb.equal(alakoodiJoin.<String>get(KOODI_URI),
                    ylaKoodi.getKoodiUri()), cb.equal(alakoodiVersioJoin.get(VERSIO), ylaKoodi.getVersio()));

            Predicate alaAndYla = cb.and(alakoodiRestriction,
                    cb.or(ylakoodiRestrictions.toArray(new Predicate[ylakoodiRestrictions.size()])));

            Predicate or = cb.or(ylaAndAla, alaAndYla);
            and = cb.and(suhteenTyyppiRestriction, or);

        } else {
            and = cb.and(ylakoodiRestriction,
                    cb.or(alakoodiRestrictions.toArray(new Predicate[alakoodiRestrictions.size()])),
                    suhteenTyyppiRestriction);
        }

        return and;
    }

    @Override
    public List<KoodinSuhde> getRelations(KoodiUriAndVersioType ylaKoodi, List<KoodiUriAndVersioType> alaKoodis,
                                          SuhteenTyyppi st) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<KoodinSuhde> cquery = cb.createQuery(KoodinSuhde.class);
        Root<KoodinSuhde> root = cquery.from(KoodinSuhde.class);
        
        root.fetch("ylakoodiVersio");
        root.fetch("alakoodiVersio");
        
        Predicate restrictions = addRestrictions(cquery, cb, root, ylaKoodi, alaKoodis, st);
        cquery.select(root).where(restrictions);

        return em.createQuery(cquery).getResultList();
    }

    @Override
    public void remove(KoodinSuhde entity) {
        EntityManager em = getEntityManager();

        // FIXME: This is kinda ugly but it works
        em.createQuery("delete from KoodinSuhde k where k.id = :id").setParameter("id", entity.getId()).executeUpdate();
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
}
