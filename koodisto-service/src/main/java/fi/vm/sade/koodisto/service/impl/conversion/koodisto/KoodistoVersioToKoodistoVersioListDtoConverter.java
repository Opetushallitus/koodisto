package fi.vm.sade.koodisto.service.impl.conversion.koodisto;

import fi.vm.sade.koodisto.dto.KoodistoVersioListDto;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
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
        converted.setVersion(source.getVersion());
        converted.setVoimassaAlkuPvm(source.getVoimassaAlkuPvm());
        converted.setVoimassaLoppuPvm(source.getVoimassaLoppuPvm());
        converted.getMetadata().addAll(source.getMetadatas());

        return converted;
    }

}
