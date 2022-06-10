package fi.vm.sade.koodisto.dto.internal;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InternalKoodistoRyhmaDto {
    private String koodistoRyhmaUri;
    private InternalNimiDto nimi;
}
