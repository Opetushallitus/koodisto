
package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Kieli;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KoodistoRyhmaMetadataDto {
    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class })
    protected Long id;
    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class })
    protected String uri;
    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class })
    protected String nimi;
    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class })
    protected Kieli kieli;

}