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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.UploadBusinessService;
import fi.vm.sade.koodisto.service.business.exception.InvalidKoodiCsvLineException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoExportException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoImportException;
import fi.vm.sade.koodisto.service.business.marshaller.KoodistoCsvConverter;
import fi.vm.sade.koodisto.service.business.marshaller.KoodistoXlsConverter;
import fi.vm.sade.koodisto.service.business.marshaller.KoodistoXmlConverter;
import fi.vm.sade.koodisto.service.types.UpdateKoodiDataType;
import fi.vm.sade.koodisto.service.types.common.ExportImportFormatType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;

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
    public void upload(String koodistoUri, ExportImportFormatType format, String encoding, DataHandler file) {
        try {
            List<KoodiType> koodis = null;
            switch (format) {

            case JHS_XML:
                koodis = koodistoXmlConverter.unmarshal(file, encoding);
                break;
            case CSV:
                koodis = koodistoCsvConverter.unmarshal(file, encoding);
                break;
            case XLS:
                koodis = koodistoXlsConverter.unmarshal(file, encoding);
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
                EntityUtils.copyFields(k, updateData);

                updateData.getMetadata().addAll(k.getMetadata());
                updateDatas.add(updateData);
            }
            koodiBusinessService.massCreate(koodistoUri, updateDatas);
        } catch (IOException e) {
            throw new KoodistoImportException(e);
        } catch (InvalidKoodiCsvLineException e) {
            throw new KoodistoImportException(e);
        } catch (UnmarshallingFailureException e) {
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
            String koodiUri = (koodistoUri + "_" + trimKoodiArvo(koodi.getKoodiArvo()));
            koodi.setKoodiUri(koodiUri);
        }
        if (koodi.getVersio() == 0) {
            koodi.setVersio(1);
        }
    }

    private String trimKoodiArvo(String value) {
        return value.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
    }
}
