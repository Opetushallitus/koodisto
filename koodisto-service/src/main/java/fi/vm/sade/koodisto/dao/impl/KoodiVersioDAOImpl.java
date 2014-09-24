package fi.vm.sade.koodisto.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.koodisto.dao.KoodiDAO;
import fi.vm.sade.koodisto.dao.KoodiVersioDAO;
import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.business.util.KoodistoItem;
import fi.vm.sade.koodisto.service.types.KoodiBaseSearchCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoVersioSelectionType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;

@Repository
public class KoodiVersioDAOImpl extends AbstractJpaDAOImpl<KoodiVersio, Long> implements KoodiVersioDAO {

    private static final String SUHTEEN_TYYPPI = "suhteenTyyppi";
    private static final String YLAKOODI_VERSIO = "ylakoodiVersio";
    private static final String ALAKOODI_VERSIO = "alakoodiVersio";
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

    @Autowired
    private KoodiDAO koodiDAO;

    @Override
    public List<KoodiVersio> getKoodiVersiosIncludedOnlyInKoodistoVersio(String koodistoUri, Integer koodistoVersio) {

        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<KoodiVersio> query = cb.createQuery(KoodiVersio.class);
        Root<KoodiVersio> root = query.from(KoodiVersio.class);
        Join<KoodistoVersioKoodiVersio, KoodistoVersio> koodistoVersioJoin = root.join(KOODISTO_VERSIOS).join(KOODISTO_VERSIO);
        Join<KoodistoVersio, Koodisto> koodistoJoin = koodistoVersioJoin.join(KOODISTO);

        Predicate condition = cb.equal(koodistoJoin.get(KOODISTO_URI), koodistoUri);
        Predicate conditionVersio = cb.equal(koodistoVersioJoin.get(VERSIO), koodistoVersio);

        @SuppressWarnings("rawtypes")
        final Expression<Integer> koodistoVersios = cb.size(root.<Collection> get(KOODISTO_VERSIOS));
        Predicate condSize = cb.equal(koodistoVersios, 1);
        query.distinct(true);
        query.select(root).where(condition, conditionVersio, condSize);

        return em.createQuery(query).getResultList();
    }

    @Override
    public KoodiVersio getPreviousKoodiVersio(String koodiUri, Integer koodiVersio) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<KoodiVersio> query = cb.createQuery(KoodiVersio.class);
        Root<KoodiVersio> root = query.from(KoodiVersio.class);
        Join<KoodiVersio, Koodi> koodi = root.join(KOODI);

        Predicate koodiUriEqual = cb.equal(koodi.<String> get(KOODI_URI), koodiUri);
        Predicate koodiVersioLessThan = cb.lessThan(root.<Integer> get(VERSIO), koodiVersio);

        query.select(root).where(cb.and(koodiUriEqual, koodiVersioLessThan)).orderBy(cb.desc(root.<Integer> get(VERSIO)));

        List<KoodiVersio> resultList = em.createQuery(query).setMaxResults(1).getResultList();

        if (resultList.size() == 1) {
             return resultList.get(0);
        }
        return null;
    }

    @Override
    public List<KoodiVersio> getKoodiVersiosByKoodistoAndKoodiTila(Long koodistoVersioId, Tila koodiTila) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<KoodiVersio> query = cb.createQuery(KoodiVersio.class);
        Root<KoodistoVersioKoodiVersio> root = query.from(KoodistoVersioKoodiVersio.class);

        Join<KoodistoVersioKoodiVersio, KoodistoVersio> koodistoVersioJoin = root.join(KOODISTO_VERSIO, JoinType.LEFT);
        Join<KoodistoVersioKoodiVersio, KoodiVersio> koodiVersioJoin = root.join(KOODI_VERSIO, JoinType.LEFT);

        Predicate koodistoEqual = cb.equal(koodistoVersioJoin.get(ID), koodistoVersioId);
        Predicate koodiTilaEqual = cb.equal(koodiVersioJoin.get(TILA), koodiTila);

        query.distinct(true);
        query.select(koodiVersioJoin).where(cb.and(koodistoEqual, koodiTilaEqual));

        return em.createQuery(query).getResultList();
    }

    private Integer getKoodistoVersionByCriteria(SearchKoodisByKoodistoCriteriaType searchCriteria) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
        Root<KoodistoVersio> root = criteriaQuery.from(KoodistoVersio.class);

        final Join<KoodistoVersio, Koodisto> koodisto = root.join(KOODISTO);
        List<Predicate> restrictions = createRestrictionsForKoodistoCriteria(cb, searchCriteria, koodisto, root);
        criteriaQuery.select(root.<Integer> get(VERSIO)).where(cb.and(restrictions.toArray(new Predicate[restrictions.size()])));
        criteriaQuery.distinct(true);
        criteriaQuery.orderBy(cb.desc(root.get(VERSIO)));

        TypedQuery<Integer> query = em.createQuery(criteriaQuery).setMaxResults(1);
        List<Integer> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    private static List<Predicate> createRestrictionsForKoodistoCriteria(CriteriaBuilder cb, SearchKoodisByKoodistoCriteriaType koodistoSearchCriteria,
            Path<Koodisto> koodisto, Path<KoodistoVersio> koodistoVersio) {
        List<Predicate> restrictions = new ArrayList<Predicate>();
        if (koodistoSearchCriteria != null) {
            if (StringUtils.isNotBlank(koodistoSearchCriteria.getKoodistoUri())) {
                restrictions.add(cb.equal(koodisto.get(KOODISTO_URI), koodistoSearchCriteria.getKoodistoUri()));
            }

            if (koodistoSearchCriteria.getKoodistoVersioSelection() != null
                    && SearchKoodisByKoodistoVersioSelectionType.SPECIFIC.equals(koodistoSearchCriteria.getKoodistoVersioSelection())) {
                restrictions.add(cb.equal(koodistoVersio.get(VERSIO), koodistoSearchCriteria.getKoodistoVersio()));
            }

            if (koodistoSearchCriteria.getKoodistoTilas() != null && koodistoSearchCriteria.getKoodistoTilas().size() > 0) {
                List<Predicate> tilaRestrictions = new ArrayList<Predicate>();
                for (TilaType tila : koodistoSearchCriteria.getKoodistoTilas()) {
                    if (tila != null) {
                        tilaRestrictions.add(cb.equal(koodistoVersio.get(TILA), Tila.valueOf(tila.name())));
                    }
                }

                if (!tilaRestrictions.isEmpty()) {
                    restrictions.add(tilaRestrictions.size() == 1 ? tilaRestrictions.get(0) : cb.or(tilaRestrictions.toArray(new Predicate[tilaRestrictions
                            .size()])));
                }
            }

            if (koodistoSearchCriteria.getValidAt() != null) {
                Date validAt = DateHelper.xmlCalToDate(koodistoSearchCriteria.getValidAt());
                Predicate conditionVoimassaAlku = cb.lessThanOrEqualTo(koodistoVersio.<Date> get(VOIMASSA_ALKU_PVM), validAt);
                Predicate conditionNullAlku = cb.isNull(koodistoVersio.get(VOIMASSA_ALKU_PVM));
                Predicate conditionVoimassaLoppu = cb.greaterThanOrEqualTo(koodistoVersio.<Date> get(VOIMASSA_LOPPU_PVM), validAt);
                Predicate conditionNullLoppu = cb.isNull(koodistoVersio.get(VOIMASSA_LOPPU_PVM));

                restrictions.add(cb.and(cb.or(conditionVoimassaAlku, conditionNullAlku), cb.or(conditionVoimassaLoppu, conditionNullLoppu)));
            }
        }

        return restrictions;
    }

    private static List<Predicate> createRestrictionsForKoodiCriteria(CriteriaBuilder cb, CriteriaQuery<?> criteriaQuery,
            SearchKoodisCriteriaType searchCriteria, Path<Koodi> koodi, Path<KoodiVersio> koodiVersio) {
        List<Predicate> restrictions = createRestrictionsForKoodiCriteria(cb, (KoodiBaseSearchCriteriaType) searchCriteria, koodi, koodiVersio);

        if (searchCriteria != null && searchCriteria.getKoodiVersioSelection() != null) {
            switch (searchCriteria.getKoodiVersioSelection()) {
            case SPECIFIC:
                restrictions.add(cb.equal(koodiVersio.get(VERSIO), searchCriteria.getKoodiVersio()));
                break;
            case LATEST:
                restrictions.add(cb.equal(koodiVersio.get(VERSIO), selectMaxVersionSubQuery(cb, criteriaQuery, searchCriteria, koodi)));
                break;

            case ALL:
                break;

            default:
                break;
            }

        }

        return restrictions;
    }

    private static List<Predicate> createRestrictionsForKoodiCriteria(CriteriaBuilder cb, KoodiBaseSearchCriteriaType searchCriteria, Path<Koodi> koodi,
            Path<KoodiVersio> koodiVersio) {
        List<Predicate> restrictions = new ArrayList<Predicate>();

        if (searchCriteria != null) {
            if (searchCriteria.getKoodiUris() != null && !searchCriteria.getKoodiUris().isEmpty()) {
                ArrayList<String> koodiUris = new ArrayList<String>();
                for (String koodiUri : searchCriteria.getKoodiUris()) {
                    if (StringUtils.isNotBlank(koodiUri)) {
                        koodiUris.add(koodiUri);
                    }
                }

                if (!koodiUris.isEmpty()) {
                    In<String> in = cb.in(koodi.<String> get(KOODI_URI));
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
        List<Predicate> restrictions = new ArrayList<Predicate>();

        if (searchCriteria != null) {
            if (StringUtils.isNotBlank(searchCriteria.getKoodiArvo())) {
                restrictions.add(cb.like(cb.lower(koodiVersio.<String> get(KOODIARVO)), searchCriteria.getKoodiArvo().toLowerCase() + '%'));
            }

            if (searchCriteria.getKoodiTilas() != null && searchCriteria.getKoodiTilas().size() > 0) {
                List<Predicate> tilaRestrictions = new ArrayList<Predicate>();
                for (TilaType tila : searchCriteria.getKoodiTilas()) {
                    if (tila != null) {
                        tilaRestrictions.add(cb.equal(koodiVersio.get(TILA), Tila.valueOf(tila.name())));
                    }
                }

                if (!tilaRestrictions.isEmpty()) {
                    restrictions.add(tilaRestrictions.size() == 1 ? tilaRestrictions.get(0) : cb.or(tilaRestrictions.toArray(new Predicate[tilaRestrictions
                            .size()])));
                }
            }

            if (searchCriteria.getValidAt() != null) {
                Date validAt = DateHelper.xmlCalToDate(searchCriteria.getValidAt());
                Predicate conditionVoimassaAlku = cb.lessThanOrEqualTo(koodiVersio.<Date> get(VOIMASSA_ALKU_PVM), validAt);
                Predicate conditionNullAlku = cb.isNull(koodiVersio.get(VOIMASSA_ALKU_PVM));
                Predicate conditionVoimassaLoppu = cb.greaterThanOrEqualTo(koodiVersio.<Date> get(VOIMASSA_LOPPU_PVM), validAt);
                Predicate conditionNullLoppu = cb.isNull(koodiVersio.get(VOIMASSA_LOPPU_PVM));

                restrictions.add(cb.and(cb.or(conditionVoimassaAlku, conditionNullAlku), cb.or(conditionVoimassaLoppu, conditionNullLoppu)));
            }
        }

        return restrictions;
    }

    @Override
    public List<KoodiVersioWithKoodistoItem> searchKoodis(SearchKoodisByKoodistoCriteriaType searchCriteria) {

        // Find the latest version of koodisto
        if (SearchKoodisByKoodistoVersioSelectionType.LATEST.equals(searchCriteria.getKoodistoVersioSelection())) {
            Integer koodistoVersion = getKoodistoVersionByCriteria(searchCriteria);
            if (koodistoVersion == null) {
                return new ArrayList<KoodiVersioWithKoodistoItem>();
            }

            searchCriteria.setKoodistoVersioSelection(SearchKoodisByKoodistoVersioSelectionType.SPECIFIC);
            searchCriteria.setKoodistoVersio(koodistoVersion);
        }

        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
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

        criteriaQuery.multiselect(koodiVersio, koodistoJoin.get(KOODISTO_URI), koodistoJoin.get(ORGANISAATIO_OID), koodistoVersio.get(VERSIO)).where(
                cb.and(restrictions.toArray(new Predicate[restrictions.size()])));

        criteriaQuery.distinct(true);

        List<Tuple> result = em.createQuery(criteriaQuery).getResultList();

        return convertSearchResultSet(result);

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
        Map<KoodiVersio, Map<String, KoodistoItem>> koodiVersios = new HashMap<KoodiVersio, Map<String, KoodistoItem>>();
        for (Tuple tuple : resultSet) {
            KoodiVersio kv = tuple.get(TUPLE_KOODI_VERSIO, KoodiVersio.class);

            Map<String, KoodistoItem> koodistos = null;
            if (!koodiVersios.containsKey(kv)) {
                koodistos = new HashMap<String, KoodistoItem>();
                koodiVersios.put(kv, koodistos);
            } else {
                koodistos = koodiVersios.get(kv);
            }

            String uri = tuple.get(TUPLE_URI, String.class);
            String organisaatioOid = tuple.get(TUPLE_ORGANISAATIO_OID, String.class);
            Integer versio = tuple.get(TUPLE_VERSIO, Integer.class);

            if (StringUtils.isNotBlank(uri)) {
                KoodistoItem koodistoItem = null;
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

        List<KoodiVersioWithKoodistoItem> result = new ArrayList<KoodiVersioWithKoodistoItem>();
        for (Entry<KoodiVersio, Map<String, KoodistoItem>> ke : koodiVersios.entrySet()) {
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

    @Override
    @Transactional
    public List<KoodiVersioWithKoodistoItem> searchKoodis(SearchKoodisCriteriaType searchCriteria) {
        if (searchCriteriaIsBlank(searchCriteria)) {
            return new ArrayList<KoodiVersioWithKoodistoItem>();
        }
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = cb.createTupleQuery();
        Root<KoodiVersio> root = criteriaQuery.from(KoodiVersio.class);

        final Join<KoodiVersio, Koodi> koodi = root.join(KOODI);
        root.fetch(KOODI);
        root.fetch(METADATAS, JoinType.LEFT);
        Join<KoodistoVersioKoodiVersio, KoodistoVersio> koodistoVersio = root.join(KOODISTO_VERSIOS, JoinType.LEFT).join(KOODISTO_VERSIO, JoinType.LEFT);

        Join<Koodi, Koodisto> koodistoJoin = koodi.join(KOODISTO, JoinType.LEFT);

        List<Predicate> restrictions = createRestrictionsForKoodiCriteria(cb, criteriaQuery, searchCriteria, koodi, root);

        criteriaQuery.multiselect(root, koodistoJoin.get(KOODISTO_URI), koodistoJoin.get(ORGANISAATIO_OID), koodistoVersio.get(VERSIO)).where(
                cb.and(restrictions.toArray(new Predicate[restrictions.size()])));
        criteriaQuery.distinct(true);

        TypedQuery<Tuple> query = em.createQuery(criteriaQuery);

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

    private static Subquery<Integer> selectMaxVersionSubQuery(CriteriaBuilder cb, CriteriaQuery<?> criteriaQuery, SearchKoodisCriteriaType searchCriteria,
            Path<Koodi> koodiPath) {
        final Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
        final Root<KoodiVersio> koodiVersioRoot = subquery.from(KoodiVersio.class);

        final Join<KoodiVersio, Koodi> koodi = koodiVersioRoot.join(KOODI, JoinType.INNER);
        final Expression<Integer> versioMax = cb.max(koodiVersioRoot.<Integer> get(VERSIO));

        List<Predicate> restrictions = createSecondaryRestrictionsForKoodiSearchCriteria(cb, searchCriteria, koodiVersioRoot);
        restrictions.add(cb.equal(koodi.get(ID), koodiPath.get(ID)));

        return subquery.select(versioMax).where(cb.and(restrictions.toArray(new Predicate[restrictions.size()])));
    }

    @Override
    public List<KoodiVersioWithKoodistoItem> listByChildRelation(KoodiUriAndVersioType parent, SuhteenTyyppi suhdeTyyppi) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = cb.createTupleQuery();
        Root<KoodinSuhde> root = criteriaQuery.from(KoodinSuhde.class);
        Join<KoodinSuhde, KoodiVersio> alakoodiVersioJoin = root.join(ALAKOODI_VERSIO);
        Join<KoodiVersio, Koodi> koodi = alakoodiVersioJoin.join(KOODI);

        Join<KoodinSuhde, KoodiVersio> ylakoodiVersioJoin = root.join(YLAKOODI_VERSIO);
        Join<KoodiVersio, Koodi> ylakoodiJoin = ylakoodiVersioJoin.join(KOODI);
        Join<KoodistoVersioKoodiVersio, KoodistoVersio> koodistoVersio = ylakoodiVersioJoin.join(KOODISTO_VERSIOS).join(KOODISTO_VERSIO);
        Join<KoodiVersio, Koodi> koodisto = ylakoodiJoin.join(KOODISTO, JoinType.LEFT);

        ylakoodiVersioJoin.fetch(KOODI);
        ylakoodiVersioJoin.fetch(METADATAS);

        criteriaQuery.distinct(true);
        criteriaQuery.multiselect(ylakoodiVersioJoin, koodisto.get(KOODISTO_URI), koodisto.get(ORGANISAATIO_OID), koodistoVersio.get(VERSIO)).where(
                cb.and(cb.equal(root.get(SUHTEEN_TYYPPI), suhdeTyyppi), createKoodiRelationRestriction(cb, koodi, alakoodiVersioJoin, parent)));

        return convertSearchResultSet(em.createQuery(criteriaQuery).getResultList());
    }

    @Override
    public List<KoodiVersioWithKoodistoItem> listByParentRelation(KoodiUriAndVersioType parent, SuhteenTyyppi suhdeTyyppi) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = cb.createTupleQuery();
        Root<KoodinSuhde> root = criteriaQuery.from(KoodinSuhde.class);
        Join<KoodinSuhde, KoodiVersio> ylakoodiVersioJoin = root.join(YLAKOODI_VERSIO);
        Join<KoodiVersio, Koodi> koodi = ylakoodiVersioJoin.join(KOODI);

        Join<KoodinSuhde, KoodiVersio> alakoodiVersioJoin = root.join(ALAKOODI_VERSIO);
        Join<KoodiVersio, Koodi> alakoodiJoin = alakoodiVersioJoin.join(KOODI);
        Join<KoodistoVersioKoodiVersio, KoodistoVersio> koodistoVersio = alakoodiVersioJoin.join(KOODISTO_VERSIOS).join(KOODISTO_VERSIO);
        Join<KoodistoVersio, Koodisto> koodisto = alakoodiJoin.join(KOODISTO, JoinType.LEFT);

        alakoodiVersioJoin.fetch(KOODI);
        alakoodiVersioJoin.fetch(METADATAS);

        criteriaQuery.distinct(true);
        criteriaQuery.multiselect(alakoodiVersioJoin, koodisto.get(KOODISTO_URI), koodisto.get(ORGANISAATIO_OID), koodistoVersio.get(VERSIO)).where(
                cb.and(cb.equal(root.get(SUHTEEN_TYYPPI), suhdeTyyppi), createKoodiRelationRestriction(cb, koodi, ylakoodiVersioJoin, parent)));

        return convertSearchResultSet(em.createQuery(criteriaQuery).getResultList());
    }

    private static Predicate createKoodiRelationRestriction(CriteriaBuilder cb, Path<Koodi> koodiPath, Path<KoodiVersio> koodiVersioPath,
            KoodiUriAndVersioType koodi) {
        return cb.and(cb.equal(koodiPath.<String> get(KOODI_URI), koodi.getKoodiUri()), cb.equal(koodiVersioPath.get(VERSIO), koodi.getVersio()));
    }

    @Override
    public List<KoodiVersio> getKoodiVersios(KoodiUriAndVersioType... koodis) {
        if (koodis.length == 0) {
            return new ArrayList<KoodiVersio>();
        }
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<KoodiVersio> criteriaQuery = cb.createQuery(KoodiVersio.class);
        Root<KoodiVersio> root = criteriaQuery.from(KoodiVersio.class);

        final Join<KoodiVersio, Koodi> koodi = root.join(KOODI);
        root.fetch(KOODI);
        root.fetch(METADATAS, JoinType.LEFT);

        List<Predicate> restrictions = new ArrayList<Predicate>();
        for (KoodiUriAndVersioType kv : koodis) {
            restrictions.add(cb.and(cb.equal(koodi.<String> get(KOODI_URI), kv.getKoodiUri()), cb.equal(root.get(VERSIO), kv.getVersio())));
        }

        Predicate connectedRestrictions = null;
        if (restrictions.size() == 1) {
            connectedRestrictions = restrictions.get(0);
        } else {
            connectedRestrictions = cb.or(restrictions.toArray(new Predicate[restrictions.size()]));
        }

        criteriaQuery.distinct(true);
        criteriaQuery.select(root).where(connectedRestrictions);
        return em.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public boolean isLatestKoodiVersio(String koodiUri, Integer versio) {
        SearchKoodisCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(koodiUri);
        TypedQuery<KoodiVersio> query = createKoodiVersioQueryFromSearchCriteria(searchCriteria);
        return query.getSingleResult().getVersio().equals(versio);
    }

    @Override
    public Map<String, Integer> getLatestVersionNumbersForUris(String... koodiUris) {
        if(koodiUris == null || koodiUris.length == 0){
            return new HashMap<String, Integer>();
        }
        SearchKoodisCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(koodiUris);
        TypedQuery<KoodiVersio> query = createKoodiVersioQueryFromSearchCriteria(searchCriteria);

        HashMap<String, Integer> returnMap = new HashMap<String, Integer>();
        for (KoodiVersio result : query.getResultList()) {
            returnMap.put(result.getKoodi().getKoodiUri(), result.getVersio());
        }
        return returnMap;
    }
    
    private TypedQuery<KoodiVersio> createKoodiVersioQueryFromSearchCriteria(SearchKoodisCriteriaType searchCriteria) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<KoodiVersio> criteriaQuery = cb.createQuery(KoodiVersio.class);
        Root<KoodiVersio> root = criteriaQuery.from(KoodiVersio.class);
        final Join<KoodiVersio, Koodi> koodi = root.join(KOODI);
        root.fetch(KOODI);
        List<Predicate> restrictions = createRestrictionsForKoodiCriteria(cb, criteriaQuery, searchCriteria, koodi, root);
        criteriaQuery.select(root).where(cb.and(restrictions.toArray(new Predicate[restrictions.size()])));
        criteriaQuery.distinct(true);
        TypedQuery<KoodiVersio> query = em.createQuery(criteriaQuery);
        return query;
    }
    
    @Override
    public KoodiVersio insertNonFlush(KoodiVersio entity) {
        validate(entity);
        EntityManager em = getEntityManager();
        em.persist(entity);
        // Database must be synchronized after this by flushing
        return entity;
    }

    @Override
    public void flush() {
        getEntityManager().flush();
    }

    @Override
    public Map<KoodiVersio, KoodiVersio> getPreviousKoodiVersios(List<KoodiVersio> koodis) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        HashMap<KoodiVersio, KoodiVersio> results = new HashMap<>();
        
        for (KoodiVersio kv : koodis) {
            CriteriaQuery<KoodiVersio> query = cb.createQuery(KoodiVersio.class);
            Root<KoodiVersio> root = query.from(KoodiVersio.class);
            Join<KoodiVersio, Koodi> koodi = root.join(KOODI);
            Predicate koodiUriEqual = cb.equal(koodi.<String> get(KOODI_URI), kv.getKoodi().getKoodiUri());
            Predicate koodiVersioLessThan = cb.lessThan(root.<Integer> get(VERSIO), kv.getVersio());
            
            query.select(root).where(cb.and(koodiUriEqual, koodiVersioLessThan)).orderBy(cb.desc(root.<Integer> get(VERSIO)));
            List<KoodiVersio> resultList = em.createQuery(query).setMaxResults(1).getResultList();
            if(resultList.size() == 1){
                results.put(kv, resultList.get(0));
            }
        }
            
        return results;
    }
}
