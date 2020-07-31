package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.exception.KoodiNotFoundException;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomKoodinSuhdeRepositoryImpl implements CustomKoodinSuhdeRepository {

    private static final String VERSIO = "versio";
    private static final String KOODI_URI = "koodiUri";
    private static final String SEPARATOR = "$%$";

    private final EntityManager entityManager;

    public CustomKoodinSuhdeRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Long getRelationsCount(KoodiUriAndVersioType ylaKoodi, List<KoodiUriAndVersioType> alaKoodis,
                                  SuhteenTyyppi st) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> cquery = cb.createQuery(Long.class);
        Root<KoodinSuhde> root = cquery.from(KoodinSuhde.class);

        Predicate restrictions = addRestrictions(cquery, cb, root, ylaKoodi, alaKoodis, st);
        cquery.select(cb.count(root)).where(restrictions);

        return entityManager.createQuery(cquery).getSingleResult();
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
            throw new KoodiNotFoundException("error.codeelement.relation.list.empty");
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
            throw new KoodiNotFoundException("error.codeelement.relation.list.empty");
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

    private List<KoodinSuhde> privateGetRelations(KoodiUriAndVersioType singleKoodi, List<KoodiUriAndVersioType> multipleKoodis, SuhteenTyyppi st,
                                                  boolean isChild) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<KoodinSuhde> cquery = cb.createQuery(KoodinSuhde.class);
        Root<KoodinSuhde> root = cquery.from(KoodinSuhde.class);

        root.fetch("ylakoodiVersio");
        root.fetch("alakoodiVersio");

        Predicate restrictions = isChild ? addWithinRestrictions(cquery, cb, root, singleKoodi, multipleKoodis, st) : addRestrictions(cquery, cb, root,
                singleKoodi, multipleKoodis, st);
        cquery.select(root).where(restrictions);

        return entityManager.createQuery(cquery).getResultList();
    }
}
