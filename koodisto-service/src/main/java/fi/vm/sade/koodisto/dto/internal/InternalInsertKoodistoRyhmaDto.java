package fi.vm.sade.koodisto.dto.internal;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;

@Getter
@Setter
public class InternalInsertKoodistoRyhmaDto {
    @Valid
    private InternalNimiDto nimi;
}
