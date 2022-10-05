package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodiVersio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KoodiVersioRepository extends JpaRepository<KoodiVersio, Long>, KoodiVersioRepositoryCustom {
}
