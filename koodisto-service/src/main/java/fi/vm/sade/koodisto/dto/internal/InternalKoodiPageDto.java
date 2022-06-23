package fi.vm.sade.koodisto.dto.internal;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.model.JsonViews;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonView({JsonViews.Internal.class})
public class InternalKoodiPageDto {

    private ExtendedKoodiDto koodi;
    private KoodistoDto koodisto;
}
