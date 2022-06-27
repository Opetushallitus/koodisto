package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.Kieli;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class KoodistoMetadataDtoTest extends DtoTest {

    private static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of("Should be valid", getValid().build(), true),
                Arguments.of("Fails when name is not set", getValid().nimi(null).build(), false),
                Arguments.of("Fails when name is blank", getValid().nimi("").build(), false),
                Arguments.of("Fails when lang is not set", getValid().kieli(null).build(), false)
        );
    }

    private static KoodistoMetadataDto.KoodistoMetadataDtoBuilder getValid() {
        return KoodistoMetadataDto.builder().kieli(Kieli.FI).nimi("unit test");
    }
}
