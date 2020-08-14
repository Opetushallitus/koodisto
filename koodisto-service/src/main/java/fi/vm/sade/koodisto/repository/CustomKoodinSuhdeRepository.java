package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;

import java.util.List;

public interface CustomKoodinSuhdeRepository {

    /*
    TODO: nimeä metodit paremmin
          - paremmin CrudRepositoryn nimeämiskäytäntöjen mukaisesti
          - käyttäen samoja nimityksiä, kuin luokassa ja kannassa
     */

    List<KoodinSuhde> getRelations(KoodiUriAndVersioType ylaKoodi, List<KoodiUriAndVersioType> alaKoodis,
                                   SuhteenTyyppi st);

    Long getRelationsCount(KoodiUriAndVersioType ylaKoodi, List<KoodiUriAndVersioType> alaKoodis,
                           SuhteenTyyppi st);

    List<KoodinSuhde> getWithinRelations(KoodiUriAndVersioType ylaKoodi, List<KoodiUriAndVersioType> alaKoodis, SuhteenTyyppi st);


}
