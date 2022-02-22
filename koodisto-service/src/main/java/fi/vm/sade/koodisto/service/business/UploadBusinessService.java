package fi.vm.sade.koodisto.service.business;

import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.types.common.ExportImportFormatType;

import javax.activation.DataHandler;

public interface UploadBusinessService {
    KoodistoVersio upload(String koodistoUri, ExportImportFormatType format, String encoding, DataHandler file);
}
