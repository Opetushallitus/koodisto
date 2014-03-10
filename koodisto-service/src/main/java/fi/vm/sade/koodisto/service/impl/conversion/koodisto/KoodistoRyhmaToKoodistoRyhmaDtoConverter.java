package fi.vm.sade.koodisto.service.impl.conversion.koodisto;

import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import org.springframework.stereotype.Component;

@Component("koodistoRyhmaToKoodistoRyhmaDtoConverter")
public class KoodistoRyhmaToKoodistoRyhmaDtoConverter extends AbstractFromDomainConverter<KoodistoRyhma, KoodistoRyhmaDto> {


    @Override
    public KoodistoRyhmaDto convert(KoodistoRyhma source) {

        KoodistoRyhmaDto converted = new KoodistoRyhmaDto();

        converted.setId(source.getId());
        converted.setKoodistoRyhmaUri(source.getKoodistoRyhmaUri());
        converted.setKoodistoRyhmaMetadatas(source.getKoodistoJoukkoMetadatas());

        return converted;
    }
}
