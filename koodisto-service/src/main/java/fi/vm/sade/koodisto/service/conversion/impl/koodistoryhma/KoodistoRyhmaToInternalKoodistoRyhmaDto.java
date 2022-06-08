package fi.vm.sade.koodisto.service.conversion.impl.koodistoryhma;

import fi.vm.sade.koodisto.dto.internal.InternalKoodistoRyhmaDto;
import fi.vm.sade.koodisto.dto.internal.InternalNimiDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;


public class KoodistoRyhmaToInternalKoodistoRyhmaDto extends AbstractFromDomainConverter<KoodistoRyhma, InternalKoodistoRyhmaDto> {
    @Override
    public InternalKoodistoRyhmaDto convert(KoodistoRyhma source) {
        return InternalKoodistoRyhmaDto.builder()
                .koodistoRyhmaUri(source.getKoodistoRyhmaUri())
                .nimi(InternalNimiDto.builder()
                        .fi(source.getKoodistoRyhmaMetadatas().stream().filter(a -> a.getKieli().equals(Kieli.FI)).findFirst().orElseGet(KoodistoRyhmaMetadata::new).getNimi())
                        .sv(source.getKoodistoRyhmaMetadatas().stream().filter(a -> a.getKieli().equals(Kieli.SV)).findFirst().orElseGet(KoodistoRyhmaMetadata::new).getNimi())
                        .en(source.getKoodistoRyhmaMetadatas().stream().filter(a -> a.getKieli().equals(Kieli.EN)).findFirst().orElseGet(KoodistoRyhmaMetadata::new).getNimi())
                        .build())
                .build();
    }
}
