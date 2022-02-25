package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoRyhma;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KoodistoRyhmaRepository extends CrudRepository<KoodistoRyhma, Long> {

    List<KoodistoRyhma> findAll(); // TODO palauttaa kaikki

    KoodistoRyhma findByKoodistoRyhmaUri(String koodistoRyhmaUri);

    List<KoodistoRyhma> findAllByKoodistoRyhmaUri(List<String> koodistoRyhmaUris);

    Optional<KoodistoRyhma> findById(Long id);

    boolean existsByKoodistoRyhmaUri(String koodistoRyhmaUri);

}
