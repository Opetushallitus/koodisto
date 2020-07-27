package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.business.util.KoodistoItem;
import fi.vm.sade.koodisto.service.types.KoodiBaseSearchCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoVersioSelectionType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.DateHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

@Repository
public class CustomKoodiVersioRepositoryImpl implements CustomKoodiVersioRepository {

    private static final String METADATAS = "metadatas";
    private static final String ORGANISAATIO_OID = "organisaatioOid";
    private static final String KOODI_VERSIOS = "koodiVersios";
    private static final String KOODIARVO = "koodiarvo";
    private static final String VOIMASSA_LOPPU_PVM = "voimassaLoppuPvm";
    private static final String VOIMASSA_ALKU_PVM = "voimassaAlkuPvm";
    private static final String TILA = "tila";
    private static final String ID = "id";
    private static final String KOODI_VERSIO = "koodiVersio";
    private static final String KOODI_URI = "koodiUri";
    private static final String KOODI = "koodi";
    private static final String VERSIO = "versio";
    private static final String KOODISTO_URI = "koodistoUri";
    private static final String KOODISTO = "koodisto";
    private static final String KOODISTO_VERSIO = "koodistoVersio";
    private static final String KOODISTO_VERSIOS = "koodistoVersios";

    private static final int TUPLE_KOODI_VERSIO = 0;
    private static final int TUPLE_URI = 1;
    private static final int TUPLE_ORGANISAATIO_OID = 2;
    private static final int TUPLE_VERSIO = 3;

    private final EntityManager entityManager;

    public CustomKoodiVersioRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<KoodiVersio> getKoodiVersiosIncludedOnlyInKoodistoVersio(String koodistoUri, Integer koodistoVersio) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<KoodiVersio> query = cb.createQuery(KoodiVersio.class);
        Root<KoodiVersio> root = query.from(KoodiVersio.class);
        Join<KoodistoVersioKoodiVersio, KoodistoVersio> koodistoVersioJoin = root.join(KOODISTO_VERSIOS).join(KOODISTO_VERSIO);
        Join<KoodistoVersio, Koodisto> koodistoJoin = koodistoVersioJoin.join(KOODISTO);

        Predicate condition = cb.equal(koodistoJoin.get(KOODISTO_URI), koodistoUri);
        Predicate conditionVersio = cb.equal(koodistoVersioJoin.get(VERSIO), koodistoVersio);

        final Expression<Integer> koodistoVersios = cb.size(root.get(KOODISTO_VERSIOS));
        Predicate condSize = cb.equal(koodistoVersios, 1);
        query.distinct(true);
        query.select(root).where(condition, conditionVersio, condSize);

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<KoodiVersioWithKoodistoItem> searchKoodis(SearchKoodisByKoodistoCriteriaType searchCriteria) {
        // Find the latest version of koodisto
        if (SearchKoodisByKoodistoVersioSelectionType.LATEST.equals(searchCriteria.getKoodistoVersioSelection())) {
            Integer koodistoVersion = getKoodistoVersionByCriteria(searchCriteria);
            if (koodistoVersion == null) {
                return new ArrayList<>();
            }

            searchCriteria.setKoodistoVersioSelection(SearchKoodisByKoodistoVersioSelectionType.SPECIFIC);
            searchCriteria.setKoodistoVersio(koodistoVersion);
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = cb.createTupleQuery();
        Root<KoodistoVersio> root = criteriaQuery.from(KoodistoVersio.class);
        Join<KoodistoVersio, Koodisto> koodisto = root.join(KOODISTO);
        Join<KoodistoVersioKoodiVersio, KoodiVersio> koodiVersio = root.join(KOODI_VERSIOS).join(KOODI_VERSIO);
        Join<KoodiVersio, Koodi> koodi = koodiVersio.join(KOODI);
        koodiVersio.fetch(KOODI);
        koodiVersio.fetch(METADATAS, JoinType.LEFT);
        Join<KoodistoVersioKoodiVersio, KoodistoVersio> koodistoVersio = koodiVersio.join(KOODISTO_VERSIOS, JoinType.LEFT).join(KOODISTO_VERSIO, JoinType.LEFT);
        Join<Koodi, Koodisto> koodistoJoin = koodi.join(KOODISTO, JoinType.LEFT);

        List<Predicate> restrictions = createRestrictionsForKoodistoCriteria(cb, searchCriteria, koodisto, root);
        restrictions.addAll(createRestrictionsForKoodiCriteria(cb, searchCriteria.getKoodiSearchCriteria(), koodi, koodiVersio));

        criteriaQuery
                .multiselect(koodiVersio, koodistoJoin.get(KOODISTO_URI), koodistoJoin.get(ORGANISAATIO_OID), koodistoVersio.get(VERSIO))
                .where(cb.and(restrictions.toArray(new Predicate[restrictions.size()])));

        criteriaQuery.distinct(true);

        List<Tuple> result = entityManager.createQuery(criteriaQuery).getResultList();

        return convertSearchResultSet(result);
    }

    @Override
    public List<KoodiVersioWithKoodistoItem> searchKoodis(SearchKoodisCriteriaType searchCriteria) {
        if (searchCriteriaIsBlank(searchCriteria)) {
            return new ArrayList<>();
        }
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = cb.createTupleQuery();
        Root<KoodiVersio> root = criteriaQuery.from(KoodiVersio.class);

        final Join<KoodiVersio, Koodi> koodi = root.join(KOODI);
        root.fetch(KOODI);
        root.fetch(METADATAS, JoinType.LEFT);
        Join<KoodistoVersioKoodiVersio, KoodistoVersio> koodistoVersio = root
                .join(KOODISTO_VERSIOS, JoinType.LEFT)
                .join(KOODISTO_VERSIO, JoinType.LEFT);

        Join<Koodi, Koodisto> koodistoJoin = koodi.join(KOODISTO, JoinType.LEFT);

        List<Predicate> restrictions = createRestrictionsForKoodiCriteria(cb, criteriaQuery, searchCriteria, koodi, root);

        criteriaQuery.multiselect(root, koodistoJoin.get(KOODISTO_URI), koodistoJoin.get(ORGANISAATIO_OID), koodistoVersio.get(VERSIO))
                .where(cb.and(restrictions.toArray(new Predicate[restrictions.size()])));
        criteriaQuery.distinct(true);

        TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery);

        List<Tuple> result = query.getResultList();
        return convertSearchResultSet(result);
    }

    private static boolean searchCriteriaIsBlank(SearchKoodisCriteriaType searchCriteria) {
        if (!StringUtils.isBlank(searchCriteria.getKoodiArvo()) || searchCriteria.getValidAt() != null) {
            return false;
        }
        boolean isBlank = true;
        for (String s : searchCriteria.getKoodiUris()) {
            isBlank = isBlank && StringUtils.isBlank(s);
        }
        return isBlank;
    }

    private Integer getKoodistoVersionByCriteria(SearchKoodisByKoodistoCriteriaType searchCriteria) {
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

    private static List<Predicate> createRestrictionsForKoodiCriteria(CriteriaBuilder cb, CriteriaQuery<?> criteriaQuery,
                                                                      SearchKoodisCriteriaType searchCriteria, Path<Koodi> koodi, Path<KoodiVersio> koodiVersio) {
        List<Predicate> restrictions = createRestrictionsForKoodiCriteria(cb, searchCriteria, koodi, koodiVersio);

        if (searchCriteria != null && searchCriteria.getKoodiVersioSelection() != null) {
            switch (searchCriteria.getKoodiVersioSelection()) {
                case SPECIFIC:
                    restrictions.add(cb.equal(koodiVersio.get(VERSIO), searchCriteria.getKoodiVersio()));
                    break;
                case LATEST:
                    restrictions.add(cb.equal(koodiVersio.get(VERSIO), selectMaxVersionSubQuery(cb, criteriaQuery, searchCriteria, koodi)));
                    break;

                case ALL:
                default:
                    break;
            }

        }

        return restrictions;
    }

    private static List<Predicate> createRestrictionsForKoodiCriteria(CriteriaBuilder cb, KoodiBaseSearchCriteriaType searchCriteria, Path<Koodi> koodi,
                                                                      Path<KoodiVersio> koodiVersio) {
        List<Predicate> restrictions = new ArrayList<>();

        if (searchCriteria != null) {
            if (searchCriteria.getKoodiUris() != null && !searchCriteria.getKoodiUris().isEmpty()) {
                ArrayList<String> koodiUris = new ArrayList<>();
                for (String koodiUri : searchCriteria.getKoodiUris()) {
                    if (StringUtils.isNotBlank(koodiUri)) {
                        koodiUris.add(koodiUri);
                    }
                }

                if (!koodiUris.isEmpty()) {
                    CriteriaBuilder.In<String> in = cb.in(koodi.get(KOODI_URI));
                    for (String uri : koodiUris) {
                        in.value(uri);
                    }
                    restrictions.add(in);
                }
            }
            restrictions.addAll(createSecondaryRestrictionsForKoodiSearchCriteria(cb, searchCriteria, koodiVersio));
        }

        return restrictions;
    }

    private static List<Predicate> createSecondaryRestrictionsForKoodiSearchCriteria(CriteriaBuilder cb, KoodiBaseSearchCriteriaType searchCriteria,
                                                                                     Path<KoodiVersio> koodiVersio) {
        List<Predicate> restrictions = new ArrayList<>();

        if (searchCriteria != null) {
            if (StringUtils.isNotBlank(searchCriteria.getKoodiArvo())) {
                restrictions.add(cb.like(cb.lower(koodiVersio.get(KOODIARVO)), searchCriteria.getKoodiArvo().toLowerCase() + '%'));
            }

            if (searchCriteria.getKoodiTilas() != null && searchCriteria.getKoodiTilas().size() > 0) {
                List<Predicate> tilaRestrictions = new ArrayList<>();
                for (TilaType tila : searchCriteria.getKoodiTilas()) {
                    if (tila != null) {
                        tilaRestrictions.add(cb.equal(koodiVersio.get(TILA), Tila.valueOf(tila.name())));
                    }
                }

                if (!tilaRestrictions.isEmpty()) {
                    restrictions.add(tilaRestrictions.size() == 1
                            ? tilaRestrictions.get(0)
                            : cb.or(tilaRestrictions.toArray(new Predicate[tilaRestrictions.size()])));
                }
            }

            if (searchCriteria.getValidAt() != null) {
                Date validAt = DateHelper.xmlCalToDate(searchCriteria.getValidAt());
                Predicate conditionVoimassaAlku = cb.lessThanOrEqualTo(koodiVersio.get(VOIMASSA_ALKU_PVM), validAt);
                Predicate conditionNullAlku = cb.isNull(koodiVersio.get(VOIMASSA_ALKU_PVM));
                Predicate conditionVoimassaLoppu = cb.greaterThanOrEqualTo(koodiVersio.get(VOIMASSA_LOPPU_PVM), validAt);
                Predicate conditionNullLoppu = cb.isNull(koodiVersio.get(VOIMASSA_LOPPU_PVM));

                restrictions.add(
                        cb.and(
                                cb.or(conditionVoimassaAlku, conditionNullAlku),
                                cb.or(conditionVoimassaLoppu, conditionNullLoppu)));
            }
        }

        return restrictions;
    }

    private static Subquery<Integer> selectMaxVersionSubQuery(CriteriaBuilder cb, CriteriaQuery<?> criteriaQuery, SearchKoodisCriteriaType searchCriteria,
                                                              Path<Koodi> koodiPath) {
        final Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
        final Root<KoodiVersio> koodiVersioRoot = subquery.from(KoodiVersio.class);

        final Join<KoodiVersio, Koodi> koodi = koodiVersioRoot.join(KOODI, JoinType.INNER);
        final Expression<Integer> versioMax = cb.max(koodiVersioRoot.get(VERSIO));

        List<Predicate> restrictions = createSecondaryRestrictionsForKoodiSearchCriteria(cb, searchCriteria, koodiVersioRoot);
        restrictions.add(cb.equal(koodi.get(ID), koodiPath.get(ID)));

        return subquery.select(versioMax).where(cb.and(restrictions.toArray(new Predicate[restrictions.size()])));
    }

    /**
     * Takes the result of the search method and converts into a structure
     * containing the koodiversio and the koodisto URIs and version numbers
     *
     * @param resultSet
     *            search result set
     * @return
     */
    private List<KoodiVersioWithKoodistoItem> convertSearchResultSet(List<Tuple> resultSet) {
        Map<KoodiVersio, Map<String, KoodistoItem>> koodiVersios = new HashMap<>();
        for (Tuple tuple : resultSet) {
            KoodiVersio kv = tuple.get(TUPLE_KOODI_VERSIO, KoodiVersio.class);

            Map<String, KoodistoItem> koodistos;
            if (!koodiVersios.containsKey(kv)) {
                koodistos = new HashMap<>();
                koodiVersios.put(kv, koodistos);
            } else {
                koodistos = koodiVersios.get(kv);
            }

            String uri = tuple.get(TUPLE_URI, String.class);
            String organisaatioOid = tuple.get(TUPLE_ORGANISAATIO_OID, String.class);
            Integer versio = tuple.get(TUPLE_VERSIO, Integer.class);

            if (StringUtils.isNotBlank(uri)) {
                KoodistoItem koodistoItem;
                if (!koodistos.containsKey(uri)) {
                    koodistoItem = new KoodistoItem();
                    koodistoItem.setKoodistoUri(uri);
                    koodistoItem.setOrganisaatioOid(organisaatioOid);
                    koodistos.put(uri, koodistoItem);
                } else {
                    koodistoItem = koodistos.get(uri);
                }

                if (versio != null) {
                    koodistoItem.addVersio(versio);
                }
            }
        }

        List<KoodiVersioWithKoodistoItem> result = new ArrayList<>();
        for (Map.Entry<KoodiVersio, Map<String, KoodistoItem>> ke : koodiVersios.entrySet()) {
            KoodiVersioWithKoodistoItem k = new KoodiVersioWithKoodistoItem();
            k.setKoodiVersio(ke.getKey());

            if (ke.getValue() != null && ke.getValue().size() > 0) {
                KoodistoItem koodistoItem = ke.getValue().values().iterator().next();
                k.setKoodistoItem(koodistoItem);
            }

            result.add(k);
        }

        return result;
    }

}
