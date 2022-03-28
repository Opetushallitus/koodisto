package fi.vm.sade.koodisto.service.conversion.impl.koodisto;

import com.google.common.base.Strings;
import fi.vm.sade.koodisto.dto.KoodistoListDto;
import fi.vm.sade.koodisto.dto.KoodistoVersioListDto;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.properties.OphProperties;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;

@Component("koodistoToKoodistoListDtoConverter")
public class KoodistoToKoodistoListDtoConverter extends AbstractFromDomainConverter<Koodisto, KoodistoListDto> {

    private OphProperties ophProperties;

    private KoodistoVersioToKoodistoVersioListDtoConverter koodistoVersioToKoodistoVersioListDtoConverter;

    public KoodistoToKoodistoListDtoConverter(OphProperties ophProperties, KoodistoVersioToKoodistoVersioListDtoConverter koodistoVersioToKoodistoVersioListDtoConverter) {
        this.ophProperties = ophProperties;
        this.koodistoVersioToKoodistoVersioListDtoConverter = koodistoVersioToKoodistoVersioListDtoConverter;
    }

    @Override
    public KoodistoListDto convert(Koodisto source) {
        KoodistoListDto converted = new KoodistoListDto();
        converted.setKoodistoUri(source.getKoodistoUri());

        if (!Strings.isNullOrEmpty(converted.getKoodistoUri())) {
            String resourceUri = MessageFormat.format(ophProperties.url("koodistoUriFormat"), converted.getKoodistoUri());
            converted.setResourceUri(resourceUri);
        }

        converted.setLukittu(source.getLukittu());
        converted.setOmistaja(source.getOmistaja());
        converted.setOrganisaatioOid(source.getOrganisaatioOid());

        for (KoodistoVersio versio : source.getKoodistoVersios()) {
            converted.getKoodistoVersios().add(koodistoVersioToKoodistoVersioListDtoConverter.convert(versio));
        }

        if (!converted.getKoodistoVersios().isEmpty()) {
            Collections.sort(converted.getKoodistoVersios(), (o1, o2) -> o2.getVersio() - o1.getVersio());

            converted.setLatestKoodistoVersio(converted.getKoodistoVersios().get(0));
        }

        return converted;
    }

}
