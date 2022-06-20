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
@Getter
@Setter
@JsonView({ JsonViews.Basic.class })
public class KoodistoChangesDto {

    private final String koodistoUri;
    private final MuutosTila muutosTila;
    private final Integer viimeisinVersio;
    private final List<SimpleMetadataDto> muuttuneetTiedot;
    private final List<SimpleMetadataDto> poistuneetTiedot;
    private final Date viimeksiPaivitetty;
    private final Date voimassaAlkuPvm;
    private final Date voimassaLoppuPvm;
    private final Boolean poistettuVoimassaLoppuPvm;
    private final Tila tila;
    private final List<SimpleCodesRelation> lisatytKoodistonSuhteet;
    private final List<SimpleCodesRelation> poistetutKoodistonSuhteet;
    private final List<SimpleCodesRelation> passivoidutKoodistonSuhteet;
    private final List<KoodiChangesDto> lisatytKoodit;
    private final List<KoodiChangesDto> muuttuneetKoodit;
    private final List<KoodiChangesDto> poistetutKoodit;

    public KoodistoChangesDto(String koodistoUri, MuutosTila muutosTila, Integer viimeisinVersio, Date viimeksiPaivitetty) {
        this(koodistoUri, muutosTila, viimeisinVersio, new ArrayList<>(), new ArrayList<>(),
                viimeksiPaivitetty, null, null, null, null,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class SimpleCodesRelation {

        private final String koodistoUri;

        private final Integer versio;

        private final SuhteenTyyppi suhteenTyyppi;

        private final boolean lapsiKoodisto;

    }

}
