package fi.vm.sade.koodisto.service.conversion.impl.koodisto;

import fi.vm.sade.koodisto.dto.internal.InternalKoodistoPageDto;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InternalKoodistoPageDtoToCreateKoodistoDataTypeConverter
        implements Converter<InternalKoodistoPageDto, CreateKoodistoDataType> {
    private final KoodistoMetadataDtoToKoodistoMetadataTypeConverter koodistoMetadataDtoToKoodistoMetadataTypeConverter;

    @Override
    public CreateKoodistoDataType convert(InternalKoodistoPageDto source) {
        CreateKoodistoDataType koodisto = new CreateKoodistoDataType();
        koodisto.setOrganisaatioOid(source.getOrganisaatioOid());
        koodisto.setVoimassaAlkuPvm(source.getVoimassaAlkuPvm());
        koodisto.setVoimassaLoppuPvm(source.getVoimassaLoppuPvm());
        koodisto.setOmistaja(source.getOmistaja());
        koodisto.getMetadataList().addAll(source.getMetadata().stream()
                .map(koodistoMetadataDtoToKoodistoMetadataTypeConverter::convert)
                .collect(Collectors.toList()));
        return koodisto;
    }

}