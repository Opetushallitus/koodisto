package fi.vm.sade.koodisto.test.support.builder;

import java.util.Date;

import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;
import fi.vm.sade.koodisto.model.Tila;

public class KoodiVersioBuilder implements Builder<KoodiVersio>{
    
    private KoodiVersio koodiVersio;
    
    private static long id = 11;

    public KoodiVersioBuilder() {
        this.koodiVersio = new KoodiVersio();
    }

    public KoodiVersioBuilder setKoodiVersio(Integer versio) {
        koodiVersio.setVersio(versio);
        return this;      
    }
    
    public KoodiVersioBuilder addMetadata(KoodiMetadata ... datas) {
        for (KoodiMetadata data : datas) {
            koodiVersio.addMetadata(data);
            data.setKoodiVersio(koodiVersio);
        }
        return this;
    }
        
    public KoodiVersioBuilder addKoodistoVersio(KoodistoVersio koodisto) {
        KoodistoVersioKoodiVersio kvkv = new KoodistoVersioKoodiVersio();
        kvkv.setKoodistoVersio(koodisto);
        kvkv.setKoodiVersio(koodiVersio);
        koodisto.addKoodiVersio(kvkv);
        koodiVersio.addKoodistoVersio(kvkv);
        return this;
    }
    
    public KoodiVersioBuilder setKoodi(Koodi koodi) {
        koodiVersio.setKoodi(koodi);
        return this;
    }

    public KoodiVersioBuilder setStartDate(Date startDate) {
        koodiVersio.setVoimassaAlkuPvm(startDate);
        return this;
    }
    
    public KoodiVersioBuilder setEndDate(Date endDate) {
        koodiVersio.setVoimassaLoppuPvm(endDate);
        return this;
    }

    public KoodiVersioBuilder setTila(Tila tila) {
        koodiVersio.setTila(tila);
        return this;
    }
    
    public KoodiVersioBuilder setLastUpdateDate(Date date) {
        koodiVersio.setPaivitysPvm(date);
        return this;
    }

    public KoodiVersioBuilder setKoodiValue(String koodiValue) {
        koodiVersio.setKoodiarvo(koodiValue);
        return this;
    }
    
    public KoodiVersioBuilder addChildRelation(KoodinSuhde relation) {
        koodiVersio.addAlakoodi(relation);
        relation.setYlakoodiVersio(koodiVersio);        
        return this;
    }
    
    public KoodiVersioBuilder addParentRelation(KoodinSuhde relation) {
        koodiVersio.addYlakoodi(relation);
        relation.setAlakoodiVersio(koodiVersio);
        return this;
    }
    
    public KoodiVersioBuilder setCreated(Date created) {
        koodiVersio.setLuotu(created);
        return this;
    }

    public KoodiVersio build() {
        if (koodiVersio.getPaivitysPvm() == null ) {
            koodiVersio.setPaivitysPvm(new Date());
        }
        if (koodiVersio.getId() == null) {
            koodiVersio.setId(id++);
        }
        return this.koodiVersio;
    }

}
