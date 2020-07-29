package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;

public interface CustomKoodistoRepository {

    Integer getKoodistoVersionByCriteria(SearchKoodisByKoodistoCriteriaType searchCriteria);

}
