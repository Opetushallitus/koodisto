package fi.vm.sade.koodisto.dto;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonView;

import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.changes.MuutosTila;
import fi.vm.sade.koodisto.service.serializer.FinnishJsonDateSerializer;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class KoodiChangesDto {
    
    @JsonView({JsonViews.Basic.class, JsonViews.Extended.class})
    public final String koodiUri;
    
    @JsonView(JsonViews.Extended.class)
    public final MuutosTila muutosTila;
    
    @JsonView({JsonViews.Basic.class, JsonViews.Extended.class})
    public final Integer viimeisinVersio;
    
    @JsonView({JsonViews.Basic.class, JsonViews.Extended.class})
    public final List<SimpleKoodiMetadataDto> muuttuneetTiedot;
    
    @JsonView({JsonViews.Basic.class, JsonViews.Extended.class})
    public final List<SimpleKoodiMetadataDto> poistuneetTiedot;
    
    @JsonView({JsonViews.Basic.class, JsonViews.Extended.class})
    public final List<SimpleCodeElementRelation> lisatytKoodinSuhteet;
    
    @JsonView({JsonViews.Basic.class, JsonViews.Extended.class})
    public final List<SimpleCodeElementRelation> poistetutKoodinSuhteet;
    
    @JsonView({JsonViews.Basic.class, JsonViews.Extended.class})
    public final List<SimpleCodeElementRelation> passivoidutKoodinSuhteet;
    
    @JsonSerialize(using=FinnishJsonDateSerializer.class)
    @JsonView({JsonViews.Basic.class, JsonViews.Extended.class})
    public final Date viimeksiPaivitetty;
    
    @JsonSerialize(using=FinnishJsonDateSerializer.class)
    @JsonView({JsonViews.Basic.class, JsonViews.Extended.class})
    public final Date voimassaAlkuPvm;
    
    @JsonSerialize(using=FinnishJsonDateSerializer.class)
    @JsonView({JsonViews.Basic.class, JsonViews.Extended.class})
    public final Date voimassaLoppuPvm;
    
    @JsonView({JsonViews.Basic.class, JsonViews.Extended.class})
    public final Boolean poistettuVoimassaLoppuPvm;
    
    @JsonView({JsonViews.Basic.class, JsonViews.Extended.class})
    public final Tila tila;

    public KoodiChangesDto(String koodiUri, MuutosTila muutosTila, Integer viimeisinVersio,
            List<SimpleKoodiMetadataDto> muuttuneetTiedot, List<SimpleKoodiMetadataDto> poistuneetTiedot,
            List<SimpleCodeElementRelation> lisatytKoodinSuhteet, List<SimpleCodeElementRelation> poistetutKoodinSuhteet, List<SimpleCodeElementRelation> passivoidutKoodinSuhteet, 
            Date viimeksiPaivitetty, Date voimassaAlkuPvm, Date voimassaLoppuPvm, Boolean poistettuVoimassaLoppuPvm, Tila tila) {
        this.koodiUri = koodiUri;
        this.muutosTila = muutosTila;
        this.viimeisinVersio = viimeisinVersio;
        this.muuttuneetTiedot = muuttuneetTiedot;
        this.poistuneetTiedot = poistuneetTiedot;
        this.lisatytKoodinSuhteet = lisatytKoodinSuhteet;
        this.poistetutKoodinSuhteet = poistetutKoodinSuhteet;
        this.viimeksiPaivitetty = viimeksiPaivitetty;
        this.voimassaAlkuPvm = voimassaAlkuPvm;
        this.voimassaLoppuPvm = voimassaLoppuPvm;
        this.poistettuVoimassaLoppuPvm = poistettuVoimassaLoppuPvm;
        this.tila = tila;
        this.passivoidutKoodinSuhteet = passivoidutKoodinSuhteet;
    }

    public static class SimpleCodeElementRelation {
        
        @JsonView({JsonViews.Basic.class, JsonViews.Extended.class})
        public final String koodiUri;

        @JsonView({JsonViews.Basic.class, JsonViews.Extended.class})
        public final Integer versio;

        @JsonView({JsonViews.Basic.class, JsonViews.Extended.class})
        public final SuhteenTyyppi suhteenTyyppi;

        @JsonView({JsonViews.Basic.class, JsonViews.Extended.class})
        public final boolean lapsiKoodi;
        
        public SimpleCodeElementRelation(String koodiUri, Integer versio, SuhteenTyyppi suhteenTyyppi, boolean lapsiKoodi) {
            this.koodiUri = koodiUri;
            this.versio = versio;
            this.suhteenTyyppi = suhteenTyyppi;
            this.lapsiKoodi = lapsiKoodi;
        }
        
    }
    
    
}
