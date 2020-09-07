package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodinSuhde;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface KoodinSuhdeRepository extends JpaRepository<KoodinSuhde, Long>, CustomKoodinSuhdeRepository {

    List<KoodinSuhde> findByYlakoodiVersioKoodiKoodiUri(String koodiUri);

    @Transactional
    @Modifying
    @Query("delete from KoodinSuhde k where k.id in (:idList)")
    void massRemove(List<Long> idList);

}
