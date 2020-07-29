package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;

import java.util.List;
import java.util.Optional;

public interface CustomKoodistoVersioRepository {

    List<KoodistoVersio> searchKoodistos(SearchKoodistosCriteriaType searchCriteria);

    Optional<KoodistoVersio> getPreviousKoodistoVersio(String koodistUri, Integer versio);
}
