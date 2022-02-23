package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.Koodi;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KoodiRepository extends CrudRepository<Koodi, Long>, KoodiRepositoryCustom {
    Koodi findByByKoodiUri(String koodiUri);
}
