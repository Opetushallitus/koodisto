/**
 *
 */
package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.Koodisto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KoodistoRepository extends CrudRepository<Koodisto, Long> {

    Koodisto findByKoodistoUri(String koodistoUri);

    void deleteByKoodistoUri(String koodistoUri);

    boolean existsByKoodistoUri(String koodistoUri);

}
