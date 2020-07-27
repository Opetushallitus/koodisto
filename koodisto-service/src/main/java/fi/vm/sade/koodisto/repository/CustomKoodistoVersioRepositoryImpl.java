package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.DateHelper;
import org.apache.commons.lang.StringUtils;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomKoodistoVersioRepositoryImpl implements CustomKoodistoVersioRepository {

    private static final String KOODISTO = "koodisto";
    private static final String VERSIO = "versio";

    private final EntityManager entityManager;

    public CustomKoodistoVersioRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<KoodistoVersio> searchKoodistos(SearchKoodistosCriteriaType searchCriteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<KoodistoVersio> criteriaQuery = cb.createQuery(KoodistoVersio.class);
        Root<KoodistoVersio> root = criteriaQuery.from(KoodistoVersio.class);

        final Join<KoodistoVersio, Koodisto> koodi = root.join(KOODISTO);

        List<Predicate> restrictions = createRestrictionsForKoodistoCriteria(cb, criteriaQuery, searchCriteria, koodi, root);

        criteriaQuery.select(root).where(cb.and(restrictions.toArray(new Predicate[restrictions.size()])));
        criteriaQuery.distinct(true);

        EntityGraph<?> entityGraph = entityManager.getEntityGraph("koodistoWithRelations");
        TypedQuery<KoodistoVersio> query = entityManager.createQuery(criteriaQuery)
                .setHint("javax.persistence.fetchgraph", entityGraph);

        return query.getResultList();
    }

    @Override
    public KoodistoVersio getPreviousKoodistoVersio(String koodistoUri, Integer koodistoVersio) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<KoodistoVersio> query = cb.createQuery(KoodistoVersio.class);
        Root<KoodistoVersio> root = query.from(KoodistoVersio.class);
        Join<KoodistoVersio, Koodisto> koodisto = root.join(KOODISTO);

        Predicate koodistoUriEqual = cb.equal(koodisto.get("koodistoUri"), koodistoUri);
        Predicate koodistoVersioLessThan = cb.lessThan(root.get(VERSIO), koodistoVersio);

        query.select(root).where(cb.and(koodistoUriEqual, koodistoVersioLessThan)).orderBy(cb.desc(root.get(VERSIO)));

        List<KoodistoVersio> resultList = entityManager.createQuery(query).setMaxResults(1).getResultList();

        KoodistoVersio result = null;
        if (resultList.size() != 0) {
            result = resultList.get(0);
        }

        return result;
    }

    private static List<Predicate> createRestrictionsForKoodistoCriteria(CriteriaBuilder cb,
                                                                         CriteriaQuery<KoodistoVersio> criteriaQuery,
                                                                         SearchKoodistosCriteriaType searchCriteria,
                                                                         Join<KoodistoVersio, Koodisto> koodisto,
                                                                         Root<KoodistoVersio> koodistoVersio) {
        List<Predicate> restrictions = new ArrayList<>();

        if (searchCriteria != null) {
            if (searchCriteria.getKoodistoUris() != null && searchCriteria.getKoodistoUris().size() > 0) {
                List<Predicate> uriRestrictions = new ArrayList<>();

                for (String koodistoUri : searchCriteria.getKoodistoUris()) {
                    if (StringUtils.isNotBlank(koodistoUri)) {
                        uriRestrictions.add(cb.equal(koodisto.get("koodistoUri"), koodistoUri));
                    }
                }

                if (!uriRestrictions.isEmpty()) {
                    restrictions.add(uriRestrictions.size() == 1 ? uriRestrictions.get(0)
                            : cb.or(uriRestrictions.toArray(new Predicate[uriRestrictions.size()])));
                }
            }

            if (searchCriteria.getKoodistoVersioSelection() != null) {
                switch (searchCriteria.getKoodistoVersioSelection()) {
                    case SPECIFIC:
                        restrictions.add(cb.equal(koodistoVersio.get(VERSIO), searchCriteria.getKoodistoVersio()));
                        break;
                    case LATEST:
                        restrictions.add(cb.equal(koodistoVersio.get(VERSIO), selectMaxVersionSubQuery(cb, criteriaQuery, searchCriteria, koodisto)));
                        break;
                    case ALL:
                        break;
                    default:
                        break;
                }
            }

            restrictions.addAll(createSecondaryRestrictionsForKoodistoCriteria(cb, searchCriteria, koodistoVersio));
        }

        return restrictions;
    }

    private static List<Predicate> createSecondaryRestrictionsForKoodistoCriteria(CriteriaBuilder cb, SearchKoodistosCriteriaType searchCriteria,
                                                                                  Root<KoodistoVersio> koodistoVersio) {
        List<Predicate> restrictions = new ArrayList<>();

        if (searchCriteria != null) {
            if (searchCriteria.getKoodistoTilas() != null && searchCriteria.getKoodistoTilas().size() > 0) {
                List<Predicate> tilaRestrictions = new ArrayList<>();
                for (TilaType tila : searchCriteria.getKoodistoTilas()) {
                    tilaRestrictions.add(cb.equal(koodistoVersio.get("tila"), Tila.valueOf(tila.name())));
                }

                restrictions.add(tilaRestrictions.size() == 1 ? tilaRestrictions.get(0)
                        : cb.or(tilaRestrictions.toArray(new Predicate[tilaRestrictions.size()])));
            }

            if (searchCriteria.getValidAt() != null) {
                Date validAt = DateHelper.xmlCalToDate(searchCriteria.getValidAt());
                Predicate conditionVoimassaAlku = cb.lessThanOrEqualTo(koodistoVersio.get("voimassaAlkuPvm"), validAt);
                Predicate conditionNullAlku = cb.isNull(koodistoVersio.get("voimassaAlkuPvm"));
                Predicate conditionVoimassaLoppu = cb.greaterThanOrEqualTo(koodistoVersio.get("voimassaLoppuPvm"), validAt);
                Predicate conditionNullLoppu = cb.isNull(koodistoVersio.get("voimassaLoppuPvm"));

                restrictions.add(cb.and(cb.or(conditionVoimassaAlku, conditionNullAlku), cb.or(conditionVoimassaLoppu, conditionNullLoppu)));
            }
        }

        return restrictions;
    }

    private static Subquery<Integer> selectMaxVersionSubQuery(CriteriaBuilder cb,
                                                              CriteriaQuery<KoodistoVersio> criteriaQuery,
                                                              SearchKoodistosCriteriaType searchCriteria,
                                                              Path<Koodisto> koodistoPath) {
        final Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
        final Root<KoodistoVersio> root = subquery.from(KoodistoVersio.class);

        final Join<KoodistoVersio, Koodisto> koodisto = root.join(KOODISTO, JoinType.INNER);
        final Expression<Integer> versioMax = cb.max(root.get(VERSIO));

        List<Predicate> restrictions = createSecondaryRestrictionsForKoodistoCriteria(cb, searchCriteria, root);
        restrictions.add(cb.equal(koodisto.get("id"), koodistoPath.get("id")));

        return subquery.select(versioMax).where(cb.and(restrictions.toArray(new Predicate[restrictions.size()])));
    }

}
