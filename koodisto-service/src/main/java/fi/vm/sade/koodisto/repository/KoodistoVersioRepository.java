package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoVersio;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KoodistoVersioRepository extends CrudRepository<KoodistoVersio, Long>, KoodistoVersioRepositoryCustom {
    boolean existsByKoodistoKoodistoUriAndVersio(String koodistoUri, Integer koodistoVersio);
}
