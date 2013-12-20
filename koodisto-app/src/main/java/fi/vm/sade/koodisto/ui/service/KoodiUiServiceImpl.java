/**
 * 
 */
package fi.vm.sade.koodisto.ui.service;

import fi.vm.sade.koodisto.exception.KoodiNotFoundException;
import fi.vm.sade.koodisto.service.DownloadService;
import fi.vm.sade.koodisto.service.KoodiAdminService;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.UploadService;
import fi.vm.sade.koodisto.service.types.CreateKoodiDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.UpdateKoodiDataType;
import fi.vm.sade.koodisto.service.types.common.*;
import fi.vm.sade.koodisto.ui.koodisto.Encoding;
import fi.vm.sade.koodisto.ui.koodisto.Format;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import org.apache.cxf.jaxrs.ext.multipart.InputStreamDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author tommiha
 * 
 */
@Service
public class KoodiUiServiceImpl implements KoodiUiService {

    @Autowired
    private KoodiAdminService koodiAdminService;

    @Autowired
    private KoodiService koodiService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private DownloadService downloadService;

    @Override
    public KoodiType update(KoodiType koodi) {
        UpdateKoodiDataType updateData = new UpdateKoodiDataType();
        KoodistoHelper.copyFields(koodi, updateData);
        return koodiAdminService.updateKoodi(updateData);
    }

    @Override
    public KoodiType create(KoodistoType koodisto, KoodiType koodi) {
        CreateKoodiDataType createData = new CreateKoodiDataType();
        KoodistoHelper.copyFields(koodi, createData);
        return koodiAdminService.createKoodi(koodisto.getKoodistoUri(), createData);
    }

    @Override
    public List<KoodiType> listKoodisByKoodisto(String koodistoUri, Integer version) {
        SearchKoodisByKoodistoCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(koodistoUri);

        if (version != null) {
            searchCriteria = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUriAndKoodistoVersio(koodistoUri, version);
        }

        return koodiService.searchKoodisByKoodisto(searchCriteria);
    }

    @Override
    public List<KoodiType> listKoodisByKoodisto(Set<String> koodiUris, String koodistoUri, Integer version) {
        SearchKoodisByKoodistoCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder
                .koodisByKoodistoUri(new ArrayList<String>(koodiUris), koodistoUri);

        if (version != null) {
            searchCriteria = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUriAndKoodistoVersio(new ArrayList<String>(koodiUris), koodistoUri, version);
        }

        return koodiService.searchKoodisByKoodisto(searchCriteria);
    }

    @Override
    public void upload(InputStream csvData, String koodistoUri, Format format, Encoding encoding) {
        // String encoding = charsetDetectService.detectCharset(csvData);
        String mime = "";
        String encodingStr = null;
        ExportImportFormatType formatStr = null;

        if (format == Format.CSV) {
            encodingStr = encoding.getStringValue();
            mime = "application/octet-stream; charset=" + encodingStr;
            formatStr = ExportImportFormatType.CSV;
        } else if (format == Format.JHS_XML) {
            formatStr = ExportImportFormatType.JHS_XML;
            mime = "application/xml";
        }
        DataSource ds = new InputStreamDataSource(csvData, mime);
        DataHandler handler = new DataHandler(ds);
        uploadService.upload(koodistoUri, formatStr, encodingStr, handler);
    }

    @Override
    public InputStream download(String koodistoUri, Integer koodistoVersion, Format format, String encoding) throws IOException {
        ExportImportFormatType formatStr = null;

        if (format == Format.CSV) {
            formatStr = ExportImportFormatType.CSV;
        } else if (format == Format.JHS_XML) {
            formatStr = ExportImportFormatType.JHS_XML;
        }

        DataHandler handler = downloadService.download(koodistoUri, koodistoVersion, formatStr, encoding);

        return handler.getInputStream();

    }

    @Override
    public KoodiType getKoodiByUri(String koodiUri) {
        SearchKoodisCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(koodiUri);

        List<KoodiType> result = koodiService.searchKoodis(searchCriteria);
        if (result.size() == 0) {
            throw new KoodiNotFoundException("Koodi (uri: " + koodiUri + ") does not exist");
        }

        return result.get(0);
    }

    @Override
    public List<KoodiType> listKoodiByRelation(String koodiUri, Integer koodiVersio, Boolean isChild, SuhteenTyyppiType suhteenTyyppi) {
        KoodiUriAndVersioType kv = new KoodiUriAndVersioType();
        kv.setKoodiUri(koodiUri);
        kv.setVersio(koodiVersio);

        List<KoodiType> list = koodiService.listKoodiByRelation(kv, isChild, suhteenTyyppi);
        if (list == null) {
            return new ArrayList<KoodiType>();
        }
        return list;
    }

    @Override
    public void delete(KoodiType koodi) {
        koodiAdminService.deleteKoodiVersion(koodi.getKoodiUri(), koodi.getVersio());
    }

    @Override
    public List<KoodiType> listAllKoodiVersiosByUri(String koodiUri) {
        SearchKoodisCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUri(koodiUri);
        return koodiService.searchKoodis(searchCriteria);
    }

    @Override
    public void addRelation(KoodiUriAndVersioType ylakoodi, Set<KoodiUriAndVersioType> alakoodis, SuhteenTyyppiType suhteenTyyppi) {

        List<String> alakoodiUris = new ArrayList<String>();
        for (KoodiUriAndVersioType ak : alakoodis) {
            alakoodiUris.add(ak.getKoodiUri());
        }

        koodiAdminService.addRelationByAlakoodi(ylakoodi.getKoodiUri(), alakoodiUris, suhteenTyyppi);
    }

    @Override
    public KoodiType getKoodiByUriAndVersio(String koodiUri, Integer versio) {
        SearchKoodisCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(koodiUri, versio);

        List<KoodiType> result = koodiService.searchKoodis(searchCriteria);
        if (result.size() == 0) {
            throw new KoodiNotFoundException("Koodi (uri: " + koodiUri + ", versio: " + versio + ") does not exist");
        }

        return result.get(0);
    }

    @Override
    public void addRelation(KoodiUriAndVersioType ylakoodi, KoodiUriAndVersioType alakoodi, SuhteenTyyppiType suhteenTyyppi) {
        koodiAdminService.addRelation(ylakoodi.getKoodiUri(), alakoodi.getKoodiUri(), suhteenTyyppi);
    }

    @Override
    public void addRelation(KoodiUriAndVersioType ylaKoodi, List<KoodiUriAndVersioType> alakoodis, SuhteenTyyppiType suhteenTyyppi) {
        if (alakoodis == null || alakoodis.isEmpty()) {
            return;
        }

        List<String> alakoodiUris = new ArrayList<String>();
        for (KoodiUriAndVersioType ak : alakoodis) {
            alakoodiUris.add(ak.getKoodiUri());
        }

        koodiAdminService.addRelationByAlakoodi(ylaKoodi.getKoodiUri(), alakoodiUris, suhteenTyyppi);
    }

    @Override
    public void removeRelation(KoodiUriAndVersioType ylaKoodi, Set<KoodiUriAndVersioType> alakoodis, SuhteenTyyppiType suhteenTyyppi) {
        removeRelation(ylaKoodi, new ArrayList<KoodiUriAndVersioType>(alakoodis), suhteenTyyppi);
    }

    @Override
    public void removeRelation(KoodiUriAndVersioType ylakoodi, List<KoodiUriAndVersioType> alakoodis, SuhteenTyyppiType suhteenTyyppi) {
        if (alakoodis == null || alakoodis.isEmpty()) {
            return;
        }

        List<String> alakoodiUris = new ArrayList<String>();
        for (KoodiUriAndVersioType ak : alakoodis) {
            alakoodiUris.add(ak.getKoodiUri());
        }

        koodiAdminService.removeRelationByAlakoodi(ylakoodi.getKoodiUri(), alakoodiUris, suhteenTyyppi);
    }

    @Override
    public void removeRelation(KoodiUriAndVersioType ylakoodi, KoodiUriAndVersioType alakoodi, SuhteenTyyppiType suhteenTyyppi) {
        List<String> alakoodis = new ArrayList<String>();
        alakoodis.add(alakoodi.getKoodiUri());
        koodiAdminService.removeRelationByAlakoodi(ylakoodi.getKoodiUri(), alakoodis, suhteenTyyppi);
    }
}
