package fi.vm.sade.koodisto.dao;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.koodisto.model.KoodistoRyhma;

import java.util.List;

/**
 * @author tommiha
 */
public interface KoodistoRyhmaDAO extends JpaDAO<KoodistoRyhma, Long> {

    List<KoodistoRyhma> listAllKoodistoRyhmas();

    KoodistoRyhma read(String ryhmaUri);

    List<KoodistoRyhma> findByUri(List<String> koodistoRyhmaUris);

    KoodistoRyhma findById(Long id);

}
