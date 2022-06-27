package fi.vm.sade.koodisto.test.support.builder;

import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodiVersio;

public class KoodiMetadataBuilder implements Builder<KoodiMetadata> {

    private final KoodiMetadata metadata;

    public KoodiMetadataBuilder() {
        this.metadata = new KoodiMetadata();
    }

    public KoodiMetadataBuilder setNimi(String nimi) {
        metadata.setNimi(nimi);
        return this;
    }

    public KoodiMetadataBuilder setKuvaus(String kuvaus) {
        metadata.setKuvaus(kuvaus);
        return this;
    }

    public KoodiMetadataBuilder setLyhytNimi(String lyhytNimi) {
        metadata.setLyhytNimi(lyhytNimi);
        return this;
    }

    public KoodiMetadataBuilder setKieli(Kieli kieli) {
        metadata.setKieli(kieli);
        return this;
    }

    public KoodiMetadataBuilder setKoodiVersio(KoodiVersio koodiVersio) {
        metadata.setKoodiVersio(koodiVersio);
        koodiVersio.addMetadata(metadata);
        return this;
    }

    @Override
    public KoodiMetadata build() {
        return metadata;
    }

}
