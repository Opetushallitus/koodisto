package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface KoodistoVersioKoodiVersioRepository extends CrudRepository<KoodistoVersioKoodiVersio, Long> {

    Optional<KoodistoVersioKoodiVersio> findByKoodistoVersioIdAndKoodiVersioId(Long koodistoVersioId, Long koodiVersioId);

    List<KoodistoVersioKoodiVersio> findByKoodiVersioId(Long koodiVersioId);

    List<KoodistoVersioKoodiVersio> findByKoodistoVersioId(Long koodistoVersioId);

    List<KoodistoVersioKoodiVersio> findByKoodistoVersioIdAndKoodiVersioKoodiId(Long koodistoVersioId, Long koodiId);

}
