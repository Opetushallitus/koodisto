package fi.vm.sade.koodisto.resource;

import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.service.conversion.impl.koodisto.KoodistoMetadataToKoodistoMetadataTypeConverter;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.UpdateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class CodesResourceConverter {

    private final KoodistoMetadataToKoodistoMetadataTypeConverter koodistoMetadataToKoodistoMetadataTypeConverter;

    public UpdateKoodistoDataType convertFromDTOToUpdateKoodistoDataType(KoodistoDto koodistoDto) {
        UpdateKoodistoDataType updateKoodistoDataType = new UpdateKoodistoDataType();
        Date startDate = koodistoDto.getVoimassaAlkuPvm();
        Date endDate = null;

        if (koodistoDto.getVoimassaLoppuPvm() != null) {
            endDate = koodistoDto.getVoimassaLoppuPvm();
        }
        updateKoodistoDataType.setCodesGroupUri(koodistoDto.getCodesGroupUri());
        updateKoodistoDataType.setVoimassaAlkuPvm(startDate);
        updateKoodistoDataType.setVoimassaLoppuPvm(endDate);
        updateKoodistoDataType.setKoodistoUri(koodistoDto.getKoodistoUri());
        updateKoodistoDataType.setOmistaja(koodistoDto.getOmistaja());
        updateKoodistoDataType.setOrganisaatioOid(koodistoDto.getOrganisaatioOid());
        updateKoodistoDataType.setVersio(koodistoDto.getVersio());
        updateKoodistoDataType.setTila(TilaType.fromValue(koodistoDto.getTila().toString()));
        updateKoodistoDataType.setLockingVersion(koodistoDto.getVersion());
        updateKoodistoDataType.getMetadataList().addAll(koodistoDto.getMetadata());

        return updateKoodistoDataType;
    }

    public CreateKoodistoDataType convertFromDTOToCreateKoodistoDataType(KoodistoDto koodistoDto) {
        CreateKoodistoDataType createKoodistoDataType = new CreateKoodistoDataType();
        Date startDate = koodistoDto.getVoimassaAlkuPvm();
        Date endDate = null;
        if (koodistoDto.getVoimassaLoppuPvm() != null) {
            endDate = koodistoDto.getVoimassaLoppuPvm();
        }
        createKoodistoDataType.setVoimassaAlkuPvm(startDate);
        createKoodistoDataType.setVoimassaLoppuPvm(endDate);
        createKoodistoDataType.setOmistaja(koodistoDto.getOmistaja());
        createKoodistoDataType.setOrganisaatioOid(koodistoDto.getOrganisaatioOid());
        createKoodistoDataType.getMetadataList().addAll(koodistoDto.getMetadata());

        return createKoodistoDataType;
    }
}
