package fi.vm.sade.koodisto.dao;

import java.util.List;
import java.util.Map;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;

public interface KoodiVersioDAO extends JpaDAO<KoodiVersio, Long> {
    List<KoodiVersioWithKoodistoItem> searchKoodis(SearchKoodisByKoodistoCriteriaType searchCriteria);

    List<KoodiVersioWithKoodistoItem> searchKoodis(SearchKoodisCriteriaType searchCriteria);

    List<KoodiVersio> getKoodiVersiosIncludedOnlyInKoodistoVersio(String koodistoUri, Integer koodistoVersio);

    List<KoodiVersio> getKoodiVersios(KoodiUriAndVersioType... koodis);

    List<KoodiVersioWithKoodistoItem> listByParentRelation(KoodiUriAndVersioType parent, SuhteenTyyppi suhdeTyyppi);

    List<KoodiVersioWithKoodistoItem> listByChildRelation(KoodiUriAndVersioType parent, SuhteenTyyppi suhdeTyyppi);

    List<KoodiVersio> getKoodiVersiosByKoodistoAndKoodiTila(Long koodistoVersioId, Tila koodiTila);

    KoodiVersio getPreviousKoodiVersio(String koodiUri, Integer koodiVersio);
    
    boolean isLatestKoodiVersio(String koodiUri, Integer versio);

    Map<String, Integer> getLatestVersionNumbersForUris(String... koodiUris);

    KoodiVersio insertNonFlush(KoodiVersio koodiVersio);

    void flush();

    Map<KoodiVersio, KoodiVersio> getPreviousKoodiVersios(List<KoodiVersio> koodis);
}
