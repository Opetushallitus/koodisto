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
// TODO poista turhat kommentit
@Component
public class CodeElementResourceConverter {

    @Autowired
    private KoodistoConversionService conversionService;

    private static final Logger logger = LoggerFactory.getLogger(CodesResource.class);

    public UpdateKoodiDataType convertFromDTOToUpdateKoodiDataType(KoodiDto koodiDto) {
        UpdateKoodiDataType updateKoodiDataType = new UpdateKoodiDataType();
        //GregorianCalendar c = new GregorianCalendar();
        //c.setTime(koodiDto.getVoimassaAlkuPvm());
        Date startDate = koodiDto.getVoimassaAlkuPvm();
        Date endDate = null;

        //try {
            if (koodiDto.getVoimassaLoppuPvm() != null) {
               // c.setTime(koodiDto.getVoimassaLoppuPvm());
                endDate = koodiDto.getVoimassaLoppuPvm(); //DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            }
       // } catch (DatatypeConfigurationException e) {
       //     logger.warn("Date couldn't be parsed: ", e);
       // }

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
    
    // TODO If you can do this without duplicating code, please do it :)
    public UpdateKoodiDataType convertFromDTOToUpdateKoodiDataType(ExtendedKoodiDto koodiDto) {
        UpdateKoodiDataType updateKoodiDataType = new UpdateKoodiDataType();
        //GregorianCalendar c = new GregorianCalendar();
        //c.setTime(koodiDto.getVoimassaAlkuPvm());
        Date startDate = koodiDto.getVoimassaAlkuPvm();
        Date endDate = null;

        //try {
            //startDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            if (koodiDto.getVoimassaLoppuPvm() != null) {
                //c.setTime(koodiDto.getVoimassaLoppuPvm());
                endDate = koodiDto.getVoimassaLoppuPvm(); // DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            }
        //} catch (DatatypeConfigurationException e) {
        //    logger.warn("Date couldn't be parsed: ", e);
       // }

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
        //GregorianCalendar c = new GregorianCalendar();
        //c.setTime(koodiDto.getVoimassaAlkuPvm());
        Date startDate = koodiDto.getVoimassaAlkuPvm();
        Date endDate = null;
        //try {
            //startDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            if (koodiDto.getVoimassaLoppuPvm() != null) {
                //c.setTime(koodiDto.getVoimassaLoppuPvm());
                endDate = koodiDto.getVoimassaLoppuPvm(); // DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            }
        //} catch (DatatypeConfigurationException e) {
        //    logger.warn("Date couldn't be parsed: ", e);
        //}
        createKoodiDataType.setVoimassaAlkuPvm(startDate);
        createKoodiDataType.setVoimassaLoppuPvm(endDate);
        createKoodiDataType.setKoodiArvo(koodiDto.getKoodiArvo());

        for (KoodiMetadata koodiMetadata : koodiDto.getMetadata()) {
            createKoodiDataType.getMetadata().add(conversionService.convert(koodiMetadata, KoodiMetadataType.class));
        }

        return createKoodiDataType;
    }

}