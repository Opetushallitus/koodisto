package fi.vm.sade.koodisto.service.business.changes.impl;

import com.google.common.base.Function;

import fi.vm.sade.koodisto.dto.KoodiChangesDto.SimpleCodeElementRelation;
import fi.vm.sade.koodisto.model.KoodinSuhde;

class KoodinSuhdeToSimpleCodeElementRelation implements Function<KoodinSuhde, SimpleCodeElementRelation> {
    private final String koodiUri;

    KoodinSuhdeToSimpleCodeElementRelation(String koodiUri) {
        this.koodiUri = koodiUri;
    }

    @Override
    public SimpleCodeElementRelation apply(KoodinSuhde input) {
        boolean isChild = koodiUri.equals(input.getYlakoodiVersio().getKoodi().getKoodiUri()) ? true : false;
        String uri = isChild ? input.getAlakoodiVersio().getKoodi().getKoodiUri() : input.getYlakoodiVersio().getKoodi().getKoodiUri();
        Integer versio = isChild ? input.getAlakoodiVersio().getVersio() : input.getYlakoodiVersio().getVersio();
        return new SimpleCodeElementRelation(uri, versio, input.getSuhteenTyyppi(), isChild);
    }
}
