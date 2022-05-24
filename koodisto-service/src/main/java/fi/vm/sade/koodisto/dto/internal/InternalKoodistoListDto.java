package fi.vm.sade.koodisto.dto.internal;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.dto.KoodistoMetadataDto;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaMetadataDto;
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
public class InternalKoodistoListDto {
    @JsonView({JsonViews.Intetrnal.class})
    private
    Set<KoodistoRyhmaMetadataDto> koodistoRyhmaMetadata;
    @JsonView({JsonViews.Intetrnal.class})
    private String koodistoUri;
    @JsonView({JsonViews.Intetrnal.class})
    private int versio;
    @JsonView({JsonViews.Intetrnal.class})
    private Date voimassaAlkuPvm;
    @JsonView({JsonViews.Intetrnal.class})
    private Date voimassaLoppuPvm;
    @JsonView({JsonViews.Intetrnal.class})
    private List<KoodistoMetadataDto> metadata;
    @JsonView({JsonViews.Intetrnal.class})
    private int koodiCount;
}
