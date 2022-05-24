package fi.vm.sade.koodisto.dto.internal;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.dto.KoodistoMetadataDto;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Tila;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class InternalKoodistoPageDto {
    @JsonView({JsonViews.Intetrnal.class})
    private String resourceUri;
    @JsonView({JsonViews.Intetrnal.class})
    private int versio;
    @JsonView({JsonViews.Intetrnal.class})
    private Date paivitysPvm;
    @JsonView({JsonViews.Intetrnal.class})
    private String paivittajaOid;
    @JsonView({JsonViews.Intetrnal.class})
    private Date voimassaAlkuPvm;
    @JsonView({JsonViews.Intetrnal.class})
    private Date voimassaLoppuPvm;
    @JsonView({JsonViews.Intetrnal.class})
    private Tila tila;
    @JsonView({JsonViews.Intetrnal.class})
    private List<KoodistoMetadataDto> metadata;
    @JsonView({JsonViews.Intetrnal.class})
    private List<Integer> koodiVersio;
    @JsonView({JsonViews.Intetrnal.class})
    private List<InternalKoodisuhdeDto> sisaltyyKoodistoihin;
    @JsonView({JsonViews.Intetrnal.class})
    private List<InternalKoodisuhdeDto> sisaltaaKoodistot;
    @JsonView({JsonViews.Intetrnal.class})
    private List<InternalKoodisuhdeDto> rinnastuuKoodistoihin;
    @JsonView({JsonViews.Intetrnal.class})
    private List<InternalKoodiVersioDto> koodiList;
}
