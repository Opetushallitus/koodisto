package fi.vm.sade.koodisto.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InternalNimiDto {
    @NotBlank
    private String fi;
    @NotBlank
    private String sv;
    @NotBlank
    private String en;
}
