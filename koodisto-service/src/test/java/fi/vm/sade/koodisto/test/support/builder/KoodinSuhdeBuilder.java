package fi.vm.sade.koodisto.test.support.builder;

import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;

public class KoodinSuhdeBuilder implements Builder<KoodinSuhde> {

    private KoodinSuhde relation;
    
    public KoodinSuhdeBuilder() {
        relation = new KoodinSuhde();
    }
    
    public KoodinSuhdeBuilder setParentVersio(KoodiVersio ylakoodiVersio) {
        if (ylakoodiVersio == null) {
            return this;
        }
        relation.setYlakoodiVersio(ylakoodiVersio);
        ylakoodiVersio.addAlakoodi(relation);
        return this;
    }

    public KoodinSuhdeBuilder setChildVersio(KoodiVersio alakoodiVersio) {
        if (alakoodiVersio == null) {
            return this;
        }
        relation.setAlakoodiVersio(alakoodiVersio);
        alakoodiVersio.addYlakoodi(relation);
        return this;
    }

    public KoodinSuhdeBuilder setSuhteenTyyppi(SuhteenTyyppi suhteenTyyppi) {
        relation.setSuhteenTyyppi(suhteenTyyppi);
        return this;
    }
    
    public KoodinSuhdeBuilder setChildPassive(boolean alaKoodiPassive) {
        relation.setAlaKoodiPassive(alaKoodiPassive);
        return this;
    }
    
    public KoodinSuhdeBuilder setParentPassive(boolean ylaKoodiPassive) {
        relation.setYlaKoodiPassive(ylaKoodiPassive);
        return this;
    }


    @Override
    public KoodinSuhde build() {
        return relation;
    }

}
