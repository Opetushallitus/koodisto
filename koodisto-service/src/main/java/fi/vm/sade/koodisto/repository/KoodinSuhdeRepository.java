package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodinSuhde;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KoodinSuhdeRepository extends JpaRepository<KoodinSuhde, Long>, KoodinSuhdeRepositoryCustom {
}