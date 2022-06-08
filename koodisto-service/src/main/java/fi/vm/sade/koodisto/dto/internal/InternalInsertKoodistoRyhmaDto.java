package fi.vm.sade.koodisto.dto.internal;

import lombok.Data;

import javax.validation.Valid;

@Data
public class InternalInsertKoodistoRyhmaDto {
    @Valid
    private InternalNimiDto nimi;
}
