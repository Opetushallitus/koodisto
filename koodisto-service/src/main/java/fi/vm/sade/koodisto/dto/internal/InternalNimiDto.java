package fi.vm.sade.koodisto.dto.internal;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
@Builder
public class InternalNimiDto {
    @NotBlank
    private String fi;
    @NotBlank
    private String sv;
    @NotBlank
    private String en;
}
