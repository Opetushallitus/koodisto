package fi.vm.sade.koodisto.service.business.impl;

import com.google.common.collect.Iterables;
import fi.vm.sade.authorization.NotAuthorizedException;
import fi.vm.sade.javautils.opintopolku_spring_security.Authorizer;
import fi.vm.sade.koodisto.dto.FindOrCreateWrapper;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.dto.KoodistoDto.RelationCodes;
import fi.vm.sade.koodisto.model.*;
//import fi.vm.sade.koodisto.service.DownloadService;
import fi.vm.sade.koodisto.repository.*;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.UriTransliterator;
import fi.vm.sade.koodisto.service.business.exception.*;
import fi.vm.sade.koodisto.service.impl.KoodistoRole;
import fi.vm.sade.koodisto.resource.CodesResourceConverter;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosVersioSelectionType;
import fi.vm.sade.koodisto.service.types.UpdateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.common.ExportImportFormatType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodistoUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;
import org.apache.commons.codec.binary.StringUtils;
//import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;
// import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

@Transactional
@Service("koodistoBusinessService")
public class KoodistoBusinessServiceImpl implements KoodistoBusinessService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    public static final String ROOT_ORG = "1.2.246.562.10.00000000001";

    @Autowired
    private KoodistoRepository koodistoRepository;

    @Autowired
    private KoodistoRyhmaRepository koodistoRyhmaRepository;

    @Autowired
    @Lazy
    private KoodistonSuhdeRepository koodistonSuhdeRepository;

    @Autowired
    private KoodistoVersioRepository koodistoVersioRepository;

    @Autowired
    private KoodistoMetadataRepository koodistoMetadataRepository;

    @Autowired
    @Lazy
    private KoodiRepository koodiRepository;

    @Autowired
    private KoodiVersioRepository koodiVersioRepository;

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Autowired
    private KoodistoVersioKoodiVersioRepository koodistoVersioKoodiVersioRepository;

    @Autowired
    private Authorizer authorizer;

    @Autowired
    private UriTransliterator uriTransliterator;

    // TODO @Autowired
    //private DownloadService downloadService;

    @Autowired
    private CodesResourceConverter converter;

    protected String getCurrentUserOid() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public KoodistoVersio createKoodisto(List<String> koodistoRyhmaUris, CreateKoodistoDataType createKoodistoData) {
        if (koodistoRyhmaUris == null || koodistoRyhmaUris.isEmpty()) {
            throw new KoodistoRyhmaUriEmptyException();
        }

        checkMetadatas(createKoodistoData.getMetadataList());

        List<KoodistoRyhma> koodistoRyhmas = (List<KoodistoRyhma>) koodistoRyhmaRepository.findAllByKoodistoRyhmaUriIn(koodistoRyhmaUris);
        if (koodistoRyhmas.isEmpty()) {
            throw new KoodistoRyhmaNotFoundException();
        }

        // authorize creation
        authorizer.checkOrganisationAccess(createKoodistoData.getOrganisaatioOid(), KoodistoRole.CRUD);

        Koodisto koodisto = new Koodisto();
        EntityUtils.copyFields(createKoodistoData, koodisto);
        koodisto.setKoodistoUri(uriTransliterator.generateKoodistoUriByMetadata(createKoodistoData.getMetadataList()));
        koodisto = koodistoRepository.save(koodisto); // TODO check flushing

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

        koodistoVersio = koodistoVersioRepository.save(koodistoVersio); // TODO check flushing

        for (KoodistoRyhma kr : koodistoRyhmas) {
            kr.addKoodisto(koodisto);
        }

        return koodistoVersio;
    }

    @Override
    @Transactional
    public void addRelation(String ylaKoodisto, String alaKoodisto, SuhteenTyyppi suhteenTyyppi) {
        boolean insert = true;
        if(ylaKoodisto.equals(alaKoodisto)){
            throw new KoodistoRelationToSelfException();
        }
        KoodistoVersio yla = getLatestKoodistoVersio(ylaKoodisto);
        KoodistoVersio ala = getLatestKoodistoVersio(alaKoodisto);
        if (hasAnyRelation(ylaKoodisto, alaKoodisto)) {
            logger.warn("Codes already have non-versioned relation: ylakoodisto=" + ylaKoodisto + ", alaKoodisto=" + alaKoodisto);
            insert = false;
        }
        if (suhteenTyyppi == SuhteenTyyppi.SISALTYY && !userIsRootUser() && !koodistosHaveSameOrganisaatio(ylaKoodisto, alaKoodisto)) {
            throw new KoodistosHaveDifferentOrganizationsException();
        }
        if (insert) {
            KoodistonSuhde koodistonSuhde = new KoodistonSuhde();
            koodistonSuhde.setSuhteenTyyppi(suhteenTyyppi);
            koodistonSuhde.setYlakoodistoVersio(yla);
            koodistonSuhde.setAlakoodistoVersio(ala);
            koodistonSuhde.setVersio(1);

            koodistonSuhdeRepository.save(koodistonSuhde); // TODO should flush hence transactional
        }
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
        koodistonSuhdeRepository.deleteRelations(yk, aks, st);
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
                    throw new KoodistonSuhdeContainsKoodinSuhdeException();
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
        if (koodistoMetadataRepository.nimiExistsForSomeOtherKoodisto(koodistoUri, nimi)) {
            throw new KoodistoNimiNotUniqueException();
        }
    }

    private void checkNimiIsUnique(String nimi) {
        if (koodistoMetadataRepository.existsByNimi(nimi)) {
            throw new KoodistoNimiNotUniqueException();
        }
    }

    private void checkRequiredMetadataFields(Collection<KoodistoMetadataType> metadatas) {
        for (KoodistoMetadataType md : metadatas) {
            if (md.getNimi().isBlank()) {
                logger.error("No koodisto nimi defined for language " + md.getKieli().name());
                throw new KoodistoNimiEmptyException();
            }
        }
    }

    private void checkMetadatas(Collection<KoodistoMetadataType> metadatas) {
        if (metadatas == null || metadatas.isEmpty()) {
            throw new MetadataEmptyException();
        } else {
            checkRequiredMetadataFields(metadatas);
        }
    }

    private void checkTila(KoodistoVersio latest, UpdateKoodistoDataType updateKoodistoData) {
        if (Tila.HYVAKSYTTY.equals(latest.getTila()) && updateKoodistoData.getTila().equals(TilaType.LUONNOS)) {
            logger.error("Invalid state transition (HYVAKSYTTY->LUONNOS) for koodisto " + latest.getKoodisto().getKoodistoUri());
            throw new KoodistoTilaException();
        }
    }

    @Override
    public KoodistoVersio updateKoodisto(UpdateKoodistoDataType updateKoodistoData) {
        if (updateKoodistoData == null || updateKoodistoData.getKoodistoUri().isBlank()) {
            throw new KoodistoUriEmptyException();
        }
        checkMetadatas(updateKoodistoData.getMetadataList());

        KoodistoVersio latest = getLatestKoodistoVersio(updateKoodistoData.getKoodistoUri());

        checkTila(latest, updateKoodistoData);

        if (latest.getVersio() != updateKoodistoData.getVersio()
                || (latest.getVersio() == updateKoodistoData.getVersio() &&
                latest.getVersion() != updateKoodistoData.getLockingVersion())) {
            throw new KoodistoOptimisticLockingException();
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
        return (List<KoodistoRyhma>) koodistoRyhmaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public KoodistoRyhma getKoodistoGroup(String koodistoGroupUri) {
        List<String> koodistoGroupUris = new ArrayList<String>();
        koodistoGroupUris.add(koodistoGroupUri);
        List<KoodistoRyhma> koodistoGroups = (List<KoodistoRyhma>) koodistoRyhmaRepository.findAllByKoodistoRyhmaUriIn(koodistoGroupUris);

        if (koodistoGroups.isEmpty()) {
            throw new KoodistoRyhmaNotFoundException();
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
                logger.error("Koodisto version number is empty");
                throw new KoodistoVersionNumberEmptyException();
            }
        }

        return koodistoVersioRepository.searchKoodistos(searchCriteria);
    }

    @Override
    @Transactional(readOnly = true)
    public Koodisto getKoodistoByKoodistoUri(String koodistoUri) {
        Koodisto result = koodistoRepository.findByKoodistoUri(koodistoUri);
        if (result == null) {
            logger.error("No koodisto found for URI " + koodistoUri);
            throw new KoodistoNotFoundException();
        }
        Iterator<KoodistoVersio> itr = result.getKoodistoVersios().iterator();
        while (itr.hasNext()) {
            KoodistoVersio koodistoVersio = itr.next();
            Hibernate.initialize(koodistoVersio);
            Iterator<KoodistoMetadata> itr2 = koodistoVersio.getMetadatas().iterator();
            while (itr2.hasNext()) {
                KoodistoMetadata koodistoMetadata = itr2.next();
                Hibernate.initialize(koodistoMetadata);
            }
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public KoodistoVersio getLatestKoodistoVersio(String koodistoUri) {
        return getLatestKoodistoVersio(koodistoUri, true);
    }

    @Override
    @Transactional(readOnly = true)
    public KoodistoVersio getLatestKoodistoVersio(String koodistoUri, boolean initialize) {
        SearchKoodistosCriteriaType searchCriteria = KoodistoServiceSearchCriteriaBuilder.latestKoodistoByUri(koodistoUri);
        List<KoodistoVersio> result = koodistoVersioRepository.searchKoodistos(searchCriteria);
        if (result.size() != 1) {
            logger.error("No koodisto found for URI " + koodistoUri);
            throw new KoodistoNotFoundException();
        }
        if (initialize) {
            initializeKoodistoVersio(result.get(0));
        }
        return result.get(0);
    }

    private void initializeKoodistoVersio(KoodistoVersio koodistoVersio) {
        for (KoodistonSuhde koodistonSuhde : koodistoVersio.getYlakoodistos()) {
            koodistonSuhde.getYlakoodistoVersio().getMetadatas().size();
            Hibernate.initialize(koodistonSuhde.getYlakoodistoVersio().getKoodisto());
        }
        koodistoVersio.getAlakoodistos().size();
        for (KoodistonSuhde koodistonSuhde : koodistoVersio.getAlakoodistos()) {
            koodistonSuhde.getAlakoodistoVersio().getMetadatas().size();
            Hibernate.initialize(koodistonSuhde.getAlakoodistoVersio().getKoodisto());
        }
        koodistoVersio.getKoodisto().getKoodistoRyhmas().size();
        for (KoodistoRyhma ryhma : koodistoVersio.getKoodisto().getKoodistoRyhmas()) {
            ryhma.getKoodistoJoukkoMetadatas().size();
        }
        koodistoVersio.getKoodisto().getKoodistoVersios().size();
    }

    private List<KoodistoVersio> getLatestKoodistoVersios(String... koodistoUris) {
        SearchKoodistosCriteriaType searchCriteria = KoodistoServiceSearchCriteriaBuilder.latestKoodistosByUri(koodistoUris);
        List<KoodistoVersio> result = koodistoVersioRepository.searchKoodistos(searchCriteria);
        if (result.size() < 1) {
            logger.error("No koodisto found for URIs");
            throw new KoodistoNotFoundException();
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public KoodistoVersio getKoodistoVersio(String koodistoUri, Integer koodistoVersio) {
        SearchKoodistosCriteriaType searchCriteria = KoodistoServiceSearchCriteriaBuilder.koodistoByUriAndVersio(koodistoUri, koodistoVersio);
        List<KoodistoVersio> result = koodistoVersioRepository.searchKoodistos(searchCriteria);
        if (result.size() != 1) {
            logger.error("No koodisto found for URI " + koodistoUri + " and version " + koodistoVersio);
            throw new KoodistoNotFoundException();
        }

        initializeKoodistoVersio(result.get(0));

        return result.get(0);
    }

    private FindOrCreateWrapper<KoodistoVersio> createNewVersion(KoodistoVersio latest) {
        authorizer.checkOrganisationAccess(latest.getKoodisto().getOrganisaatioOid(), KoodistoRole.CRUD, KoodistoRole.UPDATE);
        if (latest.getTila() != Tila.HYVAKSYTTY) {
            return FindOrCreateWrapper.found(latest);
        }

        KoodistoVersio input = new KoodistoVersio();
        EntityUtils.copyFields(latest, input);
        for (KoodistoMetadata md : latest.getMetadatas()) {
            KoodistoMetadata newMd = new KoodistoMetadata();
            EntityUtils.copyFields(md, newMd);
            input.addMetadata(newMd);
        }
        return FindOrCreateWrapper.created(createNewVersion(latest, input));
    }

    private KoodistoVersio createNewVersion(KoodistoVersio base, KoodistoVersio input) {

        logger.info("Creating new version of KoodistoVersio, koodisto uri={}, base versio={}", base.getKoodisto().getKoodistoUri(), base.getVersio());

        input.setId(null);
        input.setVersion(null);
        input.setVersio(base.getVersio() + 1);
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
        KoodistoVersio inserted = koodistoVersioRepository.save(input); // TODO check flushing

        this.copyKoodiVersiosFromOldKoodistoToNew(base, inserted);
        koodistonSuhdeRepository.copyRelations(base, inserted);
        return inserted;
    }

    private void copyKoodiVersiosFromOldKoodistoToNew(KoodistoVersio base, KoodistoVersio inserted) {
        logger.info("Copying codeElement versios to new Codes version, codes id={}, codes versio={}, new codes versio={}", base.getKoodisto().getId(), base.getVersio(), inserted.getVersio());
        Set<KoodiVersio> newVersions = koodiBusinessService.createNewVersionsNonFlushing(base.getKoodiVersios());
        for (KoodiVersio koodiVersio : newVersions) {
            KoodistoVersioKoodiVersio newRelationEntry = new KoodistoVersioKoodiVersio();
            newRelationEntry.setKoodiVersio(koodiVersio);
            newRelationEntry.setKoodistoVersio(inserted);
            inserted.addKoodiVersio(newRelationEntry);
            logger.info("  Copied codeElement version, codes id={}, codeElement version id={}", inserted.getKoodisto().getId(), koodiVersio.getId());
        }
        // TODO there was a flush here
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

        KoodistoVersio inserted = koodistoVersioRepository.save(newVersio); // TODO there was a flush here
        copyKoodiVersiosFromOldKoodistoToNew(latest, inserted);

        koodistonSuhdeRepository.copyRelations(latest, inserted);

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
            koodistoMetadataRepository.save(newMetadata); // TODO there was a flush
        }

        // Delete old metadatas
        for (KoodistoMetadata oldMd : latestMetadatas) {
            latest.removeMetadata(oldMd);
            koodistoMetadataRepository.delete(oldMd); // TODO check flushing
        }

        // If the latest version is in LUONNOS state and we are updating it to
        // HYVAKSYTTY, we should also set
        // set all the koodis in this koodisto to HYVAKSYTTY
        if (!Tila.HYVAKSYTTY.equals(latest.getTila()) && updateKoodistoData.getTila().equals(TilaType.HYVAKSYTTY)) {
            koodiBusinessService.acceptCodeElements(latest);
            KoodistoVersio previousVersion = koodistoVersioRepository.getPreviousKoodistoVersio(latest.getKoodisto().getKoodistoUri(), latest.getVersio());
            if (previousVersion != null && previousVersion.getVoimassaLoppuPvm() == null) {
                previousVersion.setVoimassaLoppuPvm(new Date());
            }
        } else if(!Tila.PASSIIVINEN.equals(latest.getTila()) && TilaType.PASSIIVINEN.equals(updateKoodistoData.getTila())) {
            setRelationsToPassive(latest);
        }

        // Update the version itself
        EntityUtils.copyFields(updateKoodistoData, latest);
        
        if (latest.getVoimassaAlkuPvm() == null) {
            // Set start date to current date
            latest.setVoimassaAlkuPvm(new Date());
        }

        // Set update date
        latest.setPaivitysPvm(new Date());
        latest.setPaivittajaOid(getCurrentUserOid());

        return latest;
    }

    private void setRelationsToPassive(KoodistoVersio latest) {
        setRelationsToPassiveOrActive(latest, true);
    }

    private void setRelationsToPassiveOrActive(KoodistoVersio latest, boolean setToPassive) {
        for (KoodistonSuhde ks : latest.getAlakoodistos()) {
            ks.setYlaKoodistoPassive(setToPassive);
        }
        for (KoodistonSuhde ks : latest.getYlakoodistos()) {
            ks.setAlaKoodistoPassive(setToPassive);
        }
    }

    @Override
    public FindOrCreateWrapper<KoodistoVersio> createNewVersion(String koodistoUri) {
        return createNewVersion(getLatestKoodistoVersio(koodistoUri, false));
    }

    @Override
    public boolean koodistoExists(String koodistoUri) {
        return koodistoRepository.existsByKoodistoUri(koodistoUri);
    }

    @Override
    public boolean koodistoExists(String koodistoUri, Integer koodistoVersio) {
        return koodistoVersioRepository.existsByKoodistoKoodistoUriAndVersio(koodistoUri, koodistoVersio);
    }

    @Override
    public void delete(String koodistoUri, Integer koodistoVersio) {

        KoodistoVersio versio = getKoodistoVersio(koodistoUri, koodistoVersio);

        if (!Tila.PASSIIVINEN.equals(versio.getTila())) {
            logger.error("Cannot delete koodisto version. Tila must be " + Tila.PASSIIVINEN.name() + ".");
            throw new KoodistoVersioNotPassiivinenException();
        }

        Koodisto koodisto = koodistoRepository.findByKoodistoUri(koodistoUri);
        authorizer.checkOrganisationAccess(koodisto.getOrganisaatioOid(), KoodistoRole.CRUD);

        List<KoodiVersio> koodiVersios = koodiVersioRepository.getKoodiVersiosIncludedOnlyInKoodistoVersio(koodistoUri, koodistoVersio);
        for (KoodiVersio kv : koodiVersios) {
            if (!Tila.PASSIIVINEN.equals(kv.getTila())) {
                logger.error("Cannot delete koodisto version. Tila must be " + Tila.PASSIIVINEN.name() + " for all koodi versions.");
                throw new KoodiVersioNotPassiivinenException();
            }
        }

        koodisto.removeKoodistoVersion(versio);

        koodisto = koodistoRepository.findByKoodistoUri(koodistoUri);

        for (KoodiVersio kv : koodiVersios) {
            logger.info("Delete " + kv.getKoodi().getId());
            koodiBusinessService.delete(kv.getKoodi().getKoodiUri(), kv.getVersio(), true);
        }

        koodistoVersioRepository.delete(versio); // TODO flushing?
        if (koodisto.getKoodistoVersios().size() == 0) {
            for (Koodi koodi : koodisto.getKoodis()) {
                koodiRepository.delete(koodi); // TODO flushing
            }
            for (KoodistoRyhma kr : koodisto.getKoodistoRyhmas()) {
                kr.removeKoodisto(koodisto);
            }
            koodistoRepository.delete(koodisto); // TODO flushing
        } else {
            activateRelationsInLatestKoodistoVersio(koodisto);
        }

    }

    private void activateRelationsInLatestKoodistoVersio(Koodisto koodisto) {
        KoodistoVersio latest = null;
        for (KoodistoVersio kv : koodisto.getKoodistoVersios()) {
            latest = latest == null || latest.getVersio() < kv.getVersio() ? kv : latest;
        }
        setRelationsToPassiveOrActive(latest, false);
    }

    @Override
    public boolean hasAnyRelation(String koodistoUri, String anotherKoodistoUri) {
        final KoodistoVersio koodistoVersio = getLatestKoodistoVersio(koodistoUri);
        final KoodistoVersio koodistoVersio2 = getLatestKoodistoVersio(anotherKoodistoUri);
        List<KoodistonSuhde> relations = new ArrayList<KoodistonSuhde>(koodistoVersio.getAlakoodistos());
        relations.addAll(koodistoVersio.getYlakoodistos());
        if (koodistoUri.equalsIgnoreCase(anotherKoodistoUri)) {
            return Iterables.tryFind(relations, input -> input.getAlakoodistoVersio().equals(input.getYlakoodistoVersio())).isPresent();
        }
        return Iterables.tryFind(relations, input -> input.getAlakoodistoVersio().equals(koodistoVersio2) || input.getYlakoodistoVersio().equals(koodistoVersio2)).isPresent();
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

    /* TODO download @Override
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
            logger.error("Writing Codes to file failed:\n" + e);
            throw new KoodistoExportException();
        }
    }*/

   /*  TODO private File createTemporaryFile(String codesUri, String extension, DataHandler handler) throws IOException, FileNotFoundException {
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
    */
    @Override
    @Transactional
    public KoodistoVersio saveKoodisto(KoodistoDto codesDTO) {

        UpdateKoodistoDataType codesDTOAsDataType = converter.convertFromDTOToUpdateKoodistoDataType(codesDTO);
        updateKoodisto(codesDTOAsDataType);
        String koodistoUri = codesDTO.getKoodistoUri();
        KoodistoVersio latest = getLatestKoodistoVersio(koodistoUri);

        Set<KoodistonSuhde> alaKoodistos = latest.getAlakoodistos();
        Set<KoodistonSuhde> ylaKoodistos = latest.getYlakoodistos();

        Set<String> includesUrisToBeRemoved = new HashSet<>();
        Set<String> withinUrisToBeRemoved = new HashSet<>();
        Set<String> levelsWithUrisToBeRemoved = new HashSet<>();
        for (KoodistonSuhde koodistonSuhde : alaKoodistos) {
            if (!koodistonSuhde.isPassive()) {
                String uri = koodistonSuhde.getAlakoodistoVersio().getKoodisto().getKoodistoUri();
                if (koodistonSuhde.getSuhteenTyyppi().equals(SuhteenTyyppi.SISALTYY)) {
                    includesUrisToBeRemoved.add(uri);
                } else if (koodistonSuhde.getSuhteenTyyppi().equals(SuhteenTyyppi.RINNASTEINEN)) {
                    levelsWithUrisToBeRemoved.add(uri);
                }
            }
        }
        for (KoodistonSuhde koodistonSuhde : ylaKoodistos) {
            if (!koodistonSuhde.isPassive()) {
                String uri = koodistonSuhde.getYlakoodistoVersio().getKoodisto().getKoodistoUri();
                if (koodistonSuhde.getSuhteenTyyppi().equals(SuhteenTyyppi.SISALTYY)) {
                    withinUrisToBeRemoved.add(uri);
                } else if (koodistonSuhde.getSuhteenTyyppi().equals(SuhteenTyyppi.RINNASTEINEN)) {
                    levelsWithUrisToBeRemoved.add(uri);
                }
            }
        }

        Set<String> includesUris = urisAsSet(codesDTO.getIncludesCodes());
        Set<String> withinUris = urisAsSet(codesDTO.getWithinCodes());
        Set<String> levelsWithUris = urisAsSet(codesDTO.getLevelsWithCodes());

        for (String uri : includesUris) {
            if (withinUris.contains(uri) || levelsWithUris.contains(uri)) {
                logger.error("Tried adding multiple relations between codes " + koodistoUri + " and " + uri);
                throw new KoodistosAlreadyHaveSuhdeException();
            }
        }
        for (String uri : withinUris) {
            if (levelsWithUris.contains(uri)) {
                logger.error("Tried adding multiple relations between codes " + koodistoUri + " and " + uri);
                throw new KoodistosAlreadyHaveSuhdeException();
            }
        }

        for (String relationCodes : includesUris) {
            if (!includesUrisToBeRemoved.contains(relationCodes)) {
                addRelation(koodistoUri, relationCodes, SuhteenTyyppi.SISALTYY);
            } else {
                includesUrisToBeRemoved.remove(relationCodes);
                if(relationCodes.equals(koodistoUri)){
                    withinUrisToBeRemoved.remove(relationCodes); // Duplicate if includes self
                }
            }
        }
        for (String relationCodes : withinUris) {
            if (!withinUrisToBeRemoved.contains(relationCodes)) {
                addRelation(relationCodes, koodistoUri, SuhteenTyyppi.SISALTYY);
            } else {
                withinUrisToBeRemoved.remove(relationCodes);
            }
        }
        for (String relationCodes : levelsWithUris) {
            if (!levelsWithUrisToBeRemoved.contains(relationCodes)) {
                addRelation(koodistoUri, relationCodes, SuhteenTyyppi.RINNASTEINEN);
            } else {
                levelsWithUrisToBeRemoved.remove(relationCodes);
            }
        }

        removeRelations(latest.getKoodisto().getKoodistoUri(), includesUrisToBeRemoved, withinUrisToBeRemoved, levelsWithUrisToBeRemoved);

        return getLatestKoodistoVersio(koodistoUri);
    }

    private Set<String> urisAsSet(List<RelationCodes> relations) {
        HashSet<String> result = new HashSet<String>();
        for (RelationCodes r : relations) {
            if(!r.passive) {
                result.add(r.codesUri);
            }
        }
        return result;
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
