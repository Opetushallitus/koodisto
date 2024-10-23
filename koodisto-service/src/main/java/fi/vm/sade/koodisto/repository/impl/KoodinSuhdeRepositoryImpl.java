/**
 *
 */
package fi.vm.sade.koodisto.repository.impl;

import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.repository.KoodinSuhdeRepositoryCustom;
import fi.vm.sade.koodisto.service.business.exception.KoodiNotFoundException;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class KoodinSuhdeRepositoryImpl implements KoodinSuhdeRepositoryCustom {

    private static final String VERSIO = "versio";
    private static final String KOODI_URI = "koodiUri";
    private static final String SEPARATOR = "$%$";

    private static final String KOODI = "koodi";
    private static final String YLAKOODIVERSIO = "ylakoodiVersio";
    private static final String ALAKOODIVERSIO = "alakoodiVersio";
    private static final String SUHTEENTYYPPI = "suhteenTyyppi";

    @Autowired
    EntityManager em;

    @Override
    public Long getRelationsCount(KoodiUriAndVersioType ylaKoodi, List<KoodiUriAndVersioType> alaKoodis,
            SuhteenTyyppi st) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> cquery = cb.createQuery(Long.class);
        Root<KoodinSuhde> root = cquery.from(KoodinSuhde.class);

        Predicate restrictions = addRestrictions(cquery, cb, root, ylaKoodi, alaKoodis, st);
        cquery.select(cb.count(root)).where(restrictions);

        return em.createQuery(cquery).getSingleResult();
    }

    private static Predicate addRestrictions(CriteriaQuery<?> cquery, CriteriaBuilder cb, Root<KoodinSuhde> root, KoodiUriAndVersioType ylaKoodi,
            List<KoodiUriAndVersioType> alaKoodis, SuhteenTyyppi st) {

        Join<KoodinSuhde, KoodiVersio> ylakoodiVersioJoin = root.join(YLAKOODIVERSIO);
        Join<KoodinSuhde, KoodiVersio> alakoodiVersioJoin = root.join(ALAKOODIVERSIO);
        Join<KoodiVersio, Koodi> alakoodiJoin = alakoodiVersioJoin.join(KOODI);
        Join<KoodiVersio, Koodi> ylakoodiJoin = ylakoodiVersioJoin.join(KOODI);

        Predicate suhteenTyyppiRestriction = cb.equal(root.get(SUHTEENTYYPPI), st);

        ArrayList<String> concatenatedAlaList = new ArrayList<>();
        for (KoodiUriAndVersioType ak : alaKoodis) {
            concatenatedAlaList.add(ak.getKoodiUri() + SEPARATOR + ak.getVersio());
        }
        if (concatenatedAlaList.isEmpty()) {
            throw new KoodiNotFoundException("error.codeelement.relation.list.empty");
        }
        Predicate concatenatedAlakoodiUriAndVersioRestriction =
                cb.concat(cb.concat(alakoodiJoin.get(KOODI_URI), SEPARATOR), alakoodiVersioJoin.get(VERSIO))
                        .in(concatenatedAlaList);

        Predicate ylakoodiRestriction = cb.and(
                cb.equal(ylakoodiJoin.get(KOODI_URI), ylaKoodi.getKoodiUri()),
                cb.equal(ylakoodiVersioJoin.get(VERSIO), ylaKoodi.getVersio()));

        cquery.distinct(true);

        if (st == SuhteenTyyppi.RINNASTEINEN) {

            Predicate ylaAndAla = cb.and(
                    ylakoodiRestriction,
                    concatenatedAlakoodiUriAndVersioRestriction);

            ArrayList<String> concatenatedYlaList = new ArrayList<>();
            for (KoodiUriAndVersioType yk : alaKoodis) {
                concatenatedYlaList.add(yk.getKoodiUri() + SEPARATOR + yk.getVersio());
            }
            Predicate concatenatedYlakoodiUriAndVersioRestriction =
                    cb.concat(cb.concat(ylakoodiJoin.get(KOODI_URI), SEPARATOR), ylakoodiVersioJoin.get(VERSIO))
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

        Join<KoodinSuhde, KoodiVersio> ylakoodiVersioJoin = root.join(YLAKOODIVERSIO);
        Join<KoodinSuhde, KoodiVersio> alakoodiVersioJoin = root.join(ALAKOODIVERSIO);
        Join<KoodiVersio, Koodi> alakoodiJoin = alakoodiVersioJoin.join(KOODI);
        Join<KoodiVersio, Koodi> ylakoodiJoin = ylakoodiVersioJoin.join(KOODI);

        Predicate suhteenTyyppiRestriction = cb.equal(root.get(SUHTEENTYYPPI), st);

        ArrayList<String> concatenatedYlaList = new ArrayList<>();
        for (KoodiUriAndVersioType ak : ylaKoodis) {
            concatenatedYlaList.add(ak.getKoodiUri() + SEPARATOR + ak.getVersio());
        }
        if (concatenatedYlaList.isEmpty()) {
            throw new KoodiNotFoundException("error.codeelement.relation.list.empty");
        }
        Predicate concatenatedAlakoodiUriAndVersioRestriction =
                cb.concat(cb.concat(ylakoodiJoin.get(KOODI_URI), SEPARATOR), ylakoodiVersioJoin.get(VERSIO))
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

    private List<KoodinSuhde> privateGetRelations(KoodiUriAndVersioType singleKoodi, List<KoodiUriAndVersioType> multipleKoodis, SuhteenTyyppi st,
            boolean isChild) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<KoodinSuhde> cquery = cb.createQuery(KoodinSuhde.class);
        Root<KoodinSuhde> root = cquery.from(KoodinSuhde.class);

        root.fetch(YLAKOODIVERSIO);
        root.fetch(ALAKOODIVERSIO);

        Predicate restrictions = isChild ? addWithinRestrictions(cquery, cb, root, singleKoodi, multipleKoodis, st) : addRestrictions(cquery, cb, root,
                singleKoodi, multipleKoodis, st);
        cquery.select(root).where(restrictions);

        return em.createQuery(cquery).getResultList();
    }

    @Override
    public void massRemove(List<KoodinSuhde> entityList) {
        if (entityList.isEmpty())
            throw new IllegalArgumentException("EntityList was empty.");
        ArrayList<Long> idList = new ArrayList<>();
        for (KoodinSuhde entity : entityList) {
            idList.add(entity.getId());
        }
        em.createQuery("delete from KoodinSuhde k where k.id in (:idList)").setParameter("idList", idList).executeUpdate();
    }

    @Override
    public List<KoodinSuhde> getRelations(String ylakoodiUri) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<KoodinSuhde> cquery = cb.createQuery(KoodinSuhde.class);
        Root<KoodinSuhde> root = cquery.from(KoodinSuhde.class);
        Join<KoodinSuhde, KoodiVersio> ylakoodiVersio = root.join(YLAKOODIVERSIO, JoinType.LEFT);
        Join<Object, Object> ylakoodi = ylakoodiVersio.join(KOODI, JoinType.LEFT);

        cquery.select(root).where(cb.equal(ylakoodi.get(KOODI_URI), ylakoodiUri));

        return em.createQuery(cquery).getResultList();
    }
}
