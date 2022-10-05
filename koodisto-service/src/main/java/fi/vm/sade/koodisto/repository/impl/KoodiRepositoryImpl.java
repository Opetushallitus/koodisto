/**
 *
 */
package fi.vm.sade.koodisto.repository.impl;

import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.repository.KoodiRepository;
import fi.vm.sade.koodisto.repository.KoodiRepositoryCustom;
import fi.vm.sade.koodisto.repository.KoodistoVersioKoodiVersioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.List;

public class KoodiRepositoryImpl implements KoodiRepositoryCustom {

    @Autowired
    EntityManager em;

    @Autowired
    KoodistoVersioKoodiVersioRepository koodistoVersioKoodiVersioRepository;

    @Autowired
    @Lazy
    KoodiRepository koodiRepository;

    
    @Override
    public List<KoodiVersio> getLatestCodeElementVersiosByUrisAndTila(List<String> koodiUris, Tila tila) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        
        CriteriaQuery<KoodiVersio> cquery = cb.createQuery(KoodiVersio.class);
        Root<KoodiVersio> versio = cquery.from(KoodiVersio.class);
        Join<Koodi, KoodiVersio> join = versio.join("koodi", JoinType.LEFT);

        Predicate uriRestriction = join.<String> get("koodiUri").in(koodiUris);
        Predicate tilaRestriction = cb.equal(versio.get("tila"), Tila.LUONNOS);
        
        Predicate restrictions = cb.and(uriRestriction, tilaRestriction);
        cquery.distinct(true);
        
        cquery.select(versio).where(restrictions);

        return em.createQuery(cquery).getResultList();
    }
}
