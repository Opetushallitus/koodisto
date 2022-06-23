package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.views.JsonViews;
import fi.vm.sade.koodisto.model.Tila;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * User: wuoti
 * Date: 21.5.2013
 * Time: 9.40
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KoodiDto {

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
    private String koodiUri;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    private String resourceUri;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private long version;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
    private int versio = 1;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    private KoodistoItemDto koodisto;

    @NotEmpty(message = "error.koodiarvo.empty")
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
    private String koodiArvo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    protected Date paivitysPvm;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    protected String paivittajaOid;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    @NotNull
    protected Date voimassaAlkuPvm = new Date();

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    protected Date voimassaLoppuPvm;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    protected Tila tila = Tila.LUONNOS;

    @NotEmpty(message = "error.metadata.empty")
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
    protected List<@Valid KoodiMetadataDto> metadata = new ArrayList<>();

    @JsonIgnore
    @AssertTrue(message = "error.validation.enddate")
    public boolean isStartBeforeEnd() {
        return Optional.ofNullable(voimassaLoppuPvm).map(date -> date.after(voimassaAlkuPvm)).orElse(true);
    }

    @JsonIgnore
    @AssertTrue(message = "error.validation.versio")
    public boolean isVersioSetForUpdate() {
        return Optional.ofNullable(koodiUri).map(uri -> versio > 0).orElse(true);
    }

    @JsonIgnore
    @AssertTrue(message = "error.validation.tila")
    public boolean isTilaSetForUpdate() {
        return Optional.ofNullable(koodiUri).map(uri -> tila != null).orElse(true);
    }
}
