package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.Tila;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

class KoodistoDtoTest extends DtoTest {

    private static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of("Should be valid", getValid().build(), true),
                Arguments.of("Fail if codesGroupUri is not set", getValid().codesGroupUri(null).build(), false),
                Arguments.of("Fail if codesGroupUri is blank", getValid().codesGroupUri("").build(), false),
                Arguments.of("Fail if organisaatioOid is not set", getValid().codesGroupUri(null).build(), false),
                Arguments.of("Fail if organisaatioOid is blank", getValid().codesGroupUri("").build(), false),
                Arguments.of("Fail if voimassaAlkuPvm is not set", getValid().voimassaAlkuPvm(null).build(), false),
                Arguments.of("Fail if voimassaLoppuPvm is before voimassaAlkuPvm", getValid().voimassaLoppuPvm(dateOf(2022, 6, 20)).build(), false),
                Arguments.of("Fail if metadata is not set", getValid().metadata(null).build(), false),
                Arguments.of("Fail if metadata is empty", getValid().metadata(List.of()).build(), false),
                Arguments.of("Fail if invalid metadata", getValid().metadata(List.of(KoodistoMetadataDto.builder().build())).build(), false)
        );
    }

    private static KoodistoDtoBuilder getValid() {
        return KoodistoDtoBuilder
                .builder()
                .versio(1)
                .tila(Tila.LUONNOS)
                .codesGroupUri("codesGroupUri")
                .koodistoUri("koodistoUri")
                .organisaatioOid("organisaatioOid")
                .voimassaAlkuPvm(dateOf(2022, 6, 21))
                .metadata(List.of(KoodistoMetadataDto
                        .builder()
                        .kieli(Kieli.FI)
                        .nimi("nimi")
                        .build()));
    }

    @Setter
    @Accessors(fluent = true)
    @NoArgsConstructor
    public static class KoodistoDtoBuilder {

        private String codesGroupUri;
        private String koodistoUri;
        private String organisaatioOid;
        private Date voimassaAlkuPvm;
        private Date voimassaLoppuPvm;
        private List<KoodistoMetadataDto> metadata;
        private int versio;
        private Tila tila;

        public static KoodistoDtoBuilder builder() {
            return new KoodistoDtoBuilder();
        }

        public KoodistoDto build() {
            KoodistoDto koodisto = new KoodistoDto();
            koodisto.setCodesGroupUri(codesGroupUri);
            koodisto.setKoodistoUri(koodistoUri);
            koodisto.setOrganisaatioOid(organisaatioOid);
            koodisto.setVoimassaAlkuPvm(voimassaAlkuPvm);
            koodisto.setVoimassaLoppuPvm(voimassaLoppuPvm);
            koodisto.setMetadata(metadata);
            koodisto.setVersio(versio);
            koodisto.setTila(tila);
            return koodisto;
        }
    }
}
