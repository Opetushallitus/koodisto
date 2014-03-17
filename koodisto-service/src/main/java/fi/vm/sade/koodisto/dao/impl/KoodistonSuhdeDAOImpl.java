/**
 *
 */
package fi.vm.sade.koodisto.dao.impl;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.koodisto.dao.KoodistonSuhdeDAO;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistonSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.types.common.KoodistoUriAndVersioType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


/**
 * @author
 */
@Repository
public class KoodistonSuhdeDAOImpl extends AbstractJpaDAOImpl<KoodistonSuhde, Long> implements KoodistonSuhdeDAO {

    private static final String VERSIO = "versio";
    private static final String KOODISTO_URI = "koodistoUri";

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
            alakoodistoRestrictions.add(cb.and(cb.equal(alakoodistoJoin.<String>get(KOODISTO_URI), ak.getKoodistoUri()),
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
                ylakoodiRestrictions.add(cb.and(cb.equal(ylakoodistoJoin.<String>get(KOODISTO_URI),
                        ak.getKoodistoUri()), cb.equal(ylakoodistoVersioJoin.get(VERSIO), ak.getVersio())));
            }

            Predicate alakoodistoRestriction = cb.and(cb.equal(alakoodistoJoin.<String>get(KOODISTO_URI),
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
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<KoodistonSuhde> cquery = cb.createQuery(KoodistonSuhde.class);
        Root<KoodistonSuhde> root = cquery.from(KoodistonSuhde.class);

        root.fetch("ylakoodistoVersio");
        root.fetch("alakoodistoVersio");

        Predicate restrictions = addRestrictions(cquery, cb, root, ylaKoodisto, alaKoodistos, st);
        cquery.select(root).where(restrictions);

        return em.createQuery(cquery).getResultList();
    }
}
