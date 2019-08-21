package fi.vm.sade.koodisto.util;

import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosVersioSelectionType;
import fi.vm.sade.koodisto.service.types.common.TilaType;

import java.util.Arrays;
import java.util.Date;

/**
 * Utility class to help create koodisto search criteria for the most common
 * searches
 * 
 * @author wuoti
 * 
 */
public abstract class KoodistoServiceSearchCriteriaBuilder {
    private static SearchKoodistosCriteriaType createKoodistoSearchCriteriaForUri(final String... koodistoUris) {
        SearchKoodistosCriteriaType searchType = new SearchKoodistosCriteriaType();
        searchType.getKoodistoUris().addAll(Arrays.asList(koodistoUris));
        return searchType;
    }

    public static SearchKoodistosCriteriaType latestCodes() {
        SearchKoodistosCriteriaType searchType = new SearchKoodistosCriteriaType();
        searchType.setKoodistoVersioSelection(SearchKoodistosVersioSelectionType.LATEST);
        return searchType;
    }

    public static SearchKoodistosCriteriaType latestKoodistoByUri(final String koodistoUri) {
        SearchKoodistosCriteriaType searchType = createKoodistoSearchCriteriaForUri(koodistoUri);
        searchType.setKoodistoVersioSelection(SearchKoodistosVersioSelectionType.LATEST);
        return searchType;
    }

    public static SearchKoodistosCriteriaType koodistoByUriAndVersio(final String koodistoUri,
            final Integer koodistoVersio) {
        SearchKoodistosCriteriaType searchType = createKoodistoSearchCriteriaForUri(koodistoUri);
        searchType.setKoodistoVersio(koodistoVersio);
        searchType.setKoodistoVersioSelection(SearchKoodistosVersioSelectionType.SPECIFIC);
        return searchType;
    }

    public static SearchKoodistosCriteriaType koodistoVersiosByUri(final String koodistoUri) {
        SearchKoodistosCriteriaType searchType = createKoodistoSearchCriteriaForUri(koodistoUri);
        searchType.setKoodistoVersioSelection(SearchKoodistosVersioSelectionType.ALL);
        return searchType;
    }

    public static SearchKoodistosCriteriaType latestAcceptedKoodistoByUri(final String koodistoUri) {
        SearchKoodistosCriteriaType searchType = latestKoodistoByUri(koodistoUri);
        searchType.getKoodistoTilas().add(TilaType.HYVAKSYTTY);
        return searchType;
    }

    public static SearchKoodistosCriteriaType latestValidAcceptedKoodistoByUri(final String koodistoUri) {
        SearchKoodistosCriteriaType searchType = latestAcceptedKoodistoByUri(koodistoUri);
        searchType.setValidAt(DateHelper.DateToXmlCal(new Date()));
        return searchType;
    }

    public static SearchKoodistosCriteriaType latestKoodistosByUri(String... koodistoUris) {
        SearchKoodistosCriteriaType searchType = createKoodistoSearchCriteriaForUri(koodistoUris);
        searchType.setKoodistoVersioSelection(SearchKoodistosVersioSelectionType.LATEST);
        return searchType;
    }
}
