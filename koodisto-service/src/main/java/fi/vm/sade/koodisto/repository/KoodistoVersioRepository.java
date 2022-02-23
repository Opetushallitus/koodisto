package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoVersio;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KoodistoVersioRepository extends CrudRepository<KoodistoVersio, Long>, KoodistoVersioRepositoryCustom {

    List<KoodistoVersio> findByKoodiUriAndVersio(String koodiUri, Integer koodiVersio);

}
