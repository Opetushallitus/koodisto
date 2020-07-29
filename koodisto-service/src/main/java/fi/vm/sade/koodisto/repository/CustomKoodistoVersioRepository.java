package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;

import java.util.List;
import java.util.Optional;

public interface CustomKoodistoVersioRepository {

    List<KoodistoVersio> searchKoodistos(SearchKoodistosCriteriaType searchCriteria);

    List<KoodistoVersio> findByKoodiUriAndVersio(String koodiUri, Integer versio);

    Optional<KoodistoVersio> getPreviousKoodistoVersio(String koodistoUri, Integer versio);

    Optional<Integer> getLatestKoodistoVersio(String koodistoUri);
}
