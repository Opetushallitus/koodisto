package fi.vm.sade.koodisto.util;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.koodisto.service.types.KoodiBaseSearchCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoVersioSelectionType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisVersioSelectionType;
import fi.vm.sade.koodisto.service.types.common.TilaType;

/**
 * Utility class to help create koodi search criteria for the most common
 * searches
 * 
 * @author wuoti
 * 
 */
public abstract class KoodiServiceSearchCriteriaBuilder {

    private static SearchKoodisCriteriaType createKoodiSearchCriteriaForUris(final String... koodiUris) {
        SearchKoodisCriteriaType searchType = new SearchKoodisCriteriaType();
        searchType.getKoodiUris().addAll(Arrays.asList(koodiUris));
        return searchType;
    }

    private static SearchKoodisByKoodistoCriteriaType createKoodisByKoodistoSearchCriteriaForKoodistoUri(
            final String koodistoUri) {
        SearchKoodisByKoodistoCriteriaType searchType = new SearchKoodisByKoodistoCriteriaType();
        searchType.setKoodistoUri(koodistoUri);
        return searchType;
    }

    public static SearchKoodisCriteriaType latestKoodisByUris(final String... koodiUri) {
        SearchKoodisCriteriaType searchType = createKoodiSearchCriteriaForUris(koodiUri);
        searchType.setKoodiVersioSelection(SearchKoodisVersioSelectionType.LATEST);

        return searchType;
    }

    public static SearchKoodisCriteriaType latestAcceptedKoodiByUri(final String koodiUri) {
        SearchKoodisCriteriaType searchType = latestKoodisByUris(koodiUri);
        searchType.getKoodiTilas().add(TilaType.HYVAKSYTTY);

        return searchType;
    }

    public static SearchKoodisCriteriaType latestValidAcceptedKoodiByUri(final String koodiUri) {
        SearchKoodisCriteriaType searchType = latestAcceptedKoodiByUri(koodiUri);
        searchType.setValidAt(DateHelper.DateToXmlCal(new Date()));

        return searchType;
    }

    public static SearchKoodisCriteriaType koodiByUriAndVersion(final String koodiUri, final Integer koodiVersio) {
        SearchKoodisCriteriaType searchType = createKoodiSearchCriteriaForUris(koodiUri);
        searchType.setKoodiVersioSelection(SearchKoodisVersioSelectionType.SPECIFIC);
        searchType.setKoodiVersio(koodiVersio);

        return searchType;
    }

    public static SearchKoodisByKoodistoCriteriaType koodisByKoodistoUri(final String koodistoUri) {
        SearchKoodisByKoodistoCriteriaType searchType = createKoodisByKoodistoSearchCriteriaForKoodistoUri(koodistoUri);
        searchType.setKoodistoVersioSelection(SearchKoodisByKoodistoVersioSelectionType.LATEST);
        return searchType;
    }

    public static SearchKoodisByKoodistoCriteriaType validAcceptedKoodisByKoodistoUriAndKoodistoVersio(
            final String koodistoUri, final Integer koodistoVersio) {
        SearchKoodisByKoodistoCriteriaType searchType = koodisByKoodistoUriAndKoodistoVersio(koodistoUri,
                koodistoVersio);

        KoodiBaseSearchCriteriaType koodiCriteria = new KoodiBaseSearchCriteriaType();
        koodiCriteria.getKoodiTilas().add(TilaType.HYVAKSYTTY);
        koodiCriteria.setValidAt(DateHelper.DateToXmlCal(new Date()));
        searchType.setKoodiSearchCriteria(koodiCriteria);

        return searchType;
    }

    public static SearchKoodisByKoodistoCriteriaType koodisByKoodistoUri(final List<String> koodiUris,
            final String koodistoUri) {
        SearchKoodisByKoodistoCriteriaType searchType = koodisByKoodistoUri(koodistoUri);

        KoodiBaseSearchCriteriaType koodiCriteria = new KoodiBaseSearchCriteriaType();
        koodiCriteria.getKoodiUris().addAll(koodiUris);
        searchType.setKoodiSearchCriteria(koodiCriteria);

        return searchType;
    }

    public static SearchKoodisByKoodistoCriteriaType koodisByKoodistoUriAndKoodistoVersio(final List<String> koodiUris,
            final String koodistoUri, final Integer koodistoVersio) {
        SearchKoodisByKoodistoCriteriaType searchType = koodisByKoodistoUriAndKoodistoVersio(koodistoUri,
                koodistoVersio);

        KoodiBaseSearchCriteriaType koodiCriteria = new KoodiBaseSearchCriteriaType();
        koodiCriteria.getKoodiUris().addAll(koodiUris);
        searchType.setKoodiSearchCriteria(koodiCriteria);

        return searchType;
    }

    public static SearchKoodisByKoodistoCriteriaType koodisByKoodistoUriAndKoodistoVersio(final String koodistoUri,
            final Integer koodistoVersio) {
        SearchKoodisByKoodistoCriteriaType searchType = createKoodisByKoodistoSearchCriteriaForKoodistoUri(koodistoUri);
        searchType.setKoodistoVersioSelection(SearchKoodisByKoodistoVersioSelectionType.SPECIFIC);
        searchType.setKoodistoVersio(koodistoVersio);
        return searchType;
    }

    public static SearchKoodisByKoodistoCriteriaType koodisByArvoAndKoodistoUri(final String koodiArvo,
            final String koodistoUri) {
        SearchKoodisByKoodistoCriteriaType searchType = koodisByKoodistoUri(koodistoUri);

        KoodiBaseSearchCriteriaType koodiSearchCriteria = new KoodiBaseSearchCriteriaType();
        koodiSearchCriteria.setKoodiArvo(koodiArvo);
        searchType.setKoodiSearchCriteria(koodiSearchCriteria);

        return searchType;
    }

    public static SearchKoodisByKoodistoCriteriaType koodisByArvoAndKoodistoUriAndKoodistoVersio(
            final String koodiArvo, final String koodistoUri, final Integer koodistoVersio) {
        SearchKoodisByKoodistoCriteriaType searchType = koodisByArvoAndKoodistoUri(koodiArvo, koodistoUri);
        searchType.setKoodistoVersioSelection(SearchKoodisByKoodistoVersioSelectionType.SPECIFIC);
        searchType.setKoodistoVersio(koodistoVersio);
        return searchType;
    }

    public static SearchKoodisCriteriaType koodiVersiosByUri(final String koodiUri) {
        SearchKoodisCriteriaType searchType = createKoodiSearchCriteriaForUris(koodiUri);
        searchType.setKoodiVersioSelection(SearchKoodisVersioSelectionType.ALL);
        return searchType;
    }

    public static SearchKoodisCriteriaType koodiVersiosByUriAndArvo(final String koodiUri, final String koodiArvo) {
        SearchKoodisCriteriaType searchType = new SearchKoodisCriteriaType();
        searchType.getKoodiUris().add(koodiUri);
        searchType.setKoodiArvo(koodiArvo);
        searchType.setKoodiVersioSelection(SearchKoodisVersioSelectionType.ALL);
        return searchType;
    }

    public static SearchKoodisCriteriaType koodiVersiosByUriAndTila(final String koodiUri, TilaType... tilas) {
        SearchKoodisCriteriaType searchType = koodiVersiosByUri(koodiUri);
        if (tilas != null && tilas.length > 0) {
            for (TilaType t : tilas) {
                searchType.getKoodiTilas().add(t);
            }
        }

        return searchType;
    }
}
