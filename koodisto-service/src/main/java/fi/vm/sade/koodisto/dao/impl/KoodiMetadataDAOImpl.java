/**
 *
 */
package fi.vm.sade.koodisto.dao.impl;

import fi.vm.sade.koodisto.dao.KoodiMetadataDAO;
import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.Koodisto;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class KoodiMetadataDAOImpl extends AbstractJpaDAOImpl<KoodiMetadata, Long> implements KoodiMetadataDAO {

    private static final String NIMI = "nimi";
    private static final String KOODI = "koodi";
    private static final String KOODI_VERSIO = "koodiVersio";
    private static final int INITIALIZE_KOODI_ID_BATCH_SIZE = 5000;

    @Override
    public boolean nimiExistsForSomeOtherKoodi(String koodiUri, String nimi) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<KoodiMetadata> root = query.from(KoodiMetadata.class);
        Join<KoodiVersio, Koodi> koodi = root.join(KOODI_VERSIO).join(KOODI);

        Predicate nimiEquals = cb.equal(cb.lower(root.<String>get(NIMI)), nimi.toLowerCase());
        Predicate koodiUriNotEquals = cb.not(cb.equal(koodi.<String>get("koodiUri"), koodiUri));

        query.select(cb.count(root.<String>get(NIMI))).where(cb.and(nimiEquals, koodiUriNotEquals));
        return em.createQuery(query).getSingleResult() > 0;
    }

    @Override
    public boolean nimiExists(String nimi) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<KoodiMetadata> root = query.from(KoodiMetadata.class);
        root.join(KOODI_VERSIO).join(KOODI);

        Predicate nimiEquals = cb.equal(cb.lower(root.<String>get(NIMI)), nimi.toLowerCase());

        query.select(cb.count(root.<String>get(NIMI))).where(nimiEquals);
        return em.createQuery(query).getSingleResult() > 0;
    }

    @Override
    public boolean nimiExistsInKoodisto(String koodistoUri, String nimi) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<KoodiMetadata> root = query.from(KoodiMetadata.class);
        Join<Koodi, Koodisto> koodisto = root.join(KOODI_VERSIO).join(KOODI).join("koodisto");

        Predicate nimiEquals = cb.equal(cb.lower(root.<String>get(NIMI)), nimi.toLowerCase());
        Predicate koodistoUriEquals = cb.equal(koodisto.get("koodistoUri"), koodistoUri);

        query.select(cb.count(root.<String>get(NIMI))).where(cb.and(nimiEquals, koodistoUriEquals));
        return em.createQuery(query).getSingleResult() > 0;
    }

    @Override
    public boolean nimiExistsInKoodistoForSomeOtherKoodi(String koodistoUri, String koodiUri, String nimi) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<KoodiMetadata> root = query.from(KoodiMetadata.class);
        Join<KoodiVersio, Koodi> koodi = root.join(KOODI_VERSIO).join(KOODI);
        Join<Koodi, Koodisto> koodisto = koodi.join("koodisto");

        Predicate nimiEquals = cb.equal(cb.lower(root.<String>get(NIMI)), nimi.toLowerCase());
        Predicate koodiUriNotEquals = cb.not(cb.equal(koodi.<String>get("koodiUri"), koodiUri));
        Predicate koodistoUriEquals = cb.equal(koodisto.get("koodistoUri"), koodistoUri);

        query.select(cb.count(root.<String>get(NIMI))).where(cb.and(nimiEquals, koodiUriNotEquals, koodistoUriEquals));
        return em.createQuery(query).getSingleResult() > 0;
    }

    @Override
    public void initializeByKoodiVersioIds(Set<Long> koodiVersioIdSet) {
        final int[] counter = new int[] { 0 };
        koodiVersioIdSet.stream().collect(Collectors.groupingBy(
                    koodiVersioId -> counter[0]++ / INITIALIZE_KOODI_ID_BATCH_SIZE)
                ).values().parallelStream().forEach(this::initialize);
    }

    private void initialize(List<Long> koodiVersioIds) {
        TypedQuery<KoodiMetadata> query = this.getEntityManager().createNamedQuery(
                "KoodiMetadata.initializeByKoodiVersioIds", KoodiMetadata.class);
        query.setParameter("versioIds", koodiVersioIds);
        query.getResultList();
    }
}
