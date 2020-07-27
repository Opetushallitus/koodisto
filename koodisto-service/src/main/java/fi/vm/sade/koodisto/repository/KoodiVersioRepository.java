package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodiVersio;
import org.springframework.data.repository.CrudRepository;

public interface KoodiVersioRepository extends CrudRepository<KoodiVersio, Long>, CustomKoodiVersioRepository {

}
