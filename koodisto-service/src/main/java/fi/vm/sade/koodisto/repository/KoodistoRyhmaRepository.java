package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoRyhma;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KoodistoRyhmaRepository extends CrudRepository<KoodistoRyhma, Long> {

    List<KoodistoRyhma> findAll();

    List<KoodistoRyhma> findAllByKoodistoRyhmaUriIn(List<String> koodistoRyhmaUris);

    Optional<KoodistoRyhma> findById(Long id);

    Optional<KoodistoRyhma> findByKoodistoRyhmaUri(String koodistoRyhmaUris);

    boolean existsByKoodistoRyhmaUri(String koodistoRyhmaUri);

    @Query(value = "SELECT * FROM koodistoryhma kr WHERE NOT EXISTS(SELECT 1 FROM koodistoryhma_koodisto kk WHERE kk.koodistoryhma_id = kr.id )", nativeQuery = true)
    List<KoodistoRyhma> findEmptyKoodistoRyhma();
}
