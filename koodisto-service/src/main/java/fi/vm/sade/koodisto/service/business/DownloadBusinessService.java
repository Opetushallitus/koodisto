package fi.vm.sade.koodisto.service.business;

import fi.vm.sade.koodisto.service.types.common.ExportImportFormatType;
import org.springframework.core.io.Resource;

/**
 * User: kwuoti
 * Date: 8.4.2013
 * Time: 11.54
 */
public interface DownloadBusinessService {
    Resource download(String koodistoUri, int koodistoVersio, ExportImportFormatType exportFormat, String encoding);
}
