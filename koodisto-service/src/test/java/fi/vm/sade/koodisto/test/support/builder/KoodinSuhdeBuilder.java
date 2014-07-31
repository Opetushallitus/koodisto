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
        relation.setYlakoodiVersio(ylakoodiVersio);
        ylakoodiVersio.addAlakoodi(relation);
        return this;
    }

    public KoodinSuhdeBuilder setChildVersio(KoodiVersio alakoodiVersio) {
        relation.setAlakoodiVersio(alakoodiVersio);
        alakoodiVersio.addYlakoodi(relation);
        return this;
    }

    public KoodinSuhdeBuilder setSuhteenTyyppi(SuhteenTyyppi suhteenTyyppi) {
        relation.setSuhteenTyyppi(suhteenTyyppi);
        return this;
    }

    @Override
    public KoodinSuhde build() {
        return relation;
    }

}
