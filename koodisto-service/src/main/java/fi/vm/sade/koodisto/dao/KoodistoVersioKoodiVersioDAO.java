package fi.vm.sade.koodisto.dao;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;

import java.util.List;

public interface KoodistoVersioKoodiVersioDAO extends JpaDAO<KoodistoVersioKoodiVersio, Long> {
    public KoodistoVersioKoodiVersio findByKoodistoVersioAndKoodiVersio(Long koodistoVersioId, Long koodiVersioId);

    public List<KoodistoVersioKoodiVersio> getByKoodistoVersioAndKoodi(Long koodistoVersioId, Long koodiId);

    public List<KoodistoVersioKoodiVersio> getByKoodiVersio(Long koodiVersioId);

    public List<KoodistoVersioKoodiVersio> getByKoodistoVersio(Long koodistoVersioId);
}
