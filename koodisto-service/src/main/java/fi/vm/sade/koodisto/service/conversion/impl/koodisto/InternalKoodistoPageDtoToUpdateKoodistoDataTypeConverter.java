package fi.vm.sade.koodisto.service.conversion.impl.koodisto;

import fi.vm.sade.koodisto.dto.internal.InternalKoodistoPageDto;
import fi.vm.sade.koodisto.service.types.UpdateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InternalKoodistoPageDtoToUpdateKoodistoDataTypeConverter
        implements Converter<InternalKoodistoPageDto, UpdateKoodistoDataType> {
    private final KoodistoMetadataDtoToKoodistoMetadataTypeConverter koodistoMetadataDtoToKoodistoMetadataTypeConverter;

    @Override
    public UpdateKoodistoDataType convert(InternalKoodistoPageDto source) {
        UpdateKoodistoDataType koodisto = new UpdateKoodistoDataType();
        koodisto.setCodesGroupUri(source.getKoodistoRyhmaUri());
        koodisto.setKoodistoUri(source.getKoodistoUri());
        koodisto.setTila(TilaType.fromValue(source.getTila().toString()));
        koodisto.setVersio(source.getVersio());
        koodisto.setOrganisaatioOid(source.getOrganisaatioOid());
        koodisto.setLockingVersion(source.getLockingVersion());
        koodisto.setVoimassaAlkuPvm(source.getVoimassaAlkuPvm());
        koodisto.setVoimassaLoppuPvm(source.getVoimassaLoppuPvm());
        koodisto.setOmistaja(source.getOmistaja());
        koodisto.getMetadataList().addAll(source.getMetadata().stream()
                .map(koodistoMetadataDtoToKoodistoMetadataTypeConverter::convert)
                .collect(Collectors.toList()));
        return koodisto;
    }

}