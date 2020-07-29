package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoVersio;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface KoodistoVersioRepository extends CrudRepository<KoodistoVersio, Long>, CustomKoodistoVersioRepository {

    Optional<KoodistoVersio> findByKoodistoKoodistoUriAndVersio(String koodistoUri, Integer versio);

    boolean existsByKoodistoKoodistoUriAndVersio(String koodistoUri, Integer versio);

}
