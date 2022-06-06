package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Tila;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ExtendedKoodiDto {

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    private String koodiUri;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String resourceUri;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private Long version;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    private int versio;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private KoodistoItemDto koodisto;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    private String koodiArvo;

    @JsonFormat(pattern="yyyy-MM-dd")
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    protected Date paivitysPvm;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    protected String paivittajaOid;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    protected Date voimassaAlkuPvm;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    protected Date voimassaLoppuPvm;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    protected Tila tila;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    protected List<KoodiMetadataDto> metadata = new ArrayList<>();

    @JsonView({JsonViews.Extended.class})
    protected List<RelationCodeElement> withinCodeElements = new ArrayList<>();

    @JsonView({JsonViews.Extended.class})
    protected List<RelationCodeElement> includesCodeElements = new ArrayList<>();

    @JsonView({JsonViews.Extended.class})
    protected List<RelationCodeElement> levelsWithCodeElements = new ArrayList<>();

    @Getter
    @RequiredArgsConstructor
    public static class RelationCodeElement {
        @JsonView({JsonViews.Extended.class})
        private final String codeElementUri;
        @JsonView({JsonViews.Extended.class})
        private final Integer codeElementVersion;
        @JsonView({JsonViews.Extended.class})
        private final String codeElementValue;
        @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
        private final List<SimpleMetadataDto> relationMetadata;
        @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
        private final List<SimpleMetadataDto> parentMetadata;

        @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
        private final boolean passive;


        public RelationCodeElement() {
            this.codeElementUri = null;
            this.codeElementVersion = -1;
            this.relationMetadata = null;
            this.parentMetadata = null;
            this.codeElementValue = null;
            this.passive = false;
        }

        public RelationCodeElement(String codeElementUri, Integer version, boolean passive) {
            this.codeElementUri = codeElementUri;
            this.codeElementVersion = version;
            this.relationMetadata = null;
            this.parentMetadata = null;
            this.codeElementValue = null;
            this.passive = passive;

        }
    }
}
