package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.changes.MuutosTila;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KoodiChangesDto {
    
    public final String koodiUri;
    
    public final MuutosTila muutosTila;
    
    public final Integer viimeisinVersio;
    
    public final List<SimpleKoodiMetadataDto> muuttuneetTiedot;
    
    public final List<SimpleKoodiMetadataDto> poistuneetTiedot;
    
    public final List<SimpleCodeElementRelation> lisatytKoodinSuhteet;
    
    public final List<SimpleCodeElementRelation> poistetutKoodinSuhteet;
    
    public final List<SimpleCodeElementRelation> passivoidutKoodinSuhteet;

    public final Date viimeksiPaivitetty;

    public final Date voimassaAlkuPvm;

    public final Date voimassaLoppuPvm;
    
    public final Boolean poistettuVoimassaLoppuPvm;
    
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
    
    public KoodiChangesDto(String koodiUri, MuutosTila muutosTila, Integer viimeisinVersio, Date viimeksiPaivitetty) {
        this(koodiUri, muutosTila, viimeisinVersio, new ArrayList<SimpleKoodiMetadataDto>(), new ArrayList<SimpleKoodiMetadataDto>(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                viimeksiPaivitetty, null, null, null, null);
    }

    public static class SimpleCodeElementRelation {
        
        public final String koodiUri;

        public final Integer versio;

        public final SuhteenTyyppi suhteenTyyppi;

        public final boolean lapsiKoodi;
        
        public SimpleCodeElementRelation(String koodiUri, Integer versio, SuhteenTyyppi suhteenTyyppi, boolean lapsiKoodi) {
            this.koodiUri = koodiUri;
            this.versio = versio;
            this.suhteenTyyppi = suhteenTyyppi;
            this.lapsiKoodi = lapsiKoodi;
        }
        
    }
    
    
}
