package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistonSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.types.common.KoodistoUriAndVersioType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class CustomKoodistonSuhdeRepositoryImpl implements CustomKoodistonSuhdeRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomKoodistonSuhdeRepositoryImpl.class);
    private static final String VERSIO = "versio";
    private static final String KOODISTO_URI = "koodistoUri";

    private final EntityManager entityManager;

    public CustomKoodistonSuhdeRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<KoodistonSuhde> getRelations(KoodistoUriAndVersioType ylaKoodisto, List<KoodistoUriAndVersioType> alaKoodistos,
                                             SuhteenTyyppi st) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<KoodistonSuhde> cquery = cb.createQuery(KoodistonSuhde.class);
        Root<KoodistonSuhde> root = cquery.from(KoodistonSuhde.class);

        root.fetch("ylakoodistoVersio");
        root.fetch("alakoodistoVersio");

        Predicate restrictions = addRestrictions(cquery, cb, root, ylaKoodisto, alaKoodistos, st);
        cquery.select(root).where(restrictions);

        return entityManager.createQuery(cquery).getResultList();
    }

    @Transactional
    @Override
    public void copyRelations(KoodistoVersio old, KoodistoVersio fresh) {
        LOGGER.info("Copying codes relations, old codes versio id={}, new codes versio id={}", old.getId(), fresh.getId());
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
            copiedRelations.add(createNewRelation(parent, child, relation));
            setOldRelationToPassive(relation, koodistoUri);
        }
        return copiedRelations;
    }

    private void setOldRelationToPassive(KoodistonSuhde relation, String koodistoUri) {
        if (relation.getAlakoodistoVersio().getKoodisto().getKoodistoUri().equals(koodistoUri)) {
            relation.setAlaKoodistoPassive(true);
        } else {
            relation.setYlaKoodistoPassive(true);
        }
    }

    private KoodistonSuhde createNewRelation(KoodistoVersio parent, KoodistoVersio child, KoodistonSuhde relation) {
        LOGGER.info(
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

    @Transactional
    @Override
    public void deleteRelations(KoodistoUriAndVersioType ylaKoodisto, List<KoodistoUriAndVersioType> alaKoodistos, SuhteenTyyppi st) {
        for (KoodistonSuhde suhde : getRelations(ylaKoodisto, alaKoodistos, st)) {
            KoodistoVersio ala = suhde.getAlakoodistoVersio();
            KoodistoVersio yla = suhde.getYlakoodistoVersio();
            ala.removeYlaKoodistonSuhde(suhde);
            yla.removeAlaKoodistonSuhde(suhde);
            entityManager.remove(suhde);
        }
    }

    private static Predicate addRestrictions(CriteriaQuery<?> cquery, CriteriaBuilder cb, Root<KoodistonSuhde> root,
                                             KoodistoUriAndVersioType ylaKoodisto,
                                             List<KoodistoUriAndVersioType> alaKoodistos,
                                             SuhteenTyyppi st) {

        Join<KoodistonSuhde, KoodistoVersio> ylakoodistoVersioJoin = root.join("ylakoodistoVersio");
        Join<KoodistonSuhde, KoodistoVersio> alakoodistoVersioJoin = root.join("alakoodistoVersio");
        Join<KoodistoVersio, Koodisto> alakoodistoJoin = alakoodistoVersioJoin.join("koodisto");
        Join<KoodistoVersio, Koodisto> ylakoodistoJoin = ylakoodistoVersioJoin.join("koodisto");

        Predicate suhteenTyyppiRestriction = cb.equal(root.get("suhteenTyyppi"), st);

        List<Predicate> alakoodistoRestrictions = new ArrayList<>();
        for (KoodistoUriAndVersioType ak : alaKoodistos) {
            alakoodistoRestrictions.add(cb.and(cb.equal(alakoodistoJoin.get(KOODISTO_URI), ak.getKoodistoUri()),
                    cb.equal(alakoodistoVersioJoin.get(VERSIO), ak.getVersio())));
        }

        Predicate ylakoodistoRestriction = cb.and(cb.equal(ylakoodistoJoin.get(KOODISTO_URI),
                ylaKoodisto.getKoodistoUri()), cb.equal(ylakoodistoVersioJoin.get(VERSIO), ylaKoodisto.getVersio()));

        cquery.distinct(true);

        Predicate and;
        if (st == SuhteenTyyppi.RINNASTEINEN) {

            Predicate ylaAndAla = cb.and(ylakoodistoRestriction,
                    cb.or(alakoodistoRestrictions.toArray(new Predicate[alakoodistoRestrictions.size()])));

            List<Predicate> ylakoodiRestrictions = new ArrayList<>();
            for (KoodistoUriAndVersioType ak : alaKoodistos) {
                ylakoodiRestrictions.add(cb.and(cb.equal(ylakoodistoJoin.get(KOODISTO_URI),
                        ak.getKoodistoUri()), cb.equal(ylakoodistoVersioJoin.get(VERSIO), ak.getVersio())));
            }

            Predicate alakoodistoRestriction = cb.and(cb.equal(alakoodistoJoin.get(KOODISTO_URI),
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
    public int deleteByKoodistoVersio(KoodistoVersio koodistoVersio) {
        CriteriaBuilder criteria = entityManager.getCriteriaBuilder();
        CriteriaDelete<KoodistonSuhde> delete = criteria.createCriteriaDelete(KoodistonSuhde.class);
        Root<KoodistonSuhde> root = delete.from(KoodistonSuhde.class);
        delete.where(criteria.or(
                criteria.equal(root.get("ylakoodistoVersio"), koodistoVersio),
                criteria.equal(root.get("alakoodistoVersio"), koodistoVersio)
        ));
        return entityManager.createQuery(delete).executeUpdate();
    }
}
