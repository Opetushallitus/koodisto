package fi.vm.sade.koodisto.service.business.impl;

import fi.vm.sade.koodisto.service.business.DownloadBusinessService;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.exception.KoodistoExportException;
import fi.vm.sade.koodisto.service.business.marshaller.KoodistoCsvConverter;
import fi.vm.sade.koodisto.service.business.marshaller.KoodistoXlsConverter;
import fi.vm.sade.koodisto.service.business.marshaller.KoodistoXmlConverter;
import fi.vm.sade.koodisto.service.conversion.SadeConversionService;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.common.ExportImportFormatType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * User: kwuoti
 * Date: 8.4.2013
 * Time: 11.55
 */
@Transactional(readOnly = true)
@Service
public class DownloadBusinessServiceImpl implements DownloadBusinessService {

    @Autowired
    private KoodistoXmlConverter koodistoXmlConverter;

    @Autowired
    private KoodistoCsvConverter koodistoCsvConverter;

    @Autowired
    private KoodistoXlsConverter koodistoXlsConverter;

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Autowired
    private SadeConversionService conversionService;

    @Override
    public Resource download(String koodistoUri, int koodistoVersio, ExportImportFormatType exportFormat, String encoding) {
        if(koodistoUri.equals("blankKoodistoDocument") && koodistoVersio == -1){
            try {
                return koodistoXlsConverter.getBlankDocument();
            } catch (IOException e) {
                throw new KoodistoExportException(e);
            }
        }
        SearchKoodisByKoodistoCriteriaType searchData =
                KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUriAndKoodistoVersio(koodistoUri, koodistoVersio);

        List<KoodiType> koodiTypes = conversionService.convertAll(
                koodiBusinessService.searchKoodis(searchData), KoodiType.class);
        try {
            Resource returnValue;
            switch (exportFormat) {

                case JHS_XML:
                    returnValue = koodistoXmlConverter.marshal(koodiTypes, encoding);
                    break;
                case CSV:
                    returnValue = koodistoCsvConverter.marshal(koodiTypes, encoding);
                    break;
                case XLS:
                    returnValue = koodistoXlsConverter.marshal(koodiTypes, encoding);
                    break;
                default:
                    throw new KoodistoExportException("error.codes.exporting.unknown.format");
            }

            return returnValue;
        } catch (IOException e) {
            throw new KoodistoExportException(e);
        }
    }
}
