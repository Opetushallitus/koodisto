package fi.vm.sade.koodisto.dto.internal;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.dto.KoodiMetadataDto;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Tila;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class InternalKoodiVersioDto extends InternalKoodiVersioListDto {

    @JsonView({JsonViews.Internal.class})
    private InternalKoodistoPageDto koodisto;

    @JsonView({JsonViews.Internal.class})
    private List<Integer> koodiVersio;

    @Min(value = 0, message = "error.lockingVersion.less.than.zero")
    @JsonView({JsonViews.Internal.class})
    private long lockingVersion;

    @JsonView({JsonViews.Internal.class})
    @NotNull(message = "error.sisaltyyKoodeihin.null")
    private List<InternalKoodiSuhdeDto> sisaltyyKoodeihin;

    @JsonView({JsonViews.Internal.class})
    @NotNull(message = "error.sisaltaaKoodit.null")
    private List<InternalKoodiSuhdeDto> sisaltaaKoodit;

    @JsonView({JsonViews.Internal.class})
    @NotNull(message = "error.rinnastuuKoodeihin.null")
    private List<InternalKoodiSuhdeDto> rinnastuuKoodeihin;

    @SuppressWarnings("java:S107") // constructor added to allow lombok builder generation
    @Builder(builderMethodName = "internalKoodiVersioDtoBuilder")
    public InternalKoodiVersioDto(
            String koodiUri,
            int versio,
            String koodiArvo,
            Tila tila,
            Date paivitysPvm,
            String paivittajaOid,
            Date voimassaAlkuPvm,
            Date voimassaLoppuPvm,
            List<KoodiMetadataDto> metadata,
            InternalKoodistoPageDto koodisto,
            List<Integer> koodiVersio,
            long lockingVersion,
            List<InternalKoodiSuhdeDto> sisaltyyKoodeihin,
            List<InternalKoodiSuhdeDto> sisaltaaKoodit,
            List<InternalKoodiSuhdeDto> rinnastuuKoodeihin
    ) {
        super(koodiUri, versio, koodiArvo, tila, paivitysPvm, paivittajaOid, voimassaAlkuPvm, voimassaLoppuPvm, metadata);
        this.koodisto = koodisto;
        this.koodiVersio = koodiVersio;
        this.lockingVersion = lockingVersion;
        this.sisaltyyKoodeihin = sisaltyyKoodeihin;
        this.sisaltaaKoodit = sisaltaaKoodit;
        this.rinnastuuKoodeihin = rinnastuuKoodeihin;
    }
}
