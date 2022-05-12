package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Tila;
import lombok.Getter;
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
    protected List<RelationCodeElement> levelsWithCodeElements = new ArrayList<RelationCodeElement>();

    public static class RelationCodeElement {
        public String getCodeElementUri() {
            return codeElementUri;
        }

        @JsonView({JsonViews.Extended.class})
        public final String codeElementUri;
        @JsonView({JsonViews.Extended.class})
        public final Integer codeElementVersion;
        @JsonView({JsonViews.Extended.class})
        public final String codeElementValue;
        @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
        public final List<SimpleMetadataDto> relationMetadata;
        @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
        public final List<SimpleMetadataDto> parentMetadata;

        public boolean isPassive() {
            return passive;
        }

        @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
        public final boolean passive;
        
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
        
        public RelationCodeElement(String codeElementUri, Integer codeElementVersion, String codeElementValue, List<SimpleMetadataDto> relationMetadata, List<SimpleMetadataDto> parentMetadata, boolean passive) {
            this.codeElementUri = codeElementUri;
            this.codeElementVersion = codeElementVersion;
            this.relationMetadata = relationMetadata;
            this.parentMetadata = parentMetadata;
            this.codeElementValue = codeElementValue;
            this.passive = passive;
        }
    }

}
