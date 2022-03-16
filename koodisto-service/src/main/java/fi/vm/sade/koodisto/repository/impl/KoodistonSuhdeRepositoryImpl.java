/**
 *
 */
package fi.vm.sade.koodisto.repository.impl;

import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistonSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.repository.KoodiRepository;
import fi.vm.sade.koodisto.repository.KoodistonSuhdeRepository;
import fi.vm.sade.koodisto.repository.KoodistonSuhdeRepositoryCustom;
import fi.vm.sade.koodisto.service.types.common.KoodistoUriAndVersioType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KoodistonSuhdeRepositoryImpl implements KoodistonSuhdeRepositoryCustom {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String VERSIO = "versio";
    private static final String KOODISTO_URI = "koodistoUri";

    @Autowired
    EntityManager em;

    @Autowired
    @Lazy
    private KoodistonSuhdeRepository koodistonSuhdeRepository;

    private static Predicate addRestrictions(CriteriaQuery<?> cquery, CriteriaBuilder cb, Root<KoodistonSuhde> root,
            KoodistoUriAndVersioType ylaKoodisto,
            List<KoodistoUriAndVersioType> alaKoodistos,
            SuhteenTyyppi st) {

        Join<KoodistonSuhde, KoodistoVersio> ylakoodistoVersioJoin = root.join("ylakoodistoVersio");
        Join<KoodistonSuhde, KoodistoVersio> alakoodistoVersioJoin = root.join("alakoodistoVersio");
        Join<KoodistoVersio, Koodisto> alakoodistoJoin = alakoodistoVersioJoin.join("koodisto");
        Join<KoodistoVersio, Koodisto> ylakoodistoJoin = ylakoodistoVersioJoin.join("koodisto");

        Predicate suhteenTyyppiRestriction = cb.equal(root.get("suhteenTyyppi"), st);

        List<Predicate> alakoodistoRestrictions = new ArrayList<Predicate>();
        for (KoodistoUriAndVersioType ak : alaKoodistos) {
            alakoodistoRestrictions.add(cb.and(cb.equal(alakoodistoJoin.<String> get(KOODISTO_URI), ak.getKoodistoUri()),
                    cb.equal(alakoodistoVersioJoin.get(VERSIO), ak.getVersio())));
        }

        Predicate ylakoodistoRestriction = cb.and(cb.equal(ylakoodistoJoin.get(KOODISTO_URI),
                ylaKoodisto.getKoodistoUri()), cb.equal(ylakoodistoVersioJoin.get(VERSIO), ylaKoodisto.getVersio()));

        cquery.distinct(true);

        Predicate and = null;
        if (st == SuhteenTyyppi.RINNASTEINEN) {

            Predicate ylaAndAla = cb.and(ylakoodistoRestriction,
                    cb.or(alakoodistoRestrictions.toArray(new Predicate[alakoodistoRestrictions.size()])));

            List<Predicate> ylakoodiRestrictions = new ArrayList<Predicate>();
            for (KoodistoUriAndVersioType ak : alaKoodistos) {
                ylakoodiRestrictions.add(cb.and(cb.equal(ylakoodistoJoin.<String> get(KOODISTO_URI),
                        ak.getKoodistoUri()), cb.equal(ylakoodistoVersioJoin.get(VERSIO), ak.getVersio())));
            }

            Predicate alakoodistoRestriction = cb.and(cb.equal(alakoodistoJoin.<String> get(KOODISTO_URI),
                    ylaKoodisto.getKoodistoUri()), cb.equal(alakoodistoVersioJoin.get(VERSIO), ylaKoodisto.getVersio()));

            Predicate alaAndYla = cb.and(alakoodistoRestriction,
                    cb.or(ylakoodiRestrictions.toArray(new Predicate[ylakoodiRestrictions.size()])));

            Predicate or = cb.or(ylaAndAla, alaAndYla);
            and = cb.and(suhteenTyyppiRestriction, or);

        } else {
            and = cb.and(ylakoodistoRestriction,
                    cb.or(alakoodistoRestrictions.toArray(new Predicate[alakoodistoRestrictions.size()])),
                    suhteenTyyppiRestriction);
        }

        return and;
    }

    @Override
    public List<KoodistonSuhde> getRelations(KoodistoUriAndVersioType ylaKoodisto, List<KoodistoUriAndVersioType> alaKoodistos,
                                             SuhteenTyyppi st) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<KoodistonSuhde> cquery = cb.createQuery(KoodistonSuhde.class);
        Root<KoodistonSuhde> root = cquery.from(KoodistonSuhde.class);

        root.fetch("ylakoodistoVersio");
        root.fetch("alakoodistoVersio");

        Predicate restrictions = addRestrictions(cquery, cb, root, ylaKoodisto, alaKoodistos, st);
        cquery.select(root).where(restrictions);

        return em.createQuery(cquery).getResultList();
    }

    @Override
    public void copyRelations(KoodistoVersio old, KoodistoVersio fresh) {
        logger.info("Copying codes relations, old codes versio id={}, new codes versio id={}", old.getId(), fresh.getId());
        fresh.setYlakoodistos(copyRelations(old.getYlakoodistos(), fresh));
        fresh.setAlakoodistos(copyRelations(old.getAlakoodistos(), fresh));
    }

    private Set<KoodistonSuhde> copyRelations(Set<KoodistonSuhde> relations, KoodistoVersio fresh) {
        Set<KoodistonSuhde> copiedRelations = new HashSet<>();
        String koodistoUri = fresh.getKoodisto().getKoodistoUri();
        for (KoodistonSuhde relation : relations) {
            if (relation.isPassive()) {
                continue;
            }
            KoodistoVersio child = relation.getAlakoodistoVersio().getKoodisto().getKoodistoUri().equals(koodistoUri) ?
                    fresh : relation.getAlakoodistoVersio();
            KoodistoVersio parent = relation.getYlakoodistoVersio().getKoodisto().getKoodistoUri().equals(koodistoUri) ?
                    fresh : relation.getYlakoodistoVersio();
            copiedRelations.add(koodistonSuhdeRepository.save(this.createNewRelation(parent, child, relation)));
            setOldRelationToPassive(relation, koodistoUri);
        }
        em.flush();
        return copiedRelations;
    }


    protected void setOldRelationToPassive(KoodistonSuhde relation, String koodistoUri) {
        if (relation.getAlakoodistoVersio().getKoodisto().getKoodistoUri().equals(koodistoUri)) {
            relation.setAlaKoodistoPassive(true);
        } else {
            relation.setYlaKoodistoPassive(true);
        }
    }

    protected KoodistonSuhde createNewRelation(KoodistoVersio parent, KoodistoVersio child, KoodistonSuhde relation) {
        logger.info(
                "  Inserting new codes version relation, parent codes id=" + parent.getKoodisto().getId() + ", parent version id=" + parent.getId()
                + ", child codes id=" + child.getKoodisto().getId() + ", child version id=" + child.getId() + ", relation id="
                + relation.getId() + ", relation version="
                + (relation.getVersio() + 1) + ", relation type="
                + relation.getSuhteenTyyppi());
        KoodistonSuhde newRelation = new KoodistonSuhde();
        newRelation.setAlakoodistoVersio(child);
        newRelation.setYlakoodistoVersio(parent);
        newRelation.setVersio(relation.getVersio() + 1);
        newRelation.setSuhteenTyyppi(relation.getSuhteenTyyppi());
        return newRelation;
    }

    @Override
    @Transactional
    public void deleteRelations(KoodistoUriAndVersioType ylaKoodisto, List<KoodistoUriAndVersioType> alaKoodistos,
            SuhteenTyyppi st) {
        for (KoodistonSuhde suhde : getRelations(ylaKoodisto, alaKoodistos, st)) {
            KoodistoVersio ala = suhde.getAlakoodistoVersio();
            KoodistoVersio yla = suhde.getYlakoodistoVersio();
            ala.removeYlaKoodistonSuhde(suhde);
            yla.removeAlaKoodistonSuhde(suhde);
            em.remove(suhde);
        }
        //em.flush();
    }
}
