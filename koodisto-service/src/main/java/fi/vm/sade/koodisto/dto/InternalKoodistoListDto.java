package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
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
    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class })
    private
    Set<KoodistoRyhmaMetadataDto> koodistoRyhmaMetadata;
    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class })
    private String koodistoUri;
    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class })
    private int versio;
    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class })
    private Date voimassaAlkuPvm;
    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class })
    private Date voimassaLoppuPvm;
    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class })
    private List<KoodistoMetadataDto> metadata;
    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class })
    private int koodiCount;
}
