package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KoodistoVersioKoodiVersioRepository extends JpaRepository<KoodistoVersioKoodiVersio, Long> {
    KoodistoVersioKoodiVersio findByKoodistoVersioIdAndKoodiVersioId(Long koodistoVersioId, Long koodiVersioId);

    List<KoodistoVersioKoodiVersio> findAllByKoodistoVersioAndKoodiVersio(Long koodistoVersioId, Long koodiId);

    List<KoodistoVersioKoodiVersio> findByKoodiVersio(Long koodiVersioId);

    List<KoodistoVersioKoodiVersio> findByKoodistoVersio(Long koodistoVersioId);

    // todo save ? KoodistoVersioKoodiVersio insertNonFlush(KoodistoVersioKoodiVersio koodistoVersioRelation);

    //void flush();
}
