package fi.vm.sade.koodisto.dao;

import fi.vm.sade.koodisto.dao.impl.JpaDAO;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;

import java.util.List;

public interface KoodinSuhdeDAO extends JpaDAO<KoodinSuhde, Long> {
    List<KoodinSuhde> getRelations(KoodiUriAndVersioType ylaKoodi, List<KoodiUriAndVersioType> alaKoodis,
            SuhteenTyyppi st);

    Long getRelationsCount(KoodiUriAndVersioType ylaKoodi, List<KoodiUriAndVersioType> alaKoodis,
                           SuhteenTyyppi st);

    List<KoodinSuhde> getRelations(String ylakoodiUri);
    
    void massRemove(List<KoodinSuhde> entityList);

    KoodinSuhde insertNonFlush(KoodinSuhde koodinSuhde);
    void flush();

    List<KoodinSuhde> getWithinRelations(KoodiUriAndVersioType ylaKoodi, List<KoodiUriAndVersioType> alaKoodis, SuhteenTyyppi st);
}