/**
 *
 */
package fi.vm.sade.koodisto.repository;

import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KoodistoRepository extends JpaRepository<Koodisto, Long> {

    Koodisto findByKoodistoUri(String koodistoUri);

    boolean existsByKoodistoUri(String koodistoUri);

    @Query(value = "select koodi.koodiuri as koodiUri, koodiversio.versio as koodiVersio, koodiversio.koodiarvo as koodiArvo,  " +
            "       koodimetadataFi.nimi as metadataNimiFI, koodimetadataFi.kuvaus as metadataKuvausFI, koodimetadataFi.lyhytnimi as metadataLyhytNimiFI, " +
            "       koodimetadataSv.nimi as metadataNimiSV, koodimetadataSv.kuvaus as metadataKuvausSV, koodimetadataSv.lyhytnimi as metadataLyhytNimiSV, " +
            "       koodimetadataEn.nimi as metadataNimiEN, koodimetadataEn.kuvaus as metadataKuvausEN, koodimetadataEn.lyhytnimi as metadataLyhytNimiEN " +
            "from koodisto " +
            "         join koodistoversio on koodisto.id = koodistoversio.koodisto_id " +
            "         join koodistoversio_koodiversio kk on koodistoversio.id = kk.koodistoversio_id " +
            "         join koodiversio on kk.koodiversio_id = koodiversio.id " +
            "         join koodi on koodiversio.koodi_id = koodi.id " +
            "         left join koodimetadata koodimetadataFi on koodiversio.id = koodimetadataFi.koodiversio_id and koodimetadataFi.kieli='FI' " +
            "         left join koodimetadata koodimetadataSv on koodiversio.id = koodimetadataSv.koodiversio_id and koodimetadataSv.kieli='SV' " +
            "         left join koodimetadata koodimetadataEn on koodiversio.id = koodimetadataEn.koodiversio_id and koodimetadataEn.kieli='EN' " +
            "where " +
            "    koodisto.koodistouri = ?1  " +
            "    and koodistoversio.versio = ?2", nativeQuery = true)
    List<FlatKoodiWithMetadata> findFlatKoodiWithMetadata(String koodistoUri, int koodistoVersio);

    @Query(value = "select koodi.koodiuri as koodiUri, koodiversio.versio as koodiVersio, " +
            "       suhdekoodi.koodiuri as suhdeKoodiuri, suhdekoodiversio.versio as suhdeVersio, " +
            "       koodinsuhde.alakoodistapassiivinen or koodinsuhde.ylakoodistapassiivinen as suhdeKoodiPassive, " +
            "       koodinsuhde.suhteentyyppi, " +
            "       koodinsuhde.alakoodiversio_id = koodiversio.id as isWithin " +
            "from koodisto " +
            "         join koodistoversio on koodisto.id = koodistoversio.koodisto_id " +
            "         join koodistoversio_koodiversio kk on koodistoversio.id = kk.koodistoversio_id " +
            "         join koodiversio on kk.koodiversio_id = koodiversio.id " +
            "         join koodi on koodiversio.koodi_id = koodi.id " +
            "         join koodinsuhde on koodiversio.id = koodinsuhde.alakoodiversio_id or koodiversio.id = koodinsuhde.ylakoodiversio_id " +
            "         join koodiversio suhdekoodiversio on  (koodinsuhde.ylakoodiversio_id = suhdekoodiversio.id or koodinsuhde.alakoodiversio_id = suhdekoodiversio.id) and suhdekoodiversio.id != koodiversio.id" +
            "         join koodi suhdekoodi on suhdekoodiversio.koodi_id = suhdekoodi.id " +
            "where " +
            "    koodisto.koodistouri = ?1  " +
            "    and koodistoversio.versio = ?2 " +
            "    and koodi.koodiuri = ?3", nativeQuery = true)
    List<FlatKoodiWithRelation> findFlatKoodiRelations(String koodistoUri, int koodistoVersio, String koodiUri);


    interface FlatKoodiWithMetadata {
        String getKoodiUri();

        int getKoodiVersio();

        String getKoodiArvo();

        String getMetadataNimiFI();

        String getMetadataLyhytNimiFI();

        String getMetadataKuvausFI();

        String getMetadataNimiSV();

        String getMetadataLyhytNimiSV();

        String getMetadataKuvausSV();

        String getMetadataNimiEN();

        String getMetadataLyhytNimiEN();

        String getMetadataKuvausEN();
    }

    interface FlatKoodiWithRelation {
        String getKoodiUri();

        int getKoodiVersio();

        String getKoodiArvo();

        SuhteenTyyppi getSuhteentyyppi();

        boolean getIsWithin();

        String getSuhdeKoodiuri();

        int getSuhdeVersio();

        boolean getSuhdeKoodiPassive();
    }
}
