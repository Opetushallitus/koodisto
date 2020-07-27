package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;

import java.util.List;

public interface CustomKoodiVersioRepository {

    List<KoodiVersio> getKoodiVersiosIncludedOnlyInKoodistoVersio(String koodistoUri, Integer koodistoVersio);

    List<KoodiVersioWithKoodistoItem> searchKoodis(SearchKoodisByKoodistoCriteriaType searchCriteria);

    List<KoodiVersioWithKoodistoItem> searchKoodis(SearchKoodisCriteriaType searchCriteria);

}
