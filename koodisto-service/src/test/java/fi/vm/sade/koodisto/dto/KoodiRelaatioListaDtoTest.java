package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

class KoodiRelaatioListaDtoTest extends DtoTest {

    private static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of("Should be valid", getValid().build(), true),
                Arguments.of("Fail if URI not set", getValid().codeElementUri(null).build(), false),
                Arguments.of("Fail if URI is blank", getValid().codeElementUri("").build(), false),
                Arguments.of("Fail if relation type is not set", getValid().relationType(null).build(), false),
                Arguments.of("Fail if relations is not set", getValid().relations(null).build(), false),
                Arguments.of("Fail if relations is empty", getValid().relations(List.of()).build(), false)
        );
    }

    private static KoodiRelaatioListaDto.KoodiRelaatioListaDtoBuilder getValid() {
        return KoodiRelaatioListaDto
                .builder()
                .codeElementUri("uri")
                .relationType(SuhteenTyyppi.SISALTYY)
                .relations(List.of("relation"));
    }
}
