package fi.vm.sade.koodisto.dao;

import fi.vm.sade.koodisto.dao.impl.JpaDAO;
import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;

import java.util.List;

public interface KoodistoVersioKoodiVersioDAO extends JpaDAO<KoodistoVersioKoodiVersio, Long> {
    KoodistoVersioKoodiVersio findByKoodistoVersioAndKoodiVersio(Long koodistoVersioId, Long koodiVersioId);

    List<KoodistoVersioKoodiVersio> getByKoodistoVersioAndKoodi(Long koodistoVersioId, Long koodiId);

    List<KoodistoVersioKoodiVersio> getByKoodiVersio(Long koodiVersioId);

    List<KoodistoVersioKoodiVersio> getByKoodistoVersio(Long koodistoVersioId);

    KoodistoVersioKoodiVersio insertNonFlush(KoodistoVersioKoodiVersio koodistoVersioRelation);

    void flush();
}
