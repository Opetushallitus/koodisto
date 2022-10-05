package fi.vm.sade.koodisto.service.conversion.impl.koodistoryhma;

import fi.vm.sade.koodisto.dto.internal.InternalKoodistoRyhmaDto;
import fi.vm.sade.koodisto.dto.internal.InternalNimiDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import org.springframework.stereotype.Component;

@Component
public class KoodistoRyhmaToInternalKoodistoRyhmaDto implements AbstractFromDomainConverter<KoodistoRyhma, InternalKoodistoRyhmaDto> {
    @Override
    public InternalKoodistoRyhmaDto convert(KoodistoRyhma source) {
        return InternalKoodistoRyhmaDto.builder()
                .koodistoRyhmaUri(source.getKoodistoRyhmaUri())
                .nimi(InternalNimiDto.builder()
                        .fi(source.getNimi(Kieli.FI))
                        .sv(source.getNimi(Kieli.SV))
                        .en(source.getNimi(Kieli.EN))
                        .build())
                .build();
    }
}
