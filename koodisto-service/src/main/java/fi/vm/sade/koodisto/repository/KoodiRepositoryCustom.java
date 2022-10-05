package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.Tila;

import java.util.List;

public interface KoodiRepositoryCustom {

    List<KoodiVersio> getLatestCodeElementVersiosByUrisAndTila(List<String> koodiUris, Tila tila);
}
