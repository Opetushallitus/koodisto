package fi.vm.sade.koodisto.service.business;

import fi.vm.sade.koodisto.service.types.common.ExportImportFormatType;

import javax.activation.DataHandler;

/**
 * User: kwuoti
 * Date: 8.4.2013
 * Time: 11.54
 */
public interface DownloadBusinessService {
    DataHandler download(String koodistoUri, int koodistoVersio, ExportImportFormatType exportFormat, String encoding);
}
