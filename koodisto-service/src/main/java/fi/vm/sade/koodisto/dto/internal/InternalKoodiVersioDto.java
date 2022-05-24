package fi.vm.sade.koodisto.dto.internal;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.dto.KoodiMetadataDto;
import fi.vm.sade.koodisto.model.JsonViews;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class InternalKoodiVersioDto {
    @JsonView({JsonViews.Intetrnal.class})
    private String koodiarvo;
    @JsonView({JsonViews.Intetrnal.class})
    private Integer versio;
    @JsonView({JsonViews.Intetrnal.class})
    private Date paivitysPvm;
    @JsonView({JsonViews.Intetrnal.class})
    private String paivittajaOid;
    @JsonView({JsonViews.Intetrnal.class})
    private Date voimassaAlkuPvm;
    @JsonView({JsonViews.Intetrnal.class})
    private List<KoodiMetadataDto> metadatas;
}
