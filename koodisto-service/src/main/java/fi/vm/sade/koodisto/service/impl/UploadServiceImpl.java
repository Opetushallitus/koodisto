package fi.vm.sade.koodisto.service.impl;

import fi.vm.sade.koodisto.service.GenericFault;
import fi.vm.sade.koodisto.service.UploadService;
import fi.vm.sade.koodisto.service.business.UploadBusinessService;
import fi.vm.sade.koodisto.service.types.common.ExportImportFormatType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.activation.DataHandler;
import javax.jws.WebParam;

/**
 * User: kwuoti
 * Date: 8.4.2013
 * Time: 11.30
 */
public class UploadServiceImpl implements UploadService {

    @Autowired
    private UploadBusinessService uploadBusinessService;

    @Override
    public void upload(@WebParam(name = "koodistoUri", targetNamespace = "") String koodistoUri,
                       @WebParam(name = "format", targetNamespace = "") ExportImportFormatType format,
                       @WebParam(name = "encoding", targetNamespace = "") String encoding,
                       @WebParam(name = "file", targetNamespace = "") DataHandler file) throws GenericFault {

        uploadBusinessService.upload(koodistoUri, format, encoding, file);
    }
}
