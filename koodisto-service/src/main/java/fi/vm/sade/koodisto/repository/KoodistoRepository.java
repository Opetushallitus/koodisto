package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.Koodisto;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface KoodistoRepository extends CrudRepository<Koodisto, Long>, CustomKoodistoRepository {

    Optional<Koodisto> findByKoodistoUri(String koodistoUri);

    boolean existsByKoodistoUri(String koodistoUri);

}
