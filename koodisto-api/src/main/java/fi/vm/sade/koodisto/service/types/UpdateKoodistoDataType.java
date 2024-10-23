package fi.vm.sade.koodisto.service.types;

import fi.vm.sade.koodisto.service.types.common.TilaType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class UpdateKoodistoDataType extends CreateKoodistoDataType {

    private static final long serialVersionUID = 1L;
    @NotBlank(message = "error.codesGroupUri.blank")
    protected String codesGroupUri;
    @NotBlank(message = "error.koodistoUri.blank")
    protected String koodistoUri;
    @NotNull(message = "error.tila.empty")
    protected TilaType tila;
    @Min(value = 1, message = "error.versio.less.than.one")
    protected int versio;
    @Min(value = 0, message = "error.lockingVersion.less.than.zero")
    protected long lockingVersion;

}
