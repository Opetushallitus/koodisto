package fi.vm.sade.koodisto.test.support.builder;

import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.Koodisto;

public class KoodiBuilder implements Builder<Koodi> {

    private Koodi koodi;

    public KoodiBuilder() {
        koodi = new Koodi();
    }

    public KoodiBuilder setKoodisto(Koodisto koodisto) {
        koodi.setKoodisto(koodisto);
        return this;
    }

    public KoodiBuilder setKoodiUri(String koodiUri) {
        koodi.setKoodiUri(koodiUri);
        return this;
    }

    @Override
    public Koodi build() {
        return koodi;
    }
}
