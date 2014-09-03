/**
 *
 */
package fi.vm.sade.koodisto.service.business.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;
import javax.annotation.Nonnull;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import fi.vm.sade.authentication.business.service.Authorizer;
import fi.vm.sade.generic.service.exception.NotAuthorizedException;
import fi.vm.sade.koodisto.dao.KoodiDAO;
import fi.vm.sade.koodisto.dao.KoodiVersioDAO;
import fi.vm.sade.koodisto.dao.KoodistoDAO;
import fi.vm.sade.koodisto.dao.KoodistoMetadataDAO;
import fi.vm.sade.koodisto.dao.KoodistoRyhmaDAO;
import fi.vm.sade.koodisto.dao.KoodistoVersioDAO;
import fi.vm.sade.koodisto.dao.KoodistoVersioKoodiVersioDAO;
import fi.vm.sade.koodisto.dao.KoodistonSuhdeDAO;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.dto.KoodistoDto.RelationCodes;
import fi.vm.sade.koodisto.model.Format;
import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;
import fi.vm.sade.koodisto.model.KoodistonSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.DownloadService;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.UriTransliterator;
import fi.vm.sade.koodisto.service.business.exception.KoodiVersioNotPassiivinenException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoKuvausEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNimiNotUniqueException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNotFoundException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoOptimisticLockingException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaNotFoundException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaUriEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoUriEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoVersioNotPassiivinenException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoVersionNumberEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistonSuhdeContainsKoodinSuhdeException;
import fi.vm.sade.koodisto.service.business.exception.KoodistosAlreadyHaveSuhdeException;
import fi.vm.sade.koodisto.service.business.exception.KoodistosHaveDifferentOrganizationsException;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;
import fi.vm.sade.koodisto.service.impl.KoodistoRole;
import fi.vm.sade.koodisto.service.koodisto.rest.CodesResourceConverter;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosVersioSelectionType;
import fi.vm.sade.koodisto.service.types.UpdateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.common.ExportImportFormatType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodistoUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;

/**
 * @author tommiha
 */
@Transactional
@Service("koodistoBusinessService")
public class KoodistoBusinessServiceImpl implements KoodistoBusinessService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    public static final String ROOT_ORG = "1.2.246.562.10.00000000001";

    @Autowired
    private KoodistoDAO koodistoDAO;

    @Autowired
    private KoodistoRyhmaDAO koodistoJoukkoDAO;

    @Autowired
    private KoodistonSuhdeDAO koodistonSuhdeDAO;

    @Autowired
    private KoodistoVersioDAO koodistoVersioDAO;

    @Autowired
    private KoodistoMetadataDAO koodistoMetadataDAO;

    @Autowired
    private KoodiDAO koodiDAO;

    @Autowired
    private KoodiVersioDAO koodiVersioDAO;

    @Autowired
    private KoodistoRyhmaDAO koodistoRyhmaDAO;

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Autowired
    private KoodistoVersioKoodiVersioDAO koodistoVersioKoodiVersioDAO;

    @Autowired
    private Authorizer authorizer;

    @Autowired
    private UriTransliterator uriTransliterator;

    @Autowired
    private DownloadService downloadService;
    
    @Autowired
    private CodesResourceConverter converter;

    @Override
    public KoodistoVersio createKoodisto(List<String> koodistoRyhmaUris, CreateKoodistoDataType createKoodistoData) {
        if (koodistoRyhmaUris == null || koodistoRyhmaUris.isEmpty()) {
            throw new KoodistoRyhmaUriEmptyException("codesgroup.uri.is.empty");
        }

        checkMetadatas(createKoodistoData.getMetadataList());

        List<KoodistoRyhma> koodistoRyhmas = koodistoRyhmaDAO.findByUri(koodistoRyhmaUris);
        if (koodistoRyhmas.isEmpty()) {
            throw new KoodistoRyhmaNotFoundException("codesgroup.uri.not.found");
        }

        // authorize creation
        authorizer.checkOrganisationAccess(createKoodistoData.getOrganisaatioOid(), KoodistoRole.CRUD);

        Koodisto koodisto = new Koodisto();
        EntityUtils.copyFields(createKoodistoData, koodisto);
        koodisto.setKoodistoUri(uriTransliterator.generateKoodistoUriByMetadata(createKoodistoData.getMetadataList()));
        koodisto = koodistoDAO.insert(koodisto);

        KoodistoVersio koodistoVersio = new KoodistoVersio();
        EntityUtils.copyFields(createKoodistoData, koodistoVersio);
        // new koodistos should be set to LUONNOS tila
        koodistoVersio.setTila(Tila.LUONNOS);
        // versioning begins from 1
        koodistoVersio.setVersio(1);
        koodistoVersio.setKoodisto(koodisto);

        for (KoodistoMetadataType m : createKoodistoData.getMetadataList()) {
            checkNimiIsUnique(m.getNimi());
            KoodistoMetadata meta = new KoodistoMetadata();
            EntityUtils.copyFields(m, meta);
            koodistoVersio.addMetadata(meta);
        }

        koodistoVersio = koodistoVersioDAO.insert(koodistoVersio);

        for (KoodistoRyhma kr : koodistoRyhmas) {
            kr.addKoodisto(koodisto);
        }

        return koodistoVersio;
    }

    @Override
    public void addRelation(String ylaKoodisto, String alaKoodisto, SuhteenTyyppi suhteenTyyppi) {
        if (this.hasAnyRelation(ylaKoodisto, alaKoodisto)) {
            throw new KoodistosAlreadyHaveSuhdeException("codes.already.have.relation");
        }
        if (suhteenTyyppi == SuhteenTyyppi.SISALTYY && !userIsRootUser() && !koodistosHaveSameOrganisaatio(ylaKoodisto, alaKoodisto)) {
            throw new KoodistosHaveDifferentOrganizationsException("codes.have.different.organizations");

        }
        KoodistoVersio yla = getLatestKoodistoVersio(ylaKoodisto);
        KoodistoVersio ala = getLatestKoodistoVersio(alaKoodisto);

        addRelation(yla, suhteenTyyppi, ala);
    }

    private void addRelation(KoodistoVersio ylakoodisto, SuhteenTyyppi suhteenTyyppi, KoodistoVersio alakoodisto) {

        KoodistonSuhde koodistonSuhde = new KoodistonSuhde();
        koodistonSuhde.setSuhteenTyyppi(suhteenTyyppi);
        koodistonSuhde.setYlakoodistoVersio(ylakoodisto);
        koodistonSuhde.setAlakoodistoVersio(alakoodisto);
        koodistonSuhde.setVersio(1);
        koodistonSuhdeDAO.insert(koodistonSuhde);

    }

    private void removeRelations(String ylakoodistoUri, List<String> alakoodistoUris, SuhteenTyyppi st) {
        KoodistoVersio ylakoodisto = getLatestKoodistoVersio(ylakoodistoUri);
        KoodistoUriAndVersioType yk = new KoodistoUriAndVersioType();
        yk.setKoodistoUri(ylakoodisto.getKoodisto().getKoodistoUri());
        yk.setVersio(ylakoodisto.getVersio());

        List<KoodistoUriAndVersioType> aks = new ArrayList<KoodistoUriAndVersioType>();
        for (KoodistoVersio ak : getLatestKoodistoVersios(alakoodistoUris.toArray(new String[alakoodistoUris.size()]))) {
            checkForPossibleCodeElementRelations(ylakoodisto, ak);
            KoodistoUriAndVersioType a = new KoodistoUriAndVersioType();
            a.setKoodistoUri(ak.getKoodisto().getKoodistoUri());
            a.setVersio(ak.getVersio());

            aks.add(a);
        }
        koodistonSuhdeDAO.deleteRelations(yk, aks, st);
    }

    @Override
    public void removeRelation(String ylakoodistoUri, List<String> alakoodistoUris, SuhteenTyyppi st) {
        if (alakoodistoUris != null && !alakoodistoUris.isEmpty()) {
            removeRelations(ylakoodistoUri, alakoodistoUris, st);
        }
    }

    private void checkForPossibleCodeElementRelations(KoodistoVersio ylakoodisto, KoodistoVersio alakoodisto) {
        for (KoodistoVersioKoodiVersio ylaKoodiVersio : ylakoodisto.getKoodiVersios()) {
            for (KoodistoVersioKoodiVersio alaKoodiVersio : alakoodisto.getKoodiVersios()) {
                if (koodiBusinessService.hasRelationBetweenCodeElements(ylaKoodiVersio.getKoodiVersio(), alaKoodiVersio.getKoodiVersio())) {
                    throw new KoodistonSuhdeContainsKoodinSuhdeException("codesrelation.contains.codeelementrelations");
                }
            }
        }
    }

    /**
     * Checks that the nimi is unique among koodistos. It's ok if there is
     * another version of this koodisto with the same nimi. Throws an exception
     * if the nimi is not unique.
     */
    private void checkNimiIsUnique(String koodistoUri, String nimi) {
        if (koodistoMetadataDAO.nimiExistsForSomeOtherKoodisto(koodistoUri, nimi)) {
            throw new KoodistoNimiNotUniqueException("codes.name.not.unique");
        }
    }

    private void checkNimiIsUnique(String nimi) {
        if (koodistoMetadataDAO.nimiExists(nimi)) {
            throw new KoodistoNimiNotUniqueException("codes.name.not.unique");
        }
    }

    private void checkRequiredMetadataFields(Collection<KoodistoMetadataType> metadatas) {
        for (KoodistoMetadataType md : metadatas) {
            if (StringUtils.isBlank(md.getNimi())) {
                throw new KoodistoNimiEmptyException("No koodisto nimi defined for language " + md.getKieli().name());
            } else if (StringUtils.isBlank(md.getKuvaus())) {
                throw new KoodistoKuvausEmptyException("No koodisto kuvaus defined for language " + md.getKieli().name());
            }
        }
    }

    private void checkMetadatas(Collection<KoodistoMetadataType> metadatas) {
        if (metadatas == null || metadatas.isEmpty()) {
            throw new MetadataEmptyException("codes.metadata.is.empty");
        } else {
            checkRequiredMetadataFields(metadatas);
        }
    }

    @Override
    public KoodistoVersio updateKoodisto(UpdateKoodistoDataType updateKoodistoData) {
        if (updateKoodistoData == null || StringUtils.isBlank(updateKoodistoData.getKoodistoUri())) {
            throw new KoodistoUriEmptyException("codes.uri.is.empty");
        }

        checkMetadatas(updateKoodistoData.getMetadataList());

        KoodistoVersio latest = getLatestKoodistoVersio(updateKoodistoData.getKoodistoUri());

        if (latest.getVersio() != updateKoodistoData.getVersio()
                || (latest.getVersio() == updateKoodistoData.getVersio() &&
                latest.getVersion() != updateKoodistoData.getLockingVersion())) {
            throw new KoodistoOptimisticLockingException("codes.already.modified");
        }

        changeCodesGroup(updateKoodistoData, latest);
        // authorize update
        authorizer.checkOrganisationAccess(latest.getKoodisto().getOrganisaatioOid(), KoodistoRole.CRUD, KoodistoRole.UPDATE);
        latest = createNewVersionIfNeeded(latest, updateKoodistoData);

        // update the non-version specific fields
        EntityUtils.copyFields(updateKoodistoData, latest.getKoodisto());
        return latest;
    }

    private void changeCodesGroup(final UpdateKoodistoDataType updateKoodistoData, final KoodistoVersio latest) {
        if (updateKoodistoData.getCodesGroupUri() != null && !updateKoodistoData.getCodesGroupUri().isEmpty()) {
            KoodistoRyhma newKoodistoRyhma = null;
            KoodistoRyhma oldKoodistoRyhma = null;
            for (KoodistoRyhma koodistoRyhma : latest.getKoodisto().getKoodistoRyhmas()) {
                if (koodistoRyhma.getKoodistoRyhmaUri().indexOf("kaikki") == -1
                        && !koodistoRyhma.getKoodistoRyhmaUri().equals(updateKoodistoData.getCodesGroupUri())) {
                    oldKoodistoRyhma = koodistoRyhma;
                    newKoodistoRyhma = getKoodistoGroup(updateKoodistoData.getCodesGroupUri());
                }
            }
            if (newKoodistoRyhma != null) {
                oldKoodistoRyhma.removeKoodisto(latest.getKoodisto());
                newKoodistoRyhma.addKoodisto(latest.getKoodisto());
                latest.getKoodisto().removeKoodistoRyhma(oldKoodistoRyhma);
                latest.getKoodisto().addKoodistoRyhma(newKoodistoRyhma);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<KoodistoRyhma> listAllKoodistoRyhmas() {
        return koodistoJoukkoDAO.listAllKoodistoRyhmas();
    }

    @Override
    @Transactional(readOnly = true)
    public KoodistoRyhma getKoodistoGroup(String koodistoGroupUri) {
        List<String> koodistoGroupUris = new ArrayList<String>();
        koodistoGroupUris.add(koodistoGroupUri);
        List<KoodistoRyhma> koodistoGroups = koodistoRyhmaDAO.findByUri(koodistoGroupUris);

        if (koodistoGroups.isEmpty()) {
            throw new KoodistoRyhmaNotFoundException("codesgroup.uri.not.found");
        }

        return koodistoGroups.get(0);
    }

    private KoodistoVersio createNewVersionIfNeeded(KoodistoVersio latest, UpdateKoodistoDataType updateKoodistoData) {
        if (Tila.HYVAKSYTTY.equals(latest.getTila()) && newVersionIsRequired(latest, updateKoodistoData)) {
            latest = createNewVersion(latest, updateKoodistoData);
        } else {
            latest = updateOldVersion(latest, updateKoodistoData);
        }

        return latest;
    }

    private boolean newVersionIsRequired(KoodistoVersio latest, UpdateKoodistoDataType updateKoodistoData) {

        if (latest.getMetadatas().size() != updateKoodistoData.getMetadataList().size()) {
            return true;
        }

        for (KoodistoMetadata oldMeta : latest.getMetadatas()) {
            for (KoodistoMetadataType updateMeta : updateKoodistoData.getMetadataList()) {
                if (oldMeta.getKieli().name().equals(updateMeta.getKieli().name()) && !StringUtils.equals(oldMeta.getNimi(), updateMeta.getNimi())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KoodistoVersio> searchKoodistos(SearchKoodistosCriteriaType searchCriteria) {
        if (searchCriteria != null) {
            if (searchCriteria.getKoodistoVersioSelection() == null) {
                if (searchCriteria.getKoodistoVersio() != null) {
                    searchCriteria.setKoodistoVersioSelection(SearchKoodistosVersioSelectionType.SPECIFIC);
                } else {
                    searchCriteria.setKoodistoVersioSelection(SearchKoodistosVersioSelectionType.LATEST);
                }
            } else if (SearchKoodistosVersioSelectionType.SPECIFIC.equals(searchCriteria.getKoodistoVersioSelection())
                    && searchCriteria.getKoodistoVersio() == null) {
                throw new KoodistoVersionNumberEmptyException("Koodisto version number is empty");
            }
        }

        return koodistoVersioDAO.searchKoodistos(searchCriteria);
    }

    @Override
    @Transactional(readOnly = true)
    public Koodisto getKoodistoByKoodistoUri(String koodistoUri) {
        Koodisto result = koodistoDAO.readByUri(koodistoUri);
        if (result == null) {
            throw new KoodistoNotFoundException("No koodisto found for URI " + koodistoUri);
        }
        Iterator<KoodistoVersio> itr = result.getKoodistoVersios().iterator();
        while (itr.hasNext()) {
            KoodistoVersio koodistoVersio = (KoodistoVersio) itr.next();
            Hibernate.initialize(koodistoVersio);
            Iterator<KoodistoMetadata> itr2 = koodistoVersio.getMetadatas().iterator();
            while (itr2.hasNext()) {
                KoodistoMetadata koodistoMetadata = (KoodistoMetadata) itr2.next();
                Hibernate.initialize(koodistoMetadata);
            }
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public KoodistoVersio getLatestKoodistoVersio(String koodistoUri) {
        SearchKoodistosCriteriaType searchCriteria = KoodistoServiceSearchCriteriaBuilder.latestKoodistoByUri(koodistoUri);
        List<KoodistoVersio> result = koodistoVersioDAO.searchKoodistos(searchCriteria);
        if (result.size() != 1) {
            throw new KoodistoNotFoundException("No koodisto found for URI " + koodistoUri);
        }
        initializeKoodistoVersio(result.get(0));
        return result.get(0);
    }

    private void initializeKoodistoVersio(KoodistoVersio koodistoVersio) {
        for (KoodistonSuhde koodistonSuhde : koodistoVersio.getYlakoodistos()) {
            Hibernate.initialize(koodistonSuhde.getYlakoodistoVersio().getMetadatas());
            Hibernate.initialize(koodistonSuhde.getYlakoodistoVersio().getKoodisto());
        }
        for (KoodistonSuhde koodistonSuhde : koodistoVersio.getAlakoodistos()) {
            Hibernate.initialize(koodistonSuhde.getAlakoodistoVersio().getMetadatas());
            Hibernate.initialize(koodistonSuhde.getAlakoodistoVersio().getKoodisto());
        }
        for (KoodistoRyhma ryhma : koodistoVersio.getKoodisto().getKoodistoRyhmas()) {
            Hibernate.initialize(ryhma);
        }
        for (KoodistoVersio versio : koodistoVersio.getKoodisto().getKoodistoVersios()) {
            Hibernate.initialize(versio);
        }
    }

    private List<KoodistoVersio> getLatestKoodistoVersios(String... koodistoUris) {
        SearchKoodistosCriteriaType searchCriteria = KoodistoServiceSearchCriteriaBuilder.latestKoodistosByUri(koodistoUris);
        List<KoodistoVersio> result = koodistoVersioDAO.searchKoodistos(searchCriteria);
        if (result.size() < 1) {
            throw new KoodistoNotFoundException("No koodisto found for URIs");
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public KoodistoVersio getKoodistoVersio(String koodistoUri, Integer koodistoVersio) {
        SearchKoodistosCriteriaType searchCriteria = KoodistoServiceSearchCriteriaBuilder.koodistoByUriAndVersio(koodistoUri, koodistoVersio);
        List<KoodistoVersio> result = koodistoVersioDAO.searchKoodistos(searchCriteria);
        if (result.size() != 1) {
            throw new KoodistoNotFoundException("No koodisto found for URI " + koodistoUri + " and version " + koodistoVersio);
        }

        initializeKoodistoVersio(result.get(0));

        return result.get(0);
    }

    private KoodistoVersio createNewVersion(KoodistoVersio latest) {
        authorizer.checkOrganisationAccess(latest.getKoodisto().getOrganisaatioOid(), KoodistoRole.CRUD, KoodistoRole.UPDATE);
        if (latest.getTila() != Tila.HYVAKSYTTY) {
            return latest;
        }

        KoodistoVersio input = new KoodistoVersio();
        EntityUtils.copyFields(latest, input);
        for (KoodistoMetadata md : latest.getMetadatas()) {
            KoodistoMetadata newMd = new KoodistoMetadata();
            EntityUtils.copyFields(md, newMd);
            input.addMetadata(newMd);
        }
        return createNewVersion(latest, input);
    }

    private KoodistoVersio createNewVersion(KoodistoVersio base, KoodistoVersio input) {

        logger.info("Creating new version of KoodistoVersio, koodisto id =" + base.getKoodisto().getId() + ", base versio=" + base.getVersio());

        input.setId(null);
        input.setVersion(null);
        Integer newVersion = base.getVersio() + 1;
        input.setVersio(newVersion);
        input.setTila(Tila.LUONNOS);

        Koodisto koodisto = base.getKoodisto();
        authorizer.checkOrganisationAccess(koodisto.getOrganisaatioOid(), KoodistoRole.CRUD, KoodistoRole.UPDATE);

        input.setKoodisto(koodisto);

        input.setVoimassaAlkuPvm(new Date());
        if (input.getVoimassaLoppuPvm() != null
                && (input.getVoimassaLoppuPvm().equals(input.getVoimassaAlkuPvm()) || input.getVoimassaLoppuPvm().before(input.getVoimassaAlkuPvm()))) {
            input.setVoimassaLoppuPvm(null);
        }

        // insert KoodistoVersio
        KoodistoVersio inserted = koodistoVersioDAO.insert(input);

        copyKoodiVersiosFromOldKoodistoToNew(base, inserted);
        koodistonSuhdeDAO.copyRelations(base, inserted);

        return inserted;
    }

    private void copyKoodiVersiosFromOldKoodistoToNew(KoodistoVersio base, KoodistoVersio inserted) {
        for (KoodistoVersioKoodiVersio kv : base.getKoodiVersios()) {
            KoodistoVersioKoodiVersio newRelationEntry = new KoodistoVersioKoodiVersio();

            KoodiVersio koodiVersio = koodiBusinessService.createNewVersion(kv.getKoodiVersio().getKoodi().getKoodiUri());

            newRelationEntry.setKoodiVersio(koodiVersio);
            newRelationEntry.setKoodistoVersio(inserted);
            inserted.addKoodiVersio(newRelationEntry);
        }
    }

    private KoodistoVersio createNewVersion(KoodistoVersio latest, UpdateKoodistoDataType updateKoodistoData) {
        KoodistoVersio newVersio = new KoodistoVersio();
        EntityUtils.copyFields(updateKoodistoData, newVersio);
        newVersio.setVersio(latest.getVersio() + 1);
        newVersio.setTila(Tila.LUONNOS);

        newVersio.setVoimassaAlkuPvm(new Date());
        if (newVersio.getVoimassaLoppuPvm() != null
                && (newVersio.getVoimassaLoppuPvm().equals(newVersio.getVoimassaAlkuPvm()) || newVersio.getVoimassaLoppuPvm().before(
                        newVersio.getVoimassaAlkuPvm()))) {
            newVersio.setVoimassaLoppuPvm(null);
        }

        authorizer.checkOrganisationAccess(latest.getKoodisto().getOrganisaatioOid(), KoodistoRole.CRUD, KoodistoRole.UPDATE);
        Koodisto koodisto = latest.getKoodisto();
        newVersio.setKoodisto(latest.getKoodisto());

        for (KoodistoMetadataType updateMetadata : updateKoodistoData.getMetadataList()) {
            checkNimiIsUnique(updateKoodistoData.getKoodistoUri(), updateMetadata.getNimi());
            KoodistoMetadata newMetadata = new KoodistoMetadata();
            EntityUtils.copyFields(updateMetadata, newMetadata);
            newVersio.addMetadata(newMetadata);
        }

        KoodistoVersio inserted = koodistoVersioDAO.insert(newVersio);

        copyKoodiVersiosFromOldKoodistoToNew(latest, inserted);

        koodistonSuhdeDAO.copyRelations(latest, inserted);

        koodisto.addKoodistoVersion(inserted);
        return inserted;
    }

    private KoodistoVersio updateOldVersion(KoodistoVersio latest, UpdateKoodistoDataType updateKoodistoData) {
        List<KoodistoMetadata> latestMetadatas = new ArrayList<KoodistoMetadata>(latest.getMetadatas());

        outer: for (KoodistoMetadataType updateMetadata : updateKoodistoData.getMetadataList()) {
            checkNimiIsUnique(updateKoodistoData.getKoodistoUri(), updateMetadata.getNimi());
            for (int i = 0; i < latestMetadatas.size(); ++i) {
                KoodistoMetadata oldMetadata = latestMetadatas.get(i);
                // Update the old metadata if the language fields match
                if (oldMetadata.getKieli().name().equals(updateMetadata.getKieli().name())) {
                    EntityUtils.copyFields(updateMetadata, oldMetadata);
                    latestMetadatas.remove(i);
                    continue outer;
                }
            }

            // No old metadata for the language was found so we must insert
            // new metadata
            KoodistoMetadata newMetadata = new KoodistoMetadata();
            EntityUtils.copyFields(updateMetadata, newMetadata);
            newMetadata.setKoodistoVersio(latest);
            latest.addMetadata(newMetadata);
            koodistoMetadataDAO.insert(newMetadata);
        }

        // Delete old metadatas
        for (KoodistoMetadata oldMd : latestMetadatas) {
            latest.removeMetadata(oldMd);
            koodistoMetadataDAO.remove(oldMd);
        }

        // If the latest version is in LUONNOS state and we are updating it to
        // HYVAKSYTTY, we should also set
        // set all the koodis in this koodisto to HYVAKSYTTY
        if (!Tila.HYVAKSYTTY.equals(latest.getTila()) && updateKoodistoData.getTila().equals(TilaType.HYVAKSYTTY)) {
            List<KoodiVersio> koodis = koodiVersioDAO.getKoodiVersiosByKoodistoAndKoodiTila(latest.getId(), Tila.LUONNOS);

            ArrayList<String> koodiUris = new ArrayList<String>();
            for (KoodiVersio koodiVersio : koodis) {
                koodiUris.add(koodiVersio.getKoodi().getKoodiUri());
            }

            List<KoodiVersio> latestKoodis = koodiDAO.getLatestCodeElementVersiosByUrisAndTila(koodiUris, Tila.HYVAKSYTTY);

            for (KoodiVersio latestVersio : latestKoodis) {
                koodiBusinessService.setKoodiTila(latestVersio, TilaType.HYVAKSYTTY);
            }

            KoodistoVersio previousVersion = koodistoVersioDAO.getPreviousKoodistoVersio(latest.getKoodisto().getKoodistoUri(), latest.getVersio());
            if (previousVersion != null) {
                previousVersion.setVoimassaLoppuPvm(new Date());
                setRelationsInPreviousVersionToPassive(previousVersion);
            }
        }

        // Update the version itself
        EntityUtils.copyFields(updateKoodistoData, latest);

        if(latest.getVoimassaAlkuPvm() == null) {
            // Set start date to current date
            latest.setVoimassaAlkuPvm(new Date());
        }

        // Set update date
        latest.setPaivitysPvm(new Date());

        return latest;
    }

    private void setRelationsInPreviousVersionToPassive(KoodistoVersio previousVersion) {
        for (KoodistonSuhde ks : previousVersion.getYlakoodistos()) {
            ks.setAlaKoodistoPassive(true);
        }
        for (KoodistonSuhde ks : previousVersion.getAlakoodistos()) {
            ks.setYlaKoodistoPassive(true);
        }
    }

    @Override
    public KoodistoVersio createNewVersion(String koodistoUri) {
        return createNewVersion(getLatestKoodistoVersio(koodistoUri));
    }

    @Override
    public boolean koodistoExists(String koodistoUri) {
        return koodistoDAO.koodistoUriExists(koodistoUri);
    }

    @Override
    public boolean koodistoExists(String koodistoUri, Integer koodistoVersio) {
        return koodistoVersioDAO.koodistoVersioExists(koodistoUri, koodistoVersio);
    }

    @Override
    public void delete(String koodistoUri, Integer koodistoVersio) {

        KoodistoVersio versio = getKoodistoVersio(koodistoUri, koodistoVersio);

        if (!Tila.PASSIIVINEN.equals(versio.getTila())) {
            throw new KoodistoVersioNotPassiivinenException("Cannot delete koodisto version. Tila must be " + Tila.PASSIIVINEN.name() + ".");
        }

        Koodisto koodisto = koodistoDAO.readByUri(koodistoUri);
        authorizer.checkOrganisationAccess(koodisto.getOrganisaatioOid(), KoodistoRole.CRUD);

        List<KoodiVersio> koodiVersios = koodiVersioDAO.getKoodiVersiosIncludedOnlyInKoodistoVersio(koodistoUri, koodistoVersio);
        for (KoodiVersio kv : koodiVersios) {
            if (!Tila.PASSIIVINEN.equals(kv.getTila())) {
                throw new KoodiVersioNotPassiivinenException("Cannot delete koodisto version. Tila must be " + Tila.PASSIIVINEN.name()
                        + " for all koodi versions.");
            }
        }

        koodisto.removeKoodistoVersion(versio);

        koodisto = koodistoDAO.readByUri(koodistoUri);

        for (KoodiVersio kv : koodiVersios) {
            logger.info("Delete " + kv.getKoodi().getId());
            koodiBusinessService.delete(kv.getKoodi().getKoodiUri(), kv.getVersio(), true);
        }

        koodistoVersioDAO.remove(versio);
        if (koodisto.getKoodistoVersios().size() == 0) {
            for (Koodi koodi : koodisto.getKoodis()) {
                koodiDAO.remove(koodi);
            }
            for (KoodistoRyhma kr : koodisto.getKoodistoRyhmas()) {
                kr.removeKoodisto(koodisto);
            }
            koodistoDAO.remove(koodisto);
        }

    }

    @Override
    public boolean hasAnyRelation(String koodistoUri, String anotherKoodistoUri) {
        final KoodistoVersio koodistoVersio = getLatestKoodistoVersio(koodistoUri);
        final KoodistoVersio koodistoVersio2 = getLatestKoodistoVersio(anotherKoodistoUri);
        List<KoodistonSuhde> relations = new ArrayList<KoodistonSuhde>(koodistoVersio.getAlakoodistos());
        relations.addAll(koodistoVersio.getYlakoodistos());
        if (koodistoUri.equalsIgnoreCase(anotherKoodistoUri)) {
            return Iterables.tryFind(relations, new Predicate<KoodistonSuhde>() {
                @Override
                public boolean apply(KoodistonSuhde input) {
                    return input.getAlakoodistoVersio().equals(input.getYlakoodistoVersio());
                }
            }).isPresent();
        }
        return Iterables.tryFind(relations, new Predicate<KoodistonSuhde>() {

            @Override
            public boolean apply(@Nonnull KoodistonSuhde input) {
                return input.getAlakoodistoVersio().equals(koodistoVersio2) || input.getYlakoodistoVersio().equals(koodistoVersio2);
            }

        }).isPresent();
    }

    private boolean koodistosHaveSameOrganisaatio(String koodistoUri, String anotherKoodistoUri) {
        String organisaatio1 = getKoodistoByKoodistoUri(koodistoUri).getOrganisaatioOid();
        String organisaatio2 = getKoodistoByKoodistoUri(anotherKoodistoUri).getOrganisaatioOid();
        authorizer.checkOrganisationAccess(organisaatio1, KoodistoRole.CRUD);
        return StringUtils.equals(organisaatio1, organisaatio2);
    }

    private boolean userIsRootUser() {
        try {
            authorizer.checkOrganisationAccess(ROOT_ORG, KoodistoRole.CRUD);
        } catch (NotAuthorizedException e) {
            return false;
        }
        return true;
    }

    @Override
    public File downloadFile(String codesUri, int codesVersion, Format fileFormat, String encoding) {
        try {

            String extension = "";
            ExportImportFormatType formatStr = null;
            if (fileFormat == Format.CSV) {
                formatStr = ExportImportFormatType.CSV;
                extension = ".csv";
            } else if (fileFormat == Format.JHS_XML) {
                formatStr = ExportImportFormatType.JHS_XML;
                extension = ".xml";
            } else if (fileFormat == Format.XLS) {
                formatStr = ExportImportFormatType.XLS;
                extension = ".xls";
            }
            if (StringUtils.isBlank(encoding) || !Charset.isSupported(encoding)) {
                encoding = "UTF-8";
            }

            DataHandler handler = downloadService.download(codesUri, codesVersion, formatStr, encoding);

            File file = createTemporaryFile(codesUri, extension, handler);

            return file;
        } catch (IOException e) {
            throw new RuntimeException("Writing Codes to file failed:\n" + e);
        }
    }

    private File createTemporaryFile(String codesUri, String extension, DataHandler handler) throws IOException, FileNotFoundException {
        FileOutputStream fos = null;
        try {
            File file = File.createTempFile(codesUri, extension);
            logger.debug("Created temporary file " + file.getAbsolutePath());
            fos = new FileOutputStream(file);
            IOUtils.copy(handler.getInputStream(), fos);
            fos.close();
            file.deleteOnExit(); // Delete file when VM is closed
            return file;
        } finally {
            if (fos != null)
                fos.close();
        }
    }

    @Override
    public KoodistoVersio saveKoodisto(final KoodistoDto codesDTO) {

        UpdateKoodistoDataType codesDTOAsDataType = converter.convertFromDTOToUpdateKoodistoDataType(codesDTO);
        updateKoodisto(codesDTOAsDataType);
        KoodistoVersio latest = getLatestKoodistoVersio(codesDTO.getKoodistoUri());
        
        Set<KoodistonSuhde> alaKoodistos = latest.getAlakoodistos();
        Set<KoodistonSuhde> ylaKoodistos = latest.getYlakoodistos();

        Set<String> updatedIncludesUris = new HashSet<>();
        Set<String> updatedWithinUris = new HashSet<>();
        Set<String> updatedLevelsWithUris = new HashSet<>();
        for (KoodistonSuhde ks : alaKoodistos) {
            if (!ks.isPassive()) {
                if (ks.getSuhteenTyyppi().equals(SuhteenTyyppi.SISALTYY)) {
                    updatedIncludesUris.add(ks.getAlakoodistoVersio().getKoodisto().getKoodistoUri());
                } else if (ks.getSuhteenTyyppi().equals(SuhteenTyyppi.RINNASTEINEN)) {
                    updatedLevelsWithUris.add(ks.getAlakoodistoVersio().getKoodisto().getKoodistoUri());
                }
            }
        }
        for (KoodistonSuhde ks : ylaKoodistos) {
            if (!ks.isPassive()) {
                if (ks.getSuhteenTyyppi().equals(SuhteenTyyppi.SISALTYY)) {
                    updatedWithinUris.add(ks.getYlakoodistoVersio().getKoodisto().getKoodistoUri());
                } else if (ks.getSuhteenTyyppi().equals(SuhteenTyyppi.RINNASTEINEN)) {
                    updatedLevelsWithUris.add(ks.getYlakoodistoVersio().getKoodisto().getKoodistoUri());
                }
            }
        }

        
        // Add relations
        List<RelationCodes> includes = codesDTO.getIncludesCodes();
        List<RelationCodes> within = codesDTO.getWithinCodes();
        List<RelationCodes> levelsWith = codesDTO.getLevelsWithCodes();

        for (RelationCodes relationCodes : includes) {
            if (!updatedIncludesUris.contains(relationCodes.codesUri) && !relationCodes.passive) {
                 addRelation(latest, SuhteenTyyppi.SISALTYY, getLatestKoodistoVersio(relationCodes.codesUri));
            } else {
                updatedIncludesUris.remove(relationCodes.codesUri);
            }
        }
        for (RelationCodes relationCodes : within) {
            if (!updatedWithinUris.contains(relationCodes.codesUri) && !relationCodes.passive) {
                 addRelation(getLatestKoodistoVersio(relationCodes.codesUri), SuhteenTyyppi.SISALTYY, latest);
            } else {
                updatedWithinUris.remove(relationCodes.codesUri);
            }
        }
        for (RelationCodes relationCodes : levelsWith) {
            if (!updatedLevelsWithUris.contains(relationCodes.codesUri) && !relationCodes.passive) {
                 addRelation(latest, SuhteenTyyppi.RINNASTEINEN, getLatestKoodistoVersio(relationCodes.codesUri));
            } else {
                updatedLevelsWithUris.remove(relationCodes.codesUri);
            }
        }

        removeRelations(latest.getKoodisto().getKoodistoUri(), updatedIncludesUris, updatedWithinUris, updatedLevelsWithUris);

        return latest;
    }

    private void removeRelations(String koodistoUri, Set<String> updatedIncludesUris, Set<String> updatedWithinUris, Set<String> updatedLevelsWithUris) {
        removeRelation(koodistoUri, new ArrayList<>(updatedIncludesUris), SuhteenTyyppi.SISALTYY);
        removeRelation(koodistoUri, new ArrayList<>(updatedLevelsWithUris), SuhteenTyyppi.RINNASTEINEN);
        List<String> koodistoUriAsList = new ArrayList<>(Arrays.asList(koodistoUri));
        for (String yk : updatedWithinUris) {
            removeRelation(yk, koodistoUriAsList, SuhteenTyyppi.SISALTYY);
        }
    }
}
