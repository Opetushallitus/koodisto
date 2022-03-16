package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoVersio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KoodistoVersioRepository extends JpaRepository<KoodistoVersio, Long>, KoodistoVersioRepositoryCustom {
    boolean existsByKoodistoKoodistoUriAndVersio(String koodistoUri, Integer koodistoVersio);
}
