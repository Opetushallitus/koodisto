package fi.vm.sade.koodisto.dto.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.dto.KoodiMetadataDto;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Tila;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class InternalKoodiVersioListDto {
    @JsonView({JsonViews.Internal.class})
    private String koodiUri;
    @JsonView({JsonViews.Internal.class})
    private int versio;
    @JsonView({JsonViews.Internal.class})
    private String koodiArvo;
    @JsonView({JsonViews.Internal.class})
    private Tila tila;
    @JsonView({JsonViews.Internal.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date paivitysPvm;
    @JsonView({JsonViews.Internal.class})
    private String paivittajaOid;
    @JsonView({JsonViews.Internal.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date voimassaAlkuPvm;
    @JsonView({JsonViews.Internal.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date voimassaLoppuPvm;
    @JsonView({JsonViews.Internal.class})
    private List<KoodiMetadataDto> metadata;
}
