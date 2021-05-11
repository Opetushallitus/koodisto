package fi.vm.sade.koodisto.service.business.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.UploadBusinessService;
import fi.vm.sade.koodisto.service.business.exception.InvalidKoodiCsvLineException;
import fi.vm.sade.koodisto.service.business.exception.KoodiArvoEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodiNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodiUriEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoImportException;
import fi.vm.sade.koodisto.service.business.marshaller.KoodistoCsvConverter;
import fi.vm.sade.koodisto.service.business.marshaller.KoodistoXlsConverter;
import fi.vm.sade.koodisto.service.business.marshaller.KoodistoXmlConverter;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.ValidatorUtil;
import fi.vm.sade.koodisto.service.types.UpdateKoodiDataType;
import fi.vm.sade.koodisto.service.types.common.ExportImportFormatType;

/**
 * User: kwuoti
 * Date: 11.4.2013
 * Time: 8.58
 */
@Transactional
@Service
public class UploadBusinessServiceImpl implements UploadBusinessService {

    @Autowired
    private KoodistoXmlConverter koodistoXmlConverter;

    @Autowired
    private KoodistoCsvConverter koodistoCsvConverter;

    @Autowired
    private KoodistoXlsConverter koodistoXlsConverter;

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Override
    public KoodistoVersio upload(String koodistoUri, ExportImportFormatType format, String encoding, DataHandler file) {
        try {
            List<KoodiType> koodis = null;
            switch (format) {

            case JHS_XML:
                koodis = koodistoXmlConverter.unmarshal(file, encoding).getKoodi();
                break;
            case CSV:
                koodis = koodistoCsvConverter.unmarshal(file, encoding).getKoodi();
                break;
            case XLS:
                koodis = koodistoXlsConverter.unmarshal(file, encoding).getKoodi();
                break;
            default:
                throw new KoodistoImportException();
            }

            if (koodis == null || koodis.size() == 0) {
                throw new KoodistoImportException("error.codes.importing.empty.file");
            }
            List<UpdateKoodiDataType> updateDatas = new ArrayList<UpdateKoodiDataType>();
            for (KoodiType k : koodis) {
                checkIfKoodiHasAllFields(k, koodistoUri);
                UpdateKoodiDataType updateData = new UpdateKoodiDataType();
                KoodistoHelper.copyFields(k, updateData);

                updateData.getMetadata().addAll(k.getMetadata());
                updateDatas.add(updateData);
            }
            return koodiBusinessService.massCreate(koodistoUri, updateDatas);
        } catch (IOException | InvalidKoodiCsvLineException | UnmarshallingFailureException e) {
            throw new KoodistoImportException(e);
        }
    }

    private void checkIfKoodiHasAllFields(KoodiType koodi, String koodistoUri) {
        // Lisätään validit arvot, jos sellaista ei ole listattu.
        if (koodi.getVoimassaAlkuPvm() == null) {
            try { // Asetetaan tämä hetki alkupvm:ksi
                GregorianCalendar c = new GregorianCalendar();
                c.setTime(new Date());
                XMLGregorianCalendar currentDate;
                currentDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

                koodi.setVoimassaAlkuPvm(currentDate);
            } catch (DatatypeConfigurationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (StringUtils.isBlank(koodi.getKoodiUri())) {
            ValidatorUtil.checkForBlank(koodi.getKoodiArvo(), new KoodiArvoEmptyException());
            String koodiUri = (koodistoUri + "_" + trimKoodiArvo(koodi.getKoodiArvo()));
            koodi.setKoodiUri(koodiUri);
        }
        if (koodi.getVersio() == 0) {
            koodi.setVersio(1);
        }
        ValidatorUtil.checkForBlank(koodi.getKoodiUri(), new KoodiUriEmptyException());
        ValidatorUtil.checkForBlank(koodi.getKoodiArvo(), new KoodiArvoEmptyException());
        ValidatorUtil.checkCollectionIsNotNullOrEmpty(koodi.getMetadata(), new KoodistoImportException("error.metadata.empty"));
        ValidatorUtil.checkForBlank(koodi.getMetadata().get(0).getNimi(), new KoodiNimiEmptyException());
    }

    private String trimKoodiArvo(String value) {
        return value.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
    }

}
