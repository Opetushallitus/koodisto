package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodiMetadata;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class CustomKoodiMetadataRepositoryImpl implements CustomKoodiMetadataRepository {

    private static final int INITIALIZE_KOODI_ID_BATCH_SIZE = 5000;
    private final EntityManager entityManager;

    public CustomKoodiMetadataRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void initializeByKoodiVersioIds(Set<Long> koodiVersioIdSet) {
        final int[] counter = new int[] { 0 };
        koodiVersioIdSet.stream().collect(Collectors.groupingBy(
                koodiVersioId -> counter[0]++ / INITIALIZE_KOODI_ID_BATCH_SIZE)
        ).values().parallelStream().forEach(this::initialize);
    }

    private void initialize(List<Long> koodiVersioIds) {
        TypedQuery<KoodiMetadata> query = entityManager.createNamedQuery(
                "KoodiMetadata.initializeByKoodiVersioIds", KoodiMetadata.class);
        query.setParameter("versioIds", koodiVersioIds);
        query.getResultList();
    }
}
