package fi.vm.sade.koodisto.test.support.builder;

import java.util.Date;

import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistonSuhde;
import fi.vm.sade.koodisto.model.Tila;

public class KoodistoVersioBuilder implements Builder<KoodistoVersio> {

    private KoodistoVersio kv;
    
    private static long id = 1;

    public KoodistoVersioBuilder() {
        this.kv = new KoodistoVersio();
    }
    
    public KoodistoVersioBuilder setVersio(Integer versio) {
        kv.setVersio(versio);
        return this;
    }
    
    public KoodistoVersioBuilder addMetadata(Kieli kieli, String name, String description) {
        KoodistoMetadata data = new KoodistoMetadata();
        data.setKieli(kieli);
        data.setNimi(name);
        data.setKuvaus(description);
        kv.addMetadata(data);
        return this;
    }
    
    public KoodistoVersioBuilder setMetadatas(KoodistoMetadata ... koodistoMetadatas) {
        for (KoodistoMetadata data : koodistoMetadatas) {
            kv.addMetadata(data);
        }
        return this;
    }
    
    public KoodistoVersioBuilder setKoodisto(Koodisto koodisto) {
        kv.setKoodisto(koodisto);
        koodisto.addKoodistoVersion(kv);
        return this;
    }
    
    public KoodistoVersioBuilder setTila(Tila tila) {
        kv.setTila(tila);
        return this;
    }
    
    public KoodistoVersioBuilder setStartDate(Date startDate) {
        kv.setVoimassaAlkuPvm(startDate);
        return this;
    }
    
    public KoodistoVersioBuilder setEndDate(Date endDate) {
        kv.setVoimassaLoppuPvm(endDate);
        return this;
    }
    
    public KoodistoVersioBuilder addParentRelation(KoodistonSuhde ks) {
        kv.getYlakoodistos().add(ks);
        ks.setAlakoodistoVersio(kv);
        return this;
    }

    public KoodistoVersioBuilder addChildRelation(KoodistonSuhde ks) {
        kv.getAlakoodistos().add(ks);
        ks.setYlakoodistoVersio(kv);
        return this;
    }
    
    @Override
    public KoodistoVersio build() {
        if (kv.getId() == null) {
            kv.setId(id++);
        }
        if (kv.getPaivitysPvm() == null) {
            kv.setPaivitysPvm(new Date());
        }
        if (kv.getVoimassaAlkuPvm() == null) {
            kv.setVoimassaAlkuPvm(new Date(1));
        }
        return kv;
    }

}
