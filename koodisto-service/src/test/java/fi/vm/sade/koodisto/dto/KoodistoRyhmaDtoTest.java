package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static java.util.Set.of;

class KoodistoRyhmaDtoTest extends DtoTest {

    private static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of("Should be valid", getValid().build(), true),
                Arguments.of("Metadata must be set", getValid().koodistoRyhmaMetadatas(null).build(), false),
                Arguments.of("Metadata cannot be empty", getValid().koodistoRyhmaMetadatas(of()).build(), false),
                Arguments.of("Metadata is validated", getValid().koodistoRyhmaMetadatas(of(KoodistoRyhmaMetadataDto.builder().build())).build(), false)
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
