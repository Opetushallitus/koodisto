package fi.vm.sade.koodisto.dto.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.dto.KoodiMetadataDto;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Tila;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class InternalKoodiVersioDto {

    @JsonView({JsonViews.Internal.class})
    private String koodistoUri;

    @JsonView({JsonViews.Internal.class})
    private String koodiArvo;

    @JsonView({JsonViews.Internal.class})
    private Integer versio;

    @JsonView({JsonViews.Internal.class})
    private List<Integer> koodiVersio;

    @Min(value = 0, message = "error.lockingVersion.less.than.zero")
    @JsonView({JsonViews.Internal.class})
    private long lockingVersion;

    @JsonView({JsonViews.Internal.class})
    private Tila tila;

    @JsonView({JsonViews.Internal.class})
    private String koodiUri;

    @JsonView({JsonViews.Internal.class})
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date paivitysPvm;

    @JsonView({JsonViews.Internal.class})
    private String paivittajaOid;

    @JsonView({JsonViews.Internal.class})
    private Date voimassaAlkuPvm;

    @JsonView({JsonViews.Internal.class})
    private Date voimassaLoppuPvm;

    @JsonView({JsonViews.Internal.class})
    private List<KoodiMetadataDto> metadata;

    @JsonView({JsonViews.Internal.class})
    private List<InternalKoodiSuhdeDto> sisaltyyKoodeihin;

    @JsonView({JsonViews.Internal.class})
    private List<InternalKoodiSuhdeDto> sisaltaaKoodit;

    @JsonView({JsonViews.Internal.class})
    private List<InternalKoodiSuhdeDto> rinnastuuKoodeihin;
}
