package fi.vm.sade.koodisto.dao;

import java.util.List;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.koodisto.model.KoodistoRyhma;

/**
 * @author tommiha
 */
public interface KoodistoRyhmaDAO extends JpaDAO<KoodistoRyhma, Long> {

    List<KoodistoRyhma> listAllKoodistoRyhmas();

    KoodistoRyhma read(String ryhmaUri);

    List<KoodistoRyhma> findByUri(List<String> koodistoRyhmaUris);

}
