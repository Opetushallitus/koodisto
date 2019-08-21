/**
 *
 */
package fi.vm.sade.koodisto.dao;

import fi.vm.sade.koodisto.dao.impl.JpaDAO;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistonSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.types.common.KoodistoUriAndVersioType;

import java.util.List;

/**
 * @author
 */
public interface KoodistonSuhdeDAO extends JpaDAO<KoodistonSuhde, Long> {
    List<KoodistonSuhde> getRelations(KoodistoUriAndVersioType ylaKoodisto, List<KoodistoUriAndVersioType> alaKoodistos,
                                      SuhteenTyyppi st);
    
    void copyRelations(KoodistoVersio old, KoodistoVersio fresh);

    KoodistonSuhde insertNonFlushing(KoodistonSuhde koodistonSuhde);

    void deleteRelations(KoodistoUriAndVersioType ylaKoodisto, List<KoodistoUriAndVersioType> alaKoodistos, SuhteenTyyppi st);

    void flush();
}