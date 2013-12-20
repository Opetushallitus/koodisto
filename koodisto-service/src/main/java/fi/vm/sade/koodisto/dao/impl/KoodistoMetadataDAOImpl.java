/**
 *
 */
package fi.vm.sade.koodisto.dao.impl;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.koodisto.dao.KoodistoMetadataDAO;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * @author tommiha
 */
@Repository
public class KoodistoMetadataDAOImpl extends AbstractJpaDAOImpl<KoodistoMetadata, Integer> implements
        KoodistoMetadataDAO {

    private static final String NIMI = "nimi";

    @Override
    public List<KoodistoMetadata> listAllByKoodisto(String koodistoUri) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<KoodistoMetadata> query = cb.createQuery(KoodistoMetadata.class);
        Root<KoodistoMetadata> root = query.from(KoodistoMetadata.class);
        Join<KoodistoMetadata, KoodistoVersio> join = root.join("koodistoVersio");
        Join<KoodistoVersio, Koodisto> koodistoJoin = join.join("koodisto");

        Predicate uriCondition = cb.equal(koodistoJoin.get("koodistoUri"), koodistoUri);

        query.select(root).where(uriCondition);

        return em.createQuery(query).getResultList();
    }

    @Override
    public boolean nimiExistsForSomeOtherKoodisto(String koodistoUri, String nimi) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<KoodistoMetadata> root = query.from(KoodistoMetadata.class);
        Join<KoodistoVersio, Koodisto> koodisto = root.join("koodistoVersio").join("koodisto");

        Predicate nimiEquals = cb.equal(cb.lower(root.<String>get(NIMI)), nimi.toLowerCase());
        Predicate koodistoUriNotEquals = cb.not(cb.equal(koodisto.get("koodistoUri"), koodistoUri));

        query.select(cb.count(root.<String>get(NIMI))).where(cb.and(nimiEquals, koodistoUriNotEquals));
        return em.createQuery(query).getSingleResult() > 0;
    }

    @Override
    public boolean nimiExists(String nimi) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<KoodistoMetadata> root = query.from(KoodistoMetadata.class);
        Predicate nimiEquals = cb.equal(cb.lower(root.<String>get(NIMI)), nimi.toLowerCase());

        query.select(cb.count(root.<String>get(NIMI))).where(nimiEquals);
        return em.createQuery(query).getSingleResult() > 0;
    }

}
