package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.Koodi;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface KoodiRepository extends CrudRepository<Koodi, Long> {

    Optional<Koodi> findByKoodiUri(String koodiUri);

    boolean existsByKoodiUri(String koodiUri);

    Optional<Koodi> deleteByKoodiUri(String koodiUri);
    
}
