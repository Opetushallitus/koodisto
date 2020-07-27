package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.Koodi;
import org.springframework.data.repository.CrudRepository;

public interface KoodiRepository extends CrudRepository<Koodi, Long> {
}
