package fi.vm.sade.koodisto.service.conversion.impl.koodisto;

import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import org.springframework.stereotype.Component;

@Component("koodistoRyhmaToKoodistoRyhmaDtoConverter")
public class KoodistoRyhmaToKoodistoRyhmaDtoConverter extends AbstractFromDomainConverter<KoodistoRyhma, KoodistoRyhmaDto> {


    @Override
    public KoodistoRyhmaDto convert(KoodistoRyhma source) {

        KoodistoRyhmaDto converted = new KoodistoRyhmaDto();

        converted.setId(source.getId());
        converted.setKoodistoRyhmaUri(source.getKoodistoRyhmaUri());
        converted.setKoodistoRyhmaMetadatas(source.getKoodistoJoukkoMetadatas());
        converted.setKoodistos(source.getKoodistos());

        return converted;
    }
}
