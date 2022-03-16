package fi.vm.sade.koodisto.service.conversion.impl.koodistoryhma;

import fi.vm.sade.koodisto.dto.KoodistoRyhmaListDto;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.service.conversion.impl.koodisto.KoodistoToKoodistoListDtoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("koodistoRyhmaToKoodistoRyhmaListDtoConverter")
public class KoodistoRyhmaToKoodistoRyhmaListDtoConverter extends AbstractFromDomainConverter<KoodistoRyhma, KoodistoRyhmaListDto> {

    private KoodistoToKoodistoListDtoConverter koodistoToKoodistoListDtoConverter;

    public KoodistoRyhmaToKoodistoRyhmaListDtoConverter(KoodistoToKoodistoListDtoConverter koodistoToKoodistoListDtoConverter) {
        this.koodistoToKoodistoListDtoConverter = koodistoToKoodistoListDtoConverter;
    }

    @Override
    public KoodistoRyhmaListDto convert(KoodistoRyhma source) {
        KoodistoRyhmaListDto converted = new KoodistoRyhmaListDto();
        converted.setId(source.getId());
        converted.setKoodistoRyhmaUri(source.getKoodistoRyhmaUri());

        converted.getMetadata().addAll(source.getKoodistoJoukkoMetadatas());

        for (Koodisto k : source.getKoodistos()) {
            converted.getKoodistos().add(koodistoToKoodistoListDtoConverter.convert(k));
        }

        return converted;
    }
}
