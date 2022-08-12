package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

class KoodiRelaatioListaDtoTest extends DtoTest {

    private static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of("Should be valid", getValid().build(), Set.of()),
                Arguments.of("Fail if URI not set", getValid().codeElementUri(null).build(), Set.of("error.codeElementUri.empty")),
                Arguments.of("Fail if URI is blank", getValid().codeElementUri("").build(), Set.of("error.codeElementUri.empty")),
                Arguments.of("Fail if relation type is not set", getValid().relationType(null).build(), Set.of("error.relationType.empty")),
                Arguments.of("Fail if relations is not set", getValid().relations(null).build(), Set.of("error.relations.empty")),
                Arguments.of("Fail if relations is empty", getValid().relations(List.of()).build(), Set.of("error.relations.empty"))
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
