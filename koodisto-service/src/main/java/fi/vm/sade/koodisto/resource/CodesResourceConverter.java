package fi.vm.sade.koodisto.resource;

import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.service.conversion.KoodistoConversionService;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.UpdateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

// TODO commenting

@Component
public class CodesResourceConverter {

    @Autowired
    private KoodistoConversionService conversionService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CodesResource.class);

    public UpdateKoodistoDataType convertFromDTOToUpdateKoodistoDataType(KoodistoDto koodistoDto) {
        UpdateKoodistoDataType updateKoodistoDataType = new UpdateKoodistoDataType();
        //GregorianCalendar c = new GregorianCalendar();
        // c.setTime(koodistoDto.getVoimassaAlkuPvm());
        Date startDate = koodistoDto.getVoimassaAlkuPvm();
        Date endDate = null;
        //try {
            //startDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            if (koodistoDto.getVoimassaLoppuPvm() != null) {
               // c.setTime(koodistoDto.getVoimassaLoppuPvm());
                endDate = koodistoDto.getVoimassaLoppuPvm();// DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            }
       // } catch (DatatypeConfigurationException e) {
       //     LOGGER.warn("Date couldn't be parsed: ", e);
       // }
        updateKoodistoDataType.setCodesGroupUri(koodistoDto.getCodesGroupUri());
        updateKoodistoDataType.setVoimassaAlkuPvm(startDate);
        updateKoodistoDataType.setVoimassaLoppuPvm(endDate);
        updateKoodistoDataType.setKoodistoUri(koodistoDto.getKoodistoUri());
        updateKoodistoDataType.setOmistaja(koodistoDto.getOmistaja());
        updateKoodistoDataType.setOrganisaatioOid(koodistoDto.getOrganisaatioOid());
        updateKoodistoDataType.setVersio(koodistoDto.getVersio());
        updateKoodistoDataType.setTila(TilaType.fromValue(koodistoDto.getTila().toString()));
        updateKoodistoDataType.setLockingVersion(koodistoDto.getVersion());
        for (KoodistoMetadata koodistoMetadata : koodistoDto.getMetadata()) {
            updateKoodistoDataType.getMetadataList().add(conversionService.convert(koodistoMetadata, KoodistoMetadataType.class));
        }

        return updateKoodistoDataType;
    }

    public CreateKoodistoDataType convertFromDTOToCreateKoodistoDataType(KoodistoDto koodistoDto) {
        CreateKoodistoDataType createKoodistoDataType = new CreateKoodistoDataType();
        //GregorianCalendar c = new GregorianCalendar();
        //c.setTime(koodistoDto.getVoimassaAlkuPvm());
        Date startDate = koodistoDto.getVoimassaAlkuPvm();
        Date endDate = null;
        //try {
            //startDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            if (koodistoDto.getVoimassaLoppuPvm() != null) {
                // c.setTime(koodistoDto.getVoimassaLoppuPvm());
                endDate = koodistoDto.getVoimassaLoppuPvm(); //DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            }
        //} catch (DatatypeConfigurationException e) {
        //    LOGGER.warn("Date couldn't be parsed: ", e);
        //}
        createKoodistoDataType.setVoimassaAlkuPvm(startDate);
        createKoodistoDataType.setVoimassaLoppuPvm(endDate);
        createKoodistoDataType.setOmistaja(koodistoDto.getOmistaja());
        createKoodistoDataType.setOrganisaatioOid(koodistoDto.getOrganisaatioOid());
        for (KoodistoMetadata koodistoMetadata : koodistoDto.getMetadata()) {
            createKoodistoDataType.getMetadataList().add(conversionService.convert(koodistoMetadata, KoodistoMetadataType.class));
        }

        return createKoodistoDataType;
    }

}
