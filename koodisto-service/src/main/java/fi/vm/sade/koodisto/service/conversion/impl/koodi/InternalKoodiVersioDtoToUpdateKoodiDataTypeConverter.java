package fi.vm.sade.koodisto.service.conversion.impl.koodi;

import fi.vm.sade.koodisto.dto.internal.InternalKoodiVersioDto;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.types.UpdateKoodiDataType;
import fi.vm.sade.koodisto.service.types.UpdateKoodiTilaType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InternalKoodiVersioDtoToUpdateKoodiDataTypeConverter implements
        Converter<InternalKoodiVersioDto, UpdateKoodiDataType> {
    private final KoodiMetadataDtoToKoodiMetadataTypeConverter koodiMetadataDtoToKoodiMetadataTypeConverter;
    @Override
    public UpdateKoodiDataType convert(InternalKoodiVersioDto source) {
        return UpdateKoodiDataType.builder()
                .voimassaAlkuPvm(source.getVoimassaAlkuPvm())
                .voimassaLoppuPvm(source.getVoimassaLoppuPvm())
                .koodiArvo(source.getKoodiArvo())
                .koodiUri(source.getKoodiUri())
                .lockingVersion(source.getLockingVersion())
                .versio(source.getVersio())
                .tila(source.getTila().equals(Tila.HYVAKSYTTY) ? null : UpdateKoodiTilaType.fromValue(source.getTila().toString()))
                .metadata(source.getMetadata().stream().map(koodiMetadataDtoToKoodiMetadataTypeConverter::convert).collect(Collectors.toList()))
                .build();
    }
}
