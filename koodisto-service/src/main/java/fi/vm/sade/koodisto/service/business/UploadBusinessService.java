package fi.vm.sade.koodisto.service.business;

import fi.vm.sade.koodisto.service.types.common.ExportImportFormatType;

import javax.activation.DataHandler;

/**
 * User: kwuoti
 * Date: 11.4.2013
 * Time: 8.58
 */
public interface UploadBusinessService {
    void upload(String koodistoUri, ExportImportFormatType format, String encoding, DataHandler file);
}
