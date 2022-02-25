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

public class KoodistoChangesDto {
    
    public final String koodistoUri;
    
    public final MuutosTila muutosTila;
    
    public final Integer viimeisinVersio;
    
    public final List<SimpleMetadataDto> muuttuneetTiedot;
    
    public final List<SimpleMetadataDto> poistuneetTiedot;

    public final Date viimeksiPaivitetty;

    public final Date voimassaAlkuPvm;

    public final Date voimassaLoppuPvm;

    public final Boolean poistettuVoimassaLoppuPvm;
    
    public final Tila tila;

    public final List<SimpleCodesRelation> lisatytKoodistonSuhteet;

    public final List<SimpleCodesRelation> poistetutKoodistonSuhteet;

    public final List<SimpleCodesRelation> passivoidutKoodistonSuhteet;

    public final List<KoodiChangesDto> lisatytKoodit;
    
    public final List<KoodiChangesDto> muuttuneetKoodit;

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

        public final String koodistoUri;

        public final Integer versio;

        public final SuhteenTyyppi suhteenTyyppi;

        public final boolean lapsiKoodisto;
        
        public SimpleCodesRelation(String koodistoUri, Integer versio, SuhteenTyyppi suhteenTyyppi, boolean lapsiKoodisto) {
            this.koodistoUri = koodistoUri;
            this.versio = versio;
            this.suhteenTyyppi = suhteenTyyppi;
            this.lapsiKoodisto = lapsiKoodisto;
        }
        
    }
    
}
