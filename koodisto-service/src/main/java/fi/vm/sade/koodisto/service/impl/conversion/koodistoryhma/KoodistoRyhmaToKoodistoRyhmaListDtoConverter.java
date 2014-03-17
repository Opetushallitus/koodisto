package fi.vm.sade.koodisto.service.impl.conversion.koodistoryhma;

import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaListDto;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.service.impl.conversion.koodisto.KoodistoToKoodistoListDtoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("koodistoRyhmaToKoodistoRyhmaListDtoConverter")
public class KoodistoRyhmaToKoodistoRyhmaListDtoConverter extends AbstractFromDomainConverter<KoodistoRyhma, KoodistoRyhmaListDto> {

    @Autowired
    private KoodistoToKoodistoListDtoConverter koodistoToKoodistoListDtoConverter;

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
