package fi.vm.sade.koodisto.service.business;

import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.types.common.ExportImportFormatType;
import org.springframework.core.io.Resource;

/**
 * User: kwuoti
 * Date: 11.4.2013
 * Time: 8.58
 */
public interface UploadBusinessService {
    KoodistoVersio upload(String koodistoUri, ExportImportFormatType format, String encoding, Resource resource);
}
