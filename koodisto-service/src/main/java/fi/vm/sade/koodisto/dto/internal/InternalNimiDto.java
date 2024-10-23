package fi.vm.sade.koodisto.dto.internal;

import lombok.Builder;
import lombok.Getter;

import jakarta.validation.constraints.NotBlank;
@Builder
@Getter
public class InternalNimiDto {
    @NotBlank
    private String fi;
    @NotBlank
    private String sv;
    @NotBlank
    private String en;
}
