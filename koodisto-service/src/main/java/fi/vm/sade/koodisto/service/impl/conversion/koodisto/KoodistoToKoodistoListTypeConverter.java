package fi.vm.sade.koodisto.service.impl.conversion.koodisto;

import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.types.common.KoodistoListType;
import fi.vm.sade.koodisto.service.types.common.KoodistoVersioListType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;

@Component("koodistoToKoodistoListTypeConverter")
public class KoodistoToKoodistoListTypeConverter extends AbstractFromDomainConverter<Koodisto, KoodistoListType> {

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

        if (converted.getKoodistoVersios().size() > 0) {
            Collections.sort(converted.getKoodistoVersios(), new Comparator<KoodistoVersioListType>() {

                @Override
                public int compare(KoodistoVersioListType o1, KoodistoVersioListType o2) {
                    return o2.getVersio() - o1.getVersio();
                }
            });

            converted.setLatestKoodistoVersio(converted.getKoodistoVersios().get(0));
        }

        return converted;
    }

}
