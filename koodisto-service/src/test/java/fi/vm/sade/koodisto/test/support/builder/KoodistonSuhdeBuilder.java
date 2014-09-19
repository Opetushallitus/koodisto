package fi.vm.sade.koodisto.test.support.builder;

import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistonSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;

public class KoodistonSuhdeBuilder implements Builder<KoodistonSuhde> {

    private KoodistonSuhde relation;

    public KoodistonSuhdeBuilder() {
        relation = new KoodistonSuhde();
    }

    public KoodistonSuhdeBuilder setParentVersio(KoodistoVersio ylakoodiVersio) {
        if (ylakoodiVersio == null) {
            return this;
        }
        relation.setYlakoodistoVersio(ylakoodiVersio);
        ylakoodiVersio.getAlakoodistos().add(relation);
        return this;
    }

    public KoodistonSuhdeBuilder setChildVersio(KoodistoVersio alakoodiVersio) {
        if (alakoodiVersio == null) {
            return this;
        }
        relation.setAlakoodistoVersio(alakoodiVersio);
        alakoodiVersio.getYlakoodistos().add(relation);
        return this;
    }

    public KoodistonSuhdeBuilder setSuhteenTyyppi(SuhteenTyyppi suhteenTyyppi) {
        relation.setSuhteenTyyppi(suhteenTyyppi);
        return this;
    }

    public KoodistonSuhdeBuilder setChildPassive(boolean alaKoodistoPassive) {
        relation.setAlaKoodistoPassive(alaKoodistoPassive);
        return this;
    }

    public KoodistonSuhdeBuilder setParentPassive(boolean ylaKoodistoPassive) {
        relation.setYlaKoodistoPassive(ylaKoodistoPassive);
        return this;
    }

    @Override
    public KoodistonSuhde build() {
        return relation;
    }
}
