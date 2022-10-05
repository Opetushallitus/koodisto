package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.Kieli;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Set;
import java.util.stream.Stream;

class KoodistoMetadataDtoTest extends DtoTest {

    private static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of("Should be valid", getValid().build(), Set.of()),
                Arguments.of("Fails when name is not set", getValid().nimi(null).build(), Set.of("error.koodistometadata.nimi.empty")),
                Arguments.of("Fails when name is blank", getValid().nimi("").build(), Set.of("error.koodistometadata.nimi.empty")),
                Arguments.of("Fails when lang is not set", getValid().kieli(null).build(), Set.of("error.koodistometadata.kieli.empty"))
        );
    }

    private static KoodistoMetadataDto.KoodistoMetadataDtoBuilder getValid() {
        return KoodistoMetadataDto.builder().kieli(Kieli.FI).nimi("unit test");
    }
}
