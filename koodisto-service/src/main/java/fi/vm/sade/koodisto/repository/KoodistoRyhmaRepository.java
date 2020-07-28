package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.KoodistoRyhma;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface KoodistoRyhmaRepository extends CrudRepository<KoodistoRyhma, Long> {

    @EntityGraph("KoodistoRyhma.withKoodistosAndVersions")
    Optional<KoodistoRyhma> getById(Long id);

    boolean existsKoodistoRyhmaByKoodistoRyhmaUri(String koodistoRyhmaUri);

    KoodistoRyhma getByKoodistoRyhmaUri(String koodistoRyhmaUri);

    List<KoodistoRyhma> findByKoodistoRyhmaUriIn(List<String> koodistoRyhmaUris);

    @EntityGraph("KoodistoRyhma.withKoodistosAndVersions")
    List<KoodistoRyhma> findAll();
}
