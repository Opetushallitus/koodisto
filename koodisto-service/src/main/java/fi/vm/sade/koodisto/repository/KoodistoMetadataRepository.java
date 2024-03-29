/**
 *
 */
package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoMetadata;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KoodistoMetadataRepository extends CrudRepository<KoodistoMetadata, Integer>, KoodistoMetadataRepositoryCustom {

    boolean existsByNimi(String nimi);
}
