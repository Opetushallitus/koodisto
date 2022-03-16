/**
 *
 */
package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistonSuhde;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KoodistonSuhdeRepository extends JpaRepository<KoodistonSuhde, Long>, KoodistonSuhdeRepositoryCustom {
    int countByYlakoodistoVersio(KoodistoVersio versio);
    int countByAlakoodistoVersio(KoodistoVersio versio);
}