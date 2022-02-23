package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.Tila;

import java.util.List;

public interface KoodiRepositoryCustom {
    void deleteIncludingRelations(String koodiUri);

    List<KoodiVersio> getLatestCodeElementVersiosByUrisAndTila(List<String> koodiUris, Tila tila);

    boolean koodiUriExists(String koodiUri);

    // TODO Koodi insertNonFlush(Koodi koodi);

    // TODO void flush();
}
