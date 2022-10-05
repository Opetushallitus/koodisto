package fi.vm.sade.koodisto.service.conversion.impl.koodisto;

import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.service.types.common.KoodistoListType;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component("koodistoToKoodistoListTypeConverter")
public class KoodistoToKoodistoListTypeConverter implements AbstractFromDomainConverter<Koodisto, KoodistoListType> {

    @Override
    public KoodistoListType convert(Koodisto source) {
        KoodistoListType converted = new KoodistoListType();
        converted.setKoodistoUri(source.getKoodistoUri());
        converted.setLukittu(source.getLukittu());
        converted.setOmistaja(source.getOmistaja());
        converted.setOrganisaatioOid(source.getOrganisaatioOid());

        KoodistoVersioToKoodistoVersioListTypeConverter versioConverter = new KoodistoVersioToKoodistoVersioListTypeConverter();
        for (KoodistoVersio versio : source.getKoodistoVersios()) {
            converted.getKoodistoVersios().add(versioConverter.convert(versio));
        }

        if (!converted.getKoodistoVersios().isEmpty()) {
            Collections.sort(converted.getKoodistoVersios(), (o1, o2) -> o2.getVersio() - o1.getVersio());
            converted.setLatestKoodistoVersio(converted.getKoodistoVersios().get(0));
        }

        return converted;
    }

}
