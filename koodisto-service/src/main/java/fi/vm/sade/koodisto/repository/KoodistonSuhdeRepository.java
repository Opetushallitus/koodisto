package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistonSuhde;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface KoodistonSuhdeRepository extends CrudRepository<KoodistonSuhde, Long>, CustomKoodistonSuhdeRepository {

    Optional<KoodistonSuhde> findByAlakoodistoVersio(KoodistoVersio koodistoVersio);

    Optional<KoodistonSuhde> findByYlakoodistoVersio(KoodistoVersio koodistoVersio);

}
