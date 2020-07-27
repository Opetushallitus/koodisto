package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;

import java.util.List;

public interface CustomKoodistoVersioRepository {

    List<KoodistoVersio> searchKoodistos(SearchKoodistosCriteriaType searchCriteria);

    KoodistoVersio getPreviousKoodistoVersio(String koodistoUri, Integer koodistoVersio);

}
