package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.Tila;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface KoodiVersioRepository extends CrudRepository<KoodiVersio, Long>, CustomKoodiVersioRepository {

    List<KoodiVersio> findByKoodiKoodistoIdAndTila(Long koodistoId, Tila tila);


}
