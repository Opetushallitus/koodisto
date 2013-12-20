package fi.vm.sade.koodisto.service.impl.conversion.koodisto;

import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.common.configuration.KoodistoConfiguration;
import fi.vm.sade.koodisto.dto.KoodistoListDto;
import fi.vm.sade.koodisto.dto.KoodistoVersioListDto;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;

@Component("koodistoToKoodistoListDtoConverter")
public class KoodistoToKoodistoListDtoConverter extends AbstractFromDomainConverter<Koodisto, KoodistoListDto> {

    @Autowired
    private KoodistoConfiguration koodistoConfiguration;

    @Autowired
    private KoodistoVersioToKoodistoVersioListDtoConverter koodistoVersioToKoodistoVersioListDtoConverter;

    @Override
    public KoodistoListDto convert(Koodisto source) {
        KoodistoListDto converted = new KoodistoListDto();
        converted.setKoodistoUri(source.getKoodistoUri());

        if (StringUtils.isNotBlank(converted.getKoodistoUri())) {
            converted.setResourceUri(koodistoConfiguration.getKoodistoResourceUri(converted.getKoodistoUri()));
        }

        converted.setLukittu(source.getLukittu());
        converted.setOmistaja(source.getOmistaja());
        converted.setOrganisaatioOid(source.getOrganisaatioOid());

        for (KoodistoVersio versio : source.getKoodistoVersios()) {
            converted.getKoodistoVersios().add(koodistoVersioToKoodistoVersioListDtoConverter.convert(versio));
        }

        if (converted.getKoodistoVersios().size() > 0) {
            Collections.sort(converted.getKoodistoVersios(), new Comparator<KoodistoVersioListDto>() {

                @Override
                public int compare(KoodistoVersioListDto o1, KoodistoVersioListDto o2) {
                    return o2.getVersio() - o1.getVersio();
                }
            });

            converted.setLatestKoodistoVersio(converted.getKoodistoVersios().get(0));
        }

        return converted;
    }

}
