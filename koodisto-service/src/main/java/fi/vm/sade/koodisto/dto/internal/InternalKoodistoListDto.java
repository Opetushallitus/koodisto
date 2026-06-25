package fi.vm.sade.koodisto.dto.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
@JsonPropertyOrder({"koodistoRyhmaMetadata", "koodistoUri", "versio", "voimassaAlkuPvm", "voimassaLoppuPvm", "metadata", "koodiCount"})
public class InternalKoodistoListDto {
    @JsonView({JsonViews.Internal.class})
    private
    Set<KoodistoRyhmaMetadataDto> koodistoRyhmaMetadata;
    @JsonView({JsonViews.Internal.class})
    private String koodistoUri;
    @JsonView({JsonViews.Internal.class})
    private int versio;
    @JsonView({JsonViews.Internal.class})
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date voimassaAlkuPvm;
    @JsonView({JsonViews.Internal.class})
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date voimassaLoppuPvm;
    @JsonView({JsonViews.Internal.class})
    private List<KoodistoMetadataDto> metadata;
    @JsonView({JsonViews.Internal.class})
    private int koodiCount;
}
