package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;

public interface CustomKoodistoRepository {

    boolean koodistoUriExists(String koodistoUri);

    Integer getKoodistoVersionByCriteria(SearchKoodisByKoodistoCriteriaType searchCriteria);

}
