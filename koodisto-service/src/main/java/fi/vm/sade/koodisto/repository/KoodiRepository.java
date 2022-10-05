package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.Koodi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KoodiRepository extends JpaRepository<Koodi, Long>, KoodiRepositoryCustom {

    Koodi findByKoodiUri(String koodiUri);

    void deleteById(Long id);

    boolean existsByKoodiUri(String koodiUri);
}
