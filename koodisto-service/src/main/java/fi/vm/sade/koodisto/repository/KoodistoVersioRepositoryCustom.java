package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;

import java.util.List;
import java.util.Optional;

public interface KoodistoVersioRepositoryCustom {
    List<KoodistoVersio> searchKoodistos(SearchKoodistosCriteriaType searchCriteria);

    KoodistoVersio getPreviousKoodistoVersio(String koodistoUri, Integer koodistoVersio);

    Optional<Integer> findLatestVersioByKoodistoUri(String koodistoUri);

    List<KoodistoVersio> getKoodistoVersiosForKoodiVersio(String koodiUri, Integer koodiVersio);
}
