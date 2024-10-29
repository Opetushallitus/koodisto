package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.Tila;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

class KoodiDtoTest extends DtoTest {

    static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of("Should be valid", getValid().build(), Set.of()),
                Arguments.of("End date can be null", getValid().voimassaLoppuPvm(null).build(), Set.of()),
                Arguments.of("End date before start date", getValid().voimassaLoppuPvm(dateOf(2022, 6, 20)).build(), Set.of("error.validation.enddate")),
                Arguments.of("Some attributes required if URI is set (update)", getValid().koodiUri("generated uri").build(), Set.of("error.validation.versio", "error.validation.tila")),
                Arguments.of("Tila is required URI is set (update)", getValid().koodiUri("generated uri").versio(1).build(), Set.of("error.validation.tila")),
                Arguments.of("Versio is required URI is set (update)", getValid().koodiUri("generated uri").tila(Tila.LUONNOS).build(), Set.of("error.validation.versio")),
                Arguments.of("Required attributes set for update (update)", getValid().koodiUri("generated uri").versio(1).tila(Tila.LUONNOS).build(), Set.of())
        );
    }

    private static KoodiDto.KoodiDtoBuilder getValid() {
        return KoodiDto.builder()
                .koodiArvo("just a unit test")
                .voimassaAlkuPvm(dateOf(2022, 6, 21))
                .voimassaLoppuPvm(dateOf(2022, 6, 22))
                .metadata(List.of(KoodiMetadataDto.builder().kieli(Kieli.FI).nimi("translated name").build()));
    }
}
