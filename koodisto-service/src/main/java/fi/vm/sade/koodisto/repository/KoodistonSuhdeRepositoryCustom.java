package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistonSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.types.common.KoodistoUriAndVersioType;

import java.util.List;

public interface KoodistonSuhdeRepositoryCustom {
    List<KoodistonSuhde> getRelations(KoodistoUriAndVersioType ylaKoodisto, List<KoodistoUriAndVersioType> alaKoodistos,
                                      SuhteenTyyppi st);

    void copyRelations(KoodistoVersio old, KoodistoVersio fresh);

    // TODO Save KoodistonSuhde insertNonFlushing(KoodistonSuhde koodistonSuhde);

    void deleteRelations(KoodistoUriAndVersioType ylaKoodisto, List<KoodistoUriAndVersioType> alaKoodistos, SuhteenTyyppi st);
}
