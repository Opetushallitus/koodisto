/**
 *
 */
package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoMetadata;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KoodistoMetadataRepository extends CrudRepository<KoodistoMetadata, Integer>, KoodistoMetadataRepositoryCustom {

    //List<KoodistoMetadata> findAllByKoodistoUri(String koodistoUri);

    boolean existsByNimi(String nimi);
}
