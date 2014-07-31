package fi.vm.sade.koodisto.test.support.builder;

import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;

public class KoodistoVersioBuilder implements Builder<KoodistoVersio> {

    private KoodistoVersio kv;

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

    @Override
    public KoodistoVersio build() {
        return kv;
    }
    
    

}
