package fi.vm.sade.koodisto.resource;

import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.service.conversion.KoodistoConversionService;
import fi.vm.sade.koodisto.service.types.CreateKoodiDataType;
import fi.vm.sade.koodisto.service.types.UpdateKoodiDataType;
import fi.vm.sade.koodisto.service.types.UpdateKoodiTilaType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class CodeElementResourceConverter {

    @Autowired
    private KoodistoConversionService conversionService;

    private static final Logger logger = LoggerFactory.getLogger(CodesResource.class);

    public UpdateKoodiDataType convertFromDTOToUpdateKoodiDataType(KoodiDto koodiDto) {
        UpdateKoodiDataType updateKoodiDataType = new UpdateKoodiDataType();
        Date startDate = koodiDto.getVoimassaAlkuPvm();
        Date endDate = null;
        if (koodiDto.getVoimassaLoppuPvm() != null) {
            endDate = koodiDto.getVoimassaLoppuPvm();
        }

        updateKoodiDataType.setVoimassaAlkuPvm(startDate);
        updateKoodiDataType.setVoimassaLoppuPvm(endDate);
        updateKoodiDataType.setKoodiArvo(koodiDto.getKoodiArvo());
        updateKoodiDataType.setKoodiUri(koodiDto.getKoodiUri());
        updateKoodiDataType.setVersio(koodiDto.getVersio());
        updateKoodiDataType.setLockingVersion(koodiDto.getVersion());

        if (!koodiDto.getTila().toString().equals("HYVAKSYTTY")) {
            updateKoodiDataType.setTila(UpdateKoodiTilaType.fromValue(koodiDto.getTila().toString()));
        }
        for (KoodiMetadata koodiMetadata : koodiDto.getMetadata()) {
            updateKoodiDataType.getMetadata().add(conversionService.convert(koodiMetadata, KoodiMetadataType.class));
        }

        return updateKoodiDataType;
    }
    
    public UpdateKoodiDataType convertFromDTOToUpdateKoodiDataType(ExtendedKoodiDto koodiDto) {
        UpdateKoodiDataType updateKoodiDataType = new UpdateKoodiDataType();
        Date startDate = koodiDto.getVoimassaAlkuPvm();
        Date endDate = null;

        if (koodiDto.getVoimassaLoppuPvm() != null) {
            endDate = koodiDto.getVoimassaLoppuPvm();
        }

        updateKoodiDataType.setVoimassaAlkuPvm(startDate);
        updateKoodiDataType.setVoimassaLoppuPvm(endDate);
        updateKoodiDataType.setKoodiArvo(koodiDto.getKoodiArvo());
        updateKoodiDataType.setKoodiUri(koodiDto.getKoodiUri());
        updateKoodiDataType.setVersio(koodiDto.getVersio());
        updateKoodiDataType.setLockingVersion(koodiDto.getVersion());

        if (!koodiDto.getTila().toString().equals("HYVAKSYTTY")) {
            updateKoodiDataType.setTila(UpdateKoodiTilaType.fromValue(koodiDto.getTila().toString()));
        }
        for (KoodiMetadata koodiMetadata : koodiDto.getMetadata()) {
            updateKoodiDataType.getMetadata().add(conversionService.convert(koodiMetadata, KoodiMetadataType.class));
        }

        return updateKoodiDataType;
    }

    public CreateKoodiDataType convertFromDTOToCreateKoodiDataType(KoodiDto koodiDto) {
        CreateKoodiDataType createKoodiDataType = new CreateKoodiDataType();
        Date startDate = koodiDto.getVoimassaAlkuPvm();
        Date endDate = null;
        if (koodiDto.getVoimassaLoppuPvm() != null) {
            endDate = koodiDto.getVoimassaLoppuPvm();
        }
        createKoodiDataType.setVoimassaAlkuPvm(startDate);
        createKoodiDataType.setVoimassaLoppuPvm(endDate);
        createKoodiDataType.setKoodiArvo(koodiDto.getKoodiArvo());

        for (KoodiMetadata koodiMetadata : koodiDto.getMetadata()) {
            createKoodiDataType.getMetadata().add(conversionService.convert(koodiMetadata, KoodiMetadataType.class));
        }

        return createKoodiDataType;
    }

}
