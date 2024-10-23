package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.Kieli;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.Set.of;

class KoodistoRyhmaDtoTest extends DtoTest {

    static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of("Should be valid", getValid().build(), Set.of()),
                Arguments.of("Metadata must be set", getValid().koodistoRyhmaMetadatas(null).build(), Set.of("error.metadata.empty")),
                Arguments.of("Metadata cannot be empty", getValid().koodistoRyhmaMetadatas(of()).build(), Set.of("error.metadata.empty")),
                Arguments.of("Metadata is validated", getValid().koodistoRyhmaMetadatas(of(KoodistoRyhmaMetadataDto.builder().build())).build(), Set.of("error.metadata.empty", "error.kieli.empty"))
        );
    }

    private static KoodistoRyhmaDto.KoodistoRyhmaDtoBuilder getValid() {
        return KoodistoRyhmaDto.builder()
                .koodistoRyhmaMetadatas(of(
                        KoodistoRyhmaMetadataDto.builder()
                                .kieli(Kieli.FI)
                                .nimi("nimi")
                                .build()));
    }
}
