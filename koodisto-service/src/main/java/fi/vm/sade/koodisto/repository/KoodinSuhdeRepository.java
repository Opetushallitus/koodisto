package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodinSuhde;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface KoodinSuhdeRepository extends CrudRepository<KoodinSuhde, Long>, CustomKoodinSuhdeRepository {

    List<KoodinSuhde> findByYlakoodiVersioKoodiKoodiUri(String koodiUri);

}
