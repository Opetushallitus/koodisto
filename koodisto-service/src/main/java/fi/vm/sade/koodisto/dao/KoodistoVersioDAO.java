package fi.vm.sade.koodisto.dao;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;

import java.util.List;

public interface KoodistoVersioDAO extends JpaDAO<KoodistoVersio, Long> {

    List<KoodistoVersio> searchKoodistos(SearchKoodistosCriteriaType searchCriteria);

    List<KoodistoVersio> getKoodistoVersiosForKoodiVersio(String koodiUri, Integer koodiVersio);

    KoodistoVersio getPreviousKoodistoVersio(String koodistoUri, Integer koodistoVersio);

    boolean koodistoVersioExists(String koodistoUri, Integer koodistoVersio);

    void flush();
}
