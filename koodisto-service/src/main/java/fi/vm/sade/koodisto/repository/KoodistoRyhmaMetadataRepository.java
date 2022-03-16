/**
 *
 */
package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface KoodistoRyhmaMetadataRepository extends CrudRepository<KoodistoRyhmaMetadata, Integer> {

}
