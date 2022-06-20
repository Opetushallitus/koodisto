package fi.vm.sade.koodisto.dto.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.dto.KoodistoMetadataDto;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaMetadataDto;
import fi.vm.sade.koodisto.views.JsonViews;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class InternalKoodistoPageDto {

    @JsonView({JsonViews.Internal.class})
    private Set<KoodistoRyhmaMetadataDto> koodistoRyhmaMetadata;

    @NotBlank(message = "error.koodistoRyhmaUri.blank")
    @JsonView({JsonViews.Internal.class})
    private String koodistoRyhmaUri;

    @JsonView({JsonViews.Internal.class})
    private String resourceUri;

    @NotBlank(message = "error.koodistoUri.blank")
    @JsonView({JsonViews.Internal.class})
    private String koodistoUri;

    @Min(value = 1, message = "error.versio.less.than.one")
    @JsonView({JsonViews.Internal.class})
    private int versio;

    @Min(value = 0, message = "error.lockingVersion.less.than.zero")
    @JsonView({JsonViews.Internal.class})
    private long lockingVersion;

    @NotBlank(message = "error.organisaatioOid.blank")
    @JsonView({JsonViews.Internal.class})
    private String organisaatioOid;

    @JsonView({JsonViews.Internal.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date paivitysPvm;

    @JsonView({JsonViews.Internal.class})
    private String paivittajaOid;

    @JsonView({JsonViews.Internal.class})
    private Date voimassaAlkuPvm;

    @JsonView({JsonViews.Internal.class})
    private Date voimassaLoppuPvm;

    @JsonView({JsonViews.Internal.class})
    private String omistaja;

    @JsonView({JsonViews.Internal.class})
    @NotNull(message = "error.tila.empty")
    private TilaType tila;

    @NotEmpty(message = "error.metadata.empty")
    @JsonView({JsonViews.Internal.class})
    private Set<@Valid KoodistoMetadataDto> metadata;

    @JsonView({JsonViews.Internal.class})
    private List<Integer> koodistoVersio;

    @JsonView({JsonViews.Internal.class})
    private List<InternalKoodistoSuhdeDto> sisaltyyKoodistoihin;

    @JsonView({JsonViews.Internal.class})
    private List<InternalKoodistoSuhdeDto> sisaltaaKoodistot;

    @JsonView({JsonViews.Internal.class})
    private List<InternalKoodistoSuhdeDto> rinnastuuKoodistoihin;

    @JsonView({JsonViews.Internal.class})
    private List<InternalKoodiVersioDto> koodiList;
}
