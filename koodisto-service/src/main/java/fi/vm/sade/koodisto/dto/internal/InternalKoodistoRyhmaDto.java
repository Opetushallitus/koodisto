package fi.vm.sade.koodisto.dto.internal;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
public class InternalKoodistoRyhmaDto {
    private String koodistoRyhmaUri;
    private InternalNimiDto nimi;
}
