package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KoodistoVersioKoodiVersioRepository extends JpaRepository<KoodistoVersioKoodiVersio, Long> {
    KoodistoVersioKoodiVersio findByKoodistoVersioIdAndKoodiVersioId(Long koodistoVersioId, Long koodiVersioId);
}
