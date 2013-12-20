package fi.vm.sade.koodisto.service.impl.conversion.koodisto;

import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.dto.KoodistoVersioListDto;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import org.springframework.stereotype.Component;

@Component("koodistoVersioToKoodistoVersioListDtoConverter")
public class KoodistoVersioToKoodistoVersioListDtoConverter extends
        AbstractFromDomainConverter<KoodistoVersio, KoodistoVersioListDto> {

    @Override
    public KoodistoVersioListDto convert(KoodistoVersio source) {
        KoodistoVersioListDto converted = new KoodistoVersioListDto();
        converted.setPaivitysPvm(source.getPaivitysPvm());
        converted.setTila(source.getTila());
        converted.setVersio(source.getVersio());
        converted.setVoimassaAlkuPvm(source.getVoimassaAlkuPvm());
        converted.setVoimassaLoppuPvm(source.getVoimassaLoppuPvm());
        converted.getMetadata().addAll(source.getMetadatas());

        return converted;
    }

}
