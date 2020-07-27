package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoVersioSelectionType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.DateHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class CustomKoodistoRepositoryImpl implements CustomKoodistoRepository {

    private static final String VOIMASSA_LOPPU_PVM = "voimassaLoppuPvm";
    private static final String VOIMASSA_ALKU_PVM = "voimassaAlkuPvm";
    private static final String TILA = "tila";
    private static final String VERSIO = "versio";
    private static final String KOODISTO_URI = "koodistoUri";
    private static final String KOODISTO = "koodisto";

    private final EntityManager entityManager;
    private final KoodistoRepository koodistoRepository;

    public CustomKoodistoRepositoryImpl(EntityManager entityManager, KoodistoRepository koodistoRepository) {
        this.entityManager = entityManager;
        this.koodistoRepository = koodistoRepository;
    }

    @Override
    public boolean koodistoUriExists(String koodistoUri) {
        return koodistoRepository.findByKoodistoUri(koodistoUri).isPresent();
    }

    @Override
    public Integer getKoodistoVersionByCriteria(SearchKoodisByKoodistoCriteriaType searchCriteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
        Root<KoodistoVersio> root = criteriaQuery.from(KoodistoVersio.class);

        final Join<KoodistoVersio, Koodisto> koodisto = root.join(KOODISTO);
        List<Predicate> restrictions = createRestrictionsForKoodistoCriteria(cb, searchCriteria, koodisto, root);
        criteriaQuery.select(root.get(VERSIO)).where(cb.and(restrictions.toArray(new Predicate[restrictions.size()])));
        criteriaQuery.distinct(true);
        criteriaQuery.orderBy(cb.desc(root.get(VERSIO)));

        TypedQuery<Integer> query = entityManager.createQuery(criteriaQuery).setMaxResults(1);
        List<Integer> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    private static List<Predicate> createRestrictionsForKoodistoCriteria(CriteriaBuilder cb, SearchKoodisByKoodistoCriteriaType koodistoSearchCriteria,
                                                                         Path<Koodisto> koodisto, Path<KoodistoVersio> koodistoVersio) {
        List<Predicate> restrictions = new ArrayList<>();
        if (koodistoSearchCriteria != null) {
            if (StringUtils.isNotBlank(koodistoSearchCriteria.getKoodistoUri())) {
                restrictions.add(cb.equal(koodisto.get(KOODISTO_URI), koodistoSearchCriteria.getKoodistoUri()));
            }

            if (koodistoSearchCriteria.getKoodistoVersioSelection() != null
                    && SearchKoodisByKoodistoVersioSelectionType.SPECIFIC.equals(koodistoSearchCriteria.getKoodistoVersioSelection())) {
                restrictions.add(cb.equal(koodistoVersio.get(VERSIO), koodistoSearchCriteria.getKoodistoVersio()));
            }

            if (koodistoSearchCriteria.getKoodistoTilas() != null && koodistoSearchCriteria.getKoodistoTilas().size() > 0) {
                List<Predicate> tilaRestrictions = new ArrayList<>();
                for (TilaType tila : koodistoSearchCriteria.getKoodistoTilas()) {
                    if (tila != null) {
                        tilaRestrictions.add(cb.equal(koodistoVersio.get(TILA), Tila.valueOf(tila.name())));
                    }
                }

                if (!tilaRestrictions.isEmpty()) {
                    restrictions.add(tilaRestrictions.size() == 1
                            ? tilaRestrictions.get(0)
                            : cb.or(tilaRestrictions.toArray(new Predicate[tilaRestrictions.size()])));
                }
            }

            if (koodistoSearchCriteria.getValidAt() != null) {
                Date validAt = DateHelper.xmlCalToDate(koodistoSearchCriteria.getValidAt());
                Predicate conditionVoimassaAlku = cb.lessThanOrEqualTo(koodistoVersio.get(VOIMASSA_ALKU_PVM), validAt);
                Predicate conditionNullAlku = cb.isNull(koodistoVersio.get(VOIMASSA_ALKU_PVM));
                Predicate conditionVoimassaLoppu = cb.greaterThanOrEqualTo(koodistoVersio.get(VOIMASSA_LOPPU_PVM), validAt);
                Predicate conditionNullLoppu = cb.isNull(koodistoVersio.get(VOIMASSA_LOPPU_PVM));
                restrictions.add(cb.and(cb.or(conditionVoimassaAlku, conditionNullAlku), cb.or(conditionVoimassaLoppu, conditionNullLoppu)));
            }
        }

        return restrictions;
    }
}
