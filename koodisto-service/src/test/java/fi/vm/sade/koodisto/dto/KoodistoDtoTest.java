package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
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
                Arguments.of("Fail if metadata is empty", getValid().metadata(List.of()).build(), false)
        );
    }

    private static KoodistoDtoBuilder getValid() {
        KoodistoMetadataType meta = new KoodistoMetadataType();
        meta.setKieli(KieliType.FI);
        return KoodistoDtoBuilder
                .builder()
                .codesGroupUri("codesGroupUri")
                .organisaatioOid("organisaatioOid")
                .voimassaAlkuPvm(dateOf(2022, 6, 21))
                .metadata(List.of(meta));
    }

    @Setter
    @Accessors(fluent = true)
    @NoArgsConstructor
    public static class KoodistoDtoBuilder {

        private String codesGroupUri;
        private String organisaatioOid;
        private Date voimassaAlkuPvm;
        private Date voimassaLoppuPvm;
        private List<KoodistoMetadataType> metadata;

        public static KoodistoDtoBuilder builder() {
            return new KoodistoDtoBuilder();
        }

        public KoodistoDto build() {
            KoodistoDto koodisto = new KoodistoDto();
            koodisto.setCodesGroupUri(codesGroupUri);
            koodisto.setOrganisaatioOid(organisaatioOid);
            koodisto.setVoimassaAlkuPvm(voimassaAlkuPvm);
            koodisto.setVoimassaLoppuPvm(voimassaLoppuPvm);
            koodisto.setMetadata(metadata);
            return koodisto;
        }
    }
}
