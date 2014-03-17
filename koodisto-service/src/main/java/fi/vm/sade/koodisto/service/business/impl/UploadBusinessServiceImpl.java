package fi.vm.sade.koodisto.service.business.impl;

import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.UploadBusinessService;
import fi.vm.sade.koodisto.service.business.exception.KoodistoExportException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoImportException;
import fi.vm.sade.koodisto.service.business.marshaller.KoodistoCsvConverter;
import fi.vm.sade.koodisto.service.business.marshaller.KoodistoXlsConverter;
import fi.vm.sade.koodisto.service.business.marshaller.KoodistoXmlConverter;
import fi.vm.sade.koodisto.service.types.UpdateKoodiDataType;
import fi.vm.sade.koodisto.service.types.common.ExportImportFormatType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                    throw new KoodistoExportException("Unknown koodisto import format!");
            }

            List<UpdateKoodiDataType> updateDatas = new ArrayList<UpdateKoodiDataType>();
            for (KoodiType k : koodis) {
                UpdateKoodiDataType updateData = new UpdateKoodiDataType();
                EntityUtils.copyFields(k, updateData);

                updateData.getMetadata().addAll(k.getMetadata());
                updateDatas.add(updateData);
            }

            koodiBusinessService.massCreate(koodistoUri, updateDatas);
        } catch (IOException e) {
            throw new KoodistoImportException(e);
        }
    }
}
