package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.views.JsonViews;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.changes.MuutosTila;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@JsonView({JsonViews.Basic.class, JsonViews.Extended.class})
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

    public KoodiChangesDto(String koodiUri, MuutosTila muutosTila, Integer viimeisinVersio, Date viimeksiPaivitetty) {
        this(koodiUri, muutosTila, viimeisinVersio, new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                viimeksiPaivitetty, null, null, null, null);
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class SimpleCodeElementRelation {
        private final String koodiUri;
        private final Integer versio;
        private final SuhteenTyyppi suhteenTyyppi;
        private final boolean lapsiKoodi;
    }
}
