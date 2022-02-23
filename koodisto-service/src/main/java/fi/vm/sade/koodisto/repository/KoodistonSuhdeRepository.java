/**
 *
 */
package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistonSuhde;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KoodistonSuhdeRepository extends CrudRepository<KoodistonSuhde, Long>, KoodistonSuhdeRepositoryCustom {
}