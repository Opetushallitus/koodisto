package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodiMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KoodiMetadataRepository extends JpaRepository<KoodiMetadata, Long>, KoodiMetadataRepositoryCustom {
}
