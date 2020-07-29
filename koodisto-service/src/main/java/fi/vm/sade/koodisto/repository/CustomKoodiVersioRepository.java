package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CustomKoodiVersioRepository {

    List<KoodiVersio> getKoodiVersiosIncludedOnlyInKoodistoVersio(String koodistoUri, Integer koodistoVersio);

    List<KoodiVersio> getKoodiVersios(KoodiUriAndVersioType... koodis);

    List<KoodiVersio> findByKoodistoUriAndVersio(String koodistoUri, Integer versio);

    Optional<KoodiVersio> getPreviousKoodiVersio(String koodiUri, Integer versio);

    Map<KoodiVersio, Optional<KoodiVersio>> getPreviousKoodiVersios(List<KoodiVersio> koodiVersios);

    boolean isLatestKoodiVersio(String koodiUri, Integer versio);

    Map<String, Integer> getLatestVersionNumbersForUris(String... koodiUris);

    List<KoodiVersioWithKoodistoItem> searchKoodis(SearchKoodisByKoodistoCriteriaType searchCriteria);

    List<KoodiVersioWithKoodistoItem> searchKoodis(SearchKoodisCriteriaType searchCriteria);

    List<KoodiVersioWithKoodistoItem> listByParentRelation(KoodiUriAndVersioType parent, SuhteenTyyppi suhdeTyyppi);

    List<KoodiVersioWithKoodistoItem> listByChildRelation(KoodiUriAndVersioType child, SuhteenTyyppi suhdeTyyppi);

}
