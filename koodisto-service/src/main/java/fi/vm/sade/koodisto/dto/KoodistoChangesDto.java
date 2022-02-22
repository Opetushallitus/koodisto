package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.changes.MuutosTila;
import fi.vm.sade.koodisto.service.serializer.FinnishJsonDateSerializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class KoodistoChangesDto {
    
    @JsonView(JsonViews.Basic.class)
    public final String koodistoUri;
    
    @JsonView(JsonViews.Basic.class)
    public final MuutosTila muutosTila;
    
    @JsonView(JsonViews.Basic.class)
    public final Integer viimeisinVersio;
    
    @JsonView(JsonViews.Basic.class)
    public final List<SimpleMetadataDto> muuttuneetTiedot;
    
    @JsonView(JsonViews.Basic.class)
    public final List<SimpleMetadataDto> poistuneetTiedot;
    
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY, using=FinnishJsonDateSerializer.class)
    @JsonView(JsonViews.Basic.class)
    public final Date viimeksiPaivitetty;

    @JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY, using=FinnishJsonDateSerializer.class)
    @JsonView(JsonViews.Basic.class)
    public final Date voimassaAlkuPvm;
    
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY, using=FinnishJsonDateSerializer.class)
    @JsonView(JsonViews.Basic.class)
    public final Date voimassaLoppuPvm;
    
    @JsonView(JsonViews.Basic.class)
    public final Boolean poistettuVoimassaLoppuPvm;
    
    @JsonView(JsonViews.Basic.class)
    public final Tila tila;
    
    @JsonView(JsonViews.Basic.class)
    public final List<SimpleCodesRelation> lisatytKoodistonSuhteet;
    
    @JsonView(JsonViews.Basic.class)
    public final List<SimpleCodesRelation> poistetutKoodistonSuhteet;
    
    @JsonView(JsonViews.Basic.class)
    public final List<SimpleCodesRelation> passivoidutKoodistonSuhteet;
    
    @JsonView(JsonViews.Basic.class)
    public final List<KoodiChangesDto> lisatytKoodit;
    
    @JsonView(JsonViews.Basic.class)
    public final List<KoodiChangesDto> muuttuneetKoodit;
    
    @JsonView(JsonViews.Basic.class)
    public final List<KoodiChangesDto> poistetutKoodit;
    
    public KoodistoChangesDto(String koodistoUri, MuutosTila muutosTila, Integer viimeisinVersio,
            List<SimpleMetadataDto> muuttuneetTiedot, List<SimpleMetadataDto> poistuneetTiedot, Date viimeksiPaivitetty, Date voimassaAlkuPvm,
            Date voimassaLoppuPvm, Boolean poistettuVoimassaLoppuPvm, Tila tila,
            List<SimpleCodesRelation> lisatytKoodistonSuhteet, List<SimpleCodesRelation> poistetutKoodistonSuhteet, List<SimpleCodesRelation> passivoidutKoodistonSuhteet,
            List<KoodiChangesDto> lisatytKoodit, List<KoodiChangesDto> muuttuneetKoodit, List<KoodiChangesDto> poistetutKoodit) {
        this.muutosTila = muutosTila;
        this.viimeisinVersio = viimeisinVersio;
        this.muuttuneetTiedot = muuttuneetTiedot;
        this.poistuneetTiedot = poistuneetTiedot;
        this.viimeksiPaivitetty = viimeksiPaivitetty;
        this.voimassaAlkuPvm = voimassaAlkuPvm;
        this.voimassaLoppuPvm = voimassaLoppuPvm;
        this.poistettuVoimassaLoppuPvm = poistettuVoimassaLoppuPvm;
        this.tila = tila;
        this.lisatytKoodistonSuhteet = lisatytKoodistonSuhteet;
        this.poistetutKoodistonSuhteet = poistetutKoodistonSuhteet;
        this.passivoidutKoodistonSuhteet = passivoidutKoodistonSuhteet;
        this.lisatytKoodit = lisatytKoodit;
        this.muuttuneetKoodit = muuttuneetKoodit;
        this.poistetutKoodit = poistetutKoodit;
        this.koodistoUri = koodistoUri;
    }
    
    public KoodistoChangesDto(String koodistoUri, MuutosTila muutosTila, Integer viimeisinVersio, Date viimeksiPaivitetty) {
        this(koodistoUri, muutosTila, viimeisinVersio, new ArrayList<SimpleMetadataDto>(), new ArrayList<SimpleMetadataDto>(), 
                viimeksiPaivitetty, null, null, null, null, 
                new ArrayList<SimpleCodesRelation>(), new ArrayList<SimpleCodesRelation>(), new ArrayList<SimpleCodesRelation>(), 
                new ArrayList<KoodiChangesDto>(), new ArrayList<KoodiChangesDto>(), new ArrayList<KoodiChangesDto>());
    }

    public static class SimpleCodesRelation {
        
        @JsonView(JsonViews.Basic.class)
        public final String koodistoUri;

        @JsonView(JsonViews.Basic.class)
        public final Integer versio;

        @JsonView(JsonViews.Basic.class)
        public final SuhteenTyyppi suhteenTyyppi;

        @JsonView(JsonViews.Basic.class)
        public final boolean lapsiKoodisto;
        
        public SimpleCodesRelation(String koodistoUri, Integer versio, SuhteenTyyppi suhteenTyyppi, boolean lapsiKoodisto) {
            this.koodistoUri = koodistoUri;
            this.versio = versio;
            this.suhteenTyyppi = suhteenTyyppi;
            this.lapsiKoodisto = lapsiKoodisto;
        }
        
    }
    
}
