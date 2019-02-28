package fi.vm.sade.koodisto.service.business.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import fi.vm.sade.authentication.business.service.Authorizer;
import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.generic.service.exception.NotAuthorizedException;
import fi.vm.sade.koodisto.dao.*;
import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.ExtendedKoodiDto.RelationCodeElement;
import fi.vm.sade.koodisto.dto.KoodiRelaatioListaDto;
import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.UriTransliterator;
import fi.vm.sade.koodisto.service.business.UserDetailService;
import fi.vm.sade.koodisto.service.business.exception.*;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.business.util.KoodistoItem;
import fi.vm.sade.koodisto.service.impl.KoodistoRole;
import fi.vm.sade.koodisto.service.koodisto.rest.CodeElementResourceConverter;
import fi.vm.sade.koodisto.service.types.*;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Transactional
@Service("koodiBusinessService")
public class KoodiBusinessServiceImpl implements KoodiBusinessService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    public static final String ROOT_ORG = "1.2.246.562.10.00000000001";

    @Autowired
    private KoodiDAO koodiDAO;

    @Autowired
    private KoodiMetadataDAO koodiMetadataDAO;

    @Autowired
    private KoodiVersioDAO koodiVersioDAO;

    @Autowired
    private KoodistoVersioDAO koodistoVersioDAO;

    @Autowired
    private KoodinSuhdeDAO koodinSuhdeDAO;

    @Autowired
    private KoodistoDAO koodistoDAO;

    @Autowired
    private KoodistonSuhdeDAO koodistonSuhdeDAO;

    @Autowired
    private KoodistoBusinessService koodistoBusinessService;

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private Authorizer authorizer;

    @Autowired
    private KoodistoVersioKoodiVersioDAO koodistoVersioKoodiVersioDAO;

    @Autowired
    private UriTransliterator uriTransliterator;

    @Autowired
    private CodeElementResourceConverter converter;

    @Override
    public KoodiVersioWithKoodistoItem createKoodi(String koodistoUri, CreateKoodiDataType createKoodiData) {
        KoodiVersioWithKoodistoItem createKoodiNonFlush = createKoodiNonFlush(koodistoUri, createKoodiData);
        flushAfterCreation();
        return createKoodiNonFlush;
    }

    private KoodiVersioWithKoodistoItem createKoodiNonFlush(String koodistoUri, CreateKoodiDataType createKoodiData) {

        checkMetadatas(createKoodiData.getMetadata());

        // new version of Koodisto is created if necessary
        koodistoBusinessService.createNewVersion(koodistoUri);

        KoodistoVersio koodistoVersio = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
        authorizer.checkOrganisationAccess(koodistoVersio.getKoodisto().getOrganisaatioOid(), KoodistoRole.CRUD);

        return createKoodiNonFlush(koodistoUri, createKoodiData, koodistoVersio);
    }

    private KoodiVersioWithKoodistoItem createKoodiNonFlush(String koodistoUri, CreateKoodiDataType createKoodiData, KoodistoVersio latestKoodistoVersio) {
        if (createKoodiData == null || StringUtils.isBlank(koodistoUri)) {
            logger.warn("KoodistoUri empty");
            throw new KoodistoUriEmptyException();
        }

        checkIfCodeElementValueExistsAlready("", createKoodiData.getKoodiArvo(), latestKoodistoVersio.getKoodiVersios());

        Koodi koodi = new Koodi();
        koodi.setKoodisto(latestKoodistoVersio.getKoodisto());
        koodi.setKoodiUri(uriTransliterator.generateKoodiUriByKoodistoUriAndKoodiArvo(koodistoUri, createKoodiData.getKoodiArvo()));
        koodi = koodiDAO.insertNonFlush(koodi);

        KoodiVersio koodiVersio = new KoodiVersio();
        // versioning begins from 1
        koodiVersio.setVersio(1);
        EntityUtils.copyFields(createKoodiData, koodiVersio);

        // new versio is saved in draft state
        koodiVersio.setTila(Tila.LUONNOS);
        koodiVersio.setKoodi(koodi);

        for (KoodiMetadataType mt : createKoodiData.getMetadata()) {
            KoodiMetadata metadata = new KoodiMetadata();
            EntityUtils.copyFields(mt, metadata);
            koodiVersio.addMetadata(metadata);
        }

        koodiVersio = koodiVersioDAO.insertNonFlush(koodiVersio);

        KoodistoVersioKoodiVersio koodistoVersioRelation = new KoodistoVersioKoodiVersio();
        koodistoVersioRelation.setKoodistoVersio(latestKoodistoVersio);
        koodistoVersioRelation.setKoodiVersio(koodiVersio);
        koodistoVersioKoodiVersioDAO.insertNonFlush(koodistoVersioRelation);

        Set<Integer> versio = new HashSet<>();
        versio.add(latestKoodistoVersio.getVersio());

        return new KoodiVersioWithKoodistoItem(koodiVersio, new KoodistoItem(koodistoUri, versio));
    }

    private void flushAfterCreation() {
        koodiDAO.flush();
        koodistoDAO.flush();
        koodiVersioDAO.flush();
        koodistoVersioDAO.flush();
        koodinSuhdeDAO.flush();
        koodistonSuhdeDAO.flush();
        koodistoVersioKoodiVersioDAO.flush();
    }

    private void checkIfCodeElementValueExistsAlready(String koodiUri,
                                                      String koodiArvo,
                                                      Set<KoodistoVersioKoodiVersio> koodiVersios) {
        for (KoodistoVersioKoodiVersio koodiVersio : koodiVersios) {
            if (!koodiUri.equals(koodiVersio.getKoodiVersio().getKoodi().getKoodiUri()) &&
                    koodiArvo.equals(koodiVersio.getKoodiVersio().getKoodiarvo())) {
                throw new KoodiValueNotUniqueException();
            }
        }
    }

    private KoodiVersioWithKoodistoItem getLatestKoodiVersioWithKoodistoVersioItems(String koodiUri) {
        SearchKoodisCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(koodiUri);
        List<KoodiVersioWithKoodistoItem> result = koodiVersioDAO.searchKoodis(searchCriteria);
        if (result.size() != 1) {
            throw new KoodiNotFoundException();
        }

        return result.get(0);
    }

    @Transactional(readOnly = true)
    @Override
    public KoodiVersio getLatestKoodiVersio(String koodiUri) {
        if (StringUtils.isBlank(koodiUri)) {
            return null;
        }
        return getLatestKoodiVersioWithKoodistoVersioItems(koodiUri).getKoodiVersio();
    }

    private List<KoodiVersioWithKoodistoItem> getLatestKoodiVersios(String... koodiUris) {
        SearchKoodisCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(koodiUris);
        return searchKoodis(searchCriteria);
    }

    private KoodiVersioWithKoodistoItem getKoodiVersioWithKoodistoVersioItems(String koodiUri, Integer koodiVersio) {
        SearchKoodisCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(koodiUri, koodiVersio);
        List<KoodiVersioWithKoodistoItem> result = koodiVersioDAO.searchKoodis(searchCriteria);
        if (result.size() != 1) {
            throw new KoodiNotFoundException();
        }

        return result.get(0);
    }

    public KoodiVersio getKoodiVersio(String koodiUri, Integer koodiVersio) {
        return getKoodiVersioWithKoodistoVersioItems(koodiUri, koodiVersio).getKoodiVersio();
    }

    private void checkRequiredMetadataFields(Collection<KoodiMetadataType> metadatas) {
        for (KoodiMetadataType md : metadatas) {
            if (StringUtils.isBlank(md.getNimi())) {
                logger.error("No koodi nimi defined for language " + md.getKieli().name());
                throw new KoodiNimiEmptyException();
            }
        }
    }

    private void checkMetadatas(Collection<KoodiMetadataType> metadatas) {
        if (metadatas == null || metadatas.isEmpty()) {
            throw new MetadataEmptyException();
        } else {
            checkRequiredMetadataFields(metadatas);
        }
    }

    @Override
    public KoodiVersioWithKoodistoItem updateKoodi(UpdateKoodiDataType updateKoodiData) {
        return updateKoodi(updateKoodiData, false);
    }

    public KoodiVersioWithKoodistoItem updateKoodi(UpdateKoodiDataType updateKoodiData, boolean skipAlreadyUpdatedVerification) {
        if (updateKoodiData == null || StringUtils.isBlank(updateKoodiData.getKoodiUri())) {
            throw new KoodiUriEmptyException();
        }
        KoodiVersioWithKoodistoItem latest = getLatestKoodiVersioWithKoodistoVersioItems(updateKoodiData.getKoodiUri());

        KoodiVersio latestKoodiVersion = latest.getKoodiVersio();
        if (!skipAlreadyUpdatedVerification
                && (latestKoodiVersion.getVersio() != updateKoodiData.getVersio() || (latestKoodiVersion.getVersio() == updateKoodiData.getVersio() && latestKoodiVersion
                        .getVersion() != updateKoodiData.getLockingVersion()))) {
            throw new KoodiOptimisticLockingException();
        }

        KoodistoVersio latestKoodisto = koodistoBusinessService.getLatestKoodistoVersio(latest.getKoodiVersio().getKoodi().getKoodisto().getKoodistoUri());

        if (!latest.getKoodistoItem().getVersios().contains(latestKoodisto.getVersio())) {
            throw new KoodiNotInKoodistoException();
        }

        checkMetadatas(updateKoodiData.getMetadata());

        checkIfCodeElementValueExistsAlready(updateKoodiData.getKoodiUri(), updateKoodiData.getKoodiArvo(), latestKoodisto.getKoodiVersios());

        KoodiVersio newVersion = createNewVersionIfNeeded(latest.getKoodiVersio(), updateKoodiData);

        return getKoodiVersioWithKoodistoVersioItems(newVersion.getKoodi().getKoodiUri(), newVersion.getVersio());
    }

    @Override
    public KoodistoVersio massCreate(String koodistoUri, List<UpdateKoodiDataType> koodiList) {
        if (StringUtils.isBlank(koodistoUri)) {
            throw new KoodistoUriEmptyException();
        }

        KoodistoVersio koodisto = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);

        ArrayList<UpdateKoodiDataType> koodisToBeUpdated = new ArrayList<UpdateKoodiDataType>();
        ArrayList<UpdateKoodiDataType> koodisToBeCreated = new ArrayList<UpdateKoodiDataType>();

        ArrayList<String> koodiUris = new ArrayList<String>();
        for (UpdateKoodiDataType updateData : koodiList) {
            koodiUris.add(updateData.getKoodiUri());
        }

        HashSet<String> koodiUrisInThisKoodisto = new HashSet<String>();
        HashSet<String> koodiArvosInThisKoodisto = new HashSet<String>();
        for (KoodistoVersioKoodiVersio versio : koodisto.getKoodiVersios()) {
            koodiUrisInThisKoodisto.add(versio.getKoodiVersio().getKoodi().getKoodiUri());
            koodiArvosInThisKoodisto.add(versio.getKoodiVersio().getKoodiarvo());
        }

        Map<String, Integer> latestKoodiversios = koodiVersioDAO.getLatestVersionNumbersForUris(koodiUris.toArray(new String[koodiUris.size()]));
        for (UpdateKoodiDataType updateData : koodiList) {
            String uri = updateData.getKoodiUri();
            if (StringUtils.isNotBlank(uri)) {
                KoodiVersio latest = null;
                if (latestKoodiversios.containsKey(uri) && koodiUrisInThisKoodisto.contains(uri)) {
                    latest = getLatestKoodiVersio(uri);
                    if (latest != null && latest.getKoodi().getKoodisto().getKoodistoUri().equals(koodisto.getKoodisto().getKoodistoUri())) {
                        koodisToBeUpdated.add(updateData);
                    } else {
                        koodisToBeCreated.add(updateData);
                    }
                } else if (!koodiArvosInThisKoodisto.contains(updateData.getKoodiArvo())) {
                    koodisToBeCreated.add(updateData);
                    koodiArvosInThisKoodisto.add(updateData.getKoodiArvo());
                }
            }
        }

        for (UpdateKoodiDataType data : koodisToBeUpdated) {
            KoodiVersioWithKoodistoItem updated = updateKoodi(data, true);
            KoodistoVersioKoodiVersio result = koodistoVersioKoodiVersioDAO.findByKoodistoVersioAndKoodiVersio(koodisto.getId(), updated
                    .getKoodiVersio()
                    .getId());
            if (result == null) {
                KoodistoVersioKoodiVersio newRelationEntry = new KoodistoVersioKoodiVersio();
                newRelationEntry.setKoodistoVersio(koodisto);
                newRelationEntry.setKoodiVersio(updated.getKoodiVersio());
                koodistoVersioKoodiVersioDAO.insert(newRelationEntry);
            }
        }

        ArrayList<CreateKoodiDataType> createKoodiList = new ArrayList<CreateKoodiDataType>();
        for (UpdateKoodiDataType data : koodisToBeCreated) {
            CreateKoodiDataType createData = new CreateKoodiDataType();
            EntityUtils.copyFields(data, createData);
            checkMetadatas(createData.getMetadata());
            createKoodiList.add(createData);
        }

        KoodistoVersio newKoodistoVersio = koodisto;
        if (createKoodiList.size() > 0) {
            newKoodistoVersio = koodistoBusinessService.createNewVersion(koodistoUri);
            authorizer.checkOrganisationAccess(newKoodistoVersio.getKoodisto().getOrganisaatioOid(), KoodistoRole.CRUD);
        }

        for (CreateKoodiDataType createData : createKoodiList) {
            createKoodiNonFlush(koodistoUri, createData, newKoodistoVersio);
        }
        flushAfterCreation();

        koodisto.setPaivitysPvm(new Date());
        koodisto.setPaivittajaOid(this.userDetailService.getCurrentUserOid());
        return newKoodistoVersio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KoodiVersioWithKoodistoItem> listByRelation(KoodiUriAndVersioType koodi, SuhteenTyyppi suhdeTyyppi, Boolean isChild) {
        Set<KoodiVersioWithKoodistoItem> koodis = new HashSet<>();
        if (SuhteenTyyppi.RINNASTEINEN.equals(suhdeTyyppi)) {
            koodis.addAll(koodiVersioDAO.listByParentRelation(koodi, suhdeTyyppi));
            koodis.addAll(koodiVersioDAO.listByChildRelation(koodi, suhdeTyyppi));
        } else {
            if (isChild) {
                koodis.addAll(koodiVersioDAO.listByChildRelation(koodi, suhdeTyyppi));
            } else {
                koodis.addAll(koodiVersioDAO.listByParentRelation(koodi, suhdeTyyppi));
            }
        }

        return new ArrayList<>(koodis);
    }

    @Override
    public void addRelation(String codeElementUri, List<String> relatedCodeElements, SuhteenTyyppi st, boolean isChild) {
        addRelation(getLatestKoodiVersio(codeElementUri), relatedCodeElements, st, isChild);
    }

    private void addRelation(KoodiVersio latest, List<String> relatedCodeElements, SuhteenTyyppi st, boolean isChild) {
        if (relatedCodeElements == null || relatedCodeElements.isEmpty()) {
            return;
        }
        if (st == SuhteenTyyppi.SISALTYY && !userIsRootUser() && !koodisHaveSameOrganisaatio(latest.getKoodi().getKoodiUri(), relatedCodeElements)) {
            logger.warn("Failed to add 'sisaltyy' relation from " + latest.getKoodi().getKoodiUri() + " to " + relatedCodeElements);
            throw new KoodisHaveDifferentOrganizationsException();
        }
        if (relatedCodeElements.contains(latest.getKoodi().getKoodiUri())) {
            throw new KoodiRelationToSelfException();
        }

        if (!isChild || st.equals(SuhteenTyyppi.RINNASTEINEN)) { // SISALTAA OR RINNASTUU
            if (SuhteenTyyppi.SISALTYY.equals(st)) {
                koodistoBusinessService.createNewVersion(latest.getKoodi().getKoodisto().getKoodistoUri());
                latest = createNewVersion(getLatestKoodiVersio(latest.getKoodi().getKoodiUri()), false);
                flushAfterCreation();
            }

            List<KoodiVersioWithKoodistoItem> alakoodiVersios = getLatestKoodiVersios(relatedCodeElements.toArray(new String[relatedCodeElements.size()]));
            if (alakoodiVersios.size() == 0) {
                logger.error("Child code uris did not match any code elements");
                throw new KoodiNotFoundException();
            }
            for (KoodiVersioWithKoodistoItem alakoodi : alakoodiVersios) {
                insertRelation(latest, alakoodi, st, true);
            }
            koodinSuhdeDAO.flush();
        } else { // SISALTYY
            List<String> ylakoodiUris = relatedCodeElements;
            String alakoodiUri = latest.getKoodi().getKoodiUri();
            List<KoodiVersioWithKoodistoItem> ylakoodiVersios = getLatestKoodiVersios(ylakoodiUris.toArray(new String[ylakoodiUris.size()]));

            ArrayList<String> koodistos = new ArrayList<>();
            for (KoodiVersioWithKoodistoItem latestYlakoodi : ylakoodiVersios) {
                String latestYlakoodiKoodisto = latestYlakoodi.getKoodistoItem().getKoodistoUri();
                if (!koodistos.contains(latestYlakoodiKoodisto)) {
                    koodistos.add(latestYlakoodiKoodisto);
                }
            }
            for (String koodistoUri : koodistos) {
                koodistoBusinessService.createNewVersion(koodistoUri);
            }
            flushAfterCreation();
            ylakoodiVersios = getLatestKoodiVersios(ylakoodiUris.toArray(new String[ylakoodiUris.size()]));
            for (KoodiVersioWithKoodistoItem latestYlakoodi : ylakoodiVersios) {
                createNewVersion(latestYlakoodi.getKoodiVersio(), false);
            }
            flushAfterCreation();
            ylakoodiVersios = getLatestKoodiVersios(ylakoodiUris.toArray(new String[ylakoodiUris.size()]));
            KoodiVersio latestAlakoodi = getLatestKoodiVersio(alakoodiUri);
            for (KoodiVersioWithKoodistoItem latestYlakoodi : ylakoodiVersios) {
                insertRelation(latestAlakoodi, latestYlakoodi, st, false);
            }
            koodinSuhdeDAO.flush();
        }
    }

    private void insertRelation(KoodiVersio codeElementVersion, KoodiVersioWithKoodistoItem toBeAdded, SuhteenTyyppi st, boolean isChildRelation) {
        KoodistoVersio currentCodes = koodistoBusinessService.getLatestKoodistoVersio(codeElementVersion.getKoodi().getKoodisto().getKoodistoUri());

        Set<KoodistonSuhde> codesRelationsToBeCompared = null;
        Set<KoodistonSuhde> parentCodes = currentCodes.getYlakoodistos();
        Set<KoodistonSuhde> childCodes = currentCodes.getAlakoodistos();
        if (st.equals(SuhteenTyyppi.RINNASTEINEN)) {
            codesRelationsToBeCompared = childCodes;
            codesRelationsToBeCompared.addAll(parentCodes);
        } else {
            codesRelationsToBeCompared = isChildRelation ? childCodes : parentCodes;
        }
        checkCodesHaveRelation(currentCodes.getKoodisto().getKoodistoUri(), codesRelationsToBeCompared, toBeAdded.getKoodistoItem().getKoodistoUri(), st);

        KoodinSuhde koodinSuhde = new KoodinSuhde();
        koodinSuhde.setSuhteenTyyppi(st);
        koodinSuhde.setYlakoodiVersio(isChildRelation ? codeElementVersion : toBeAdded.getKoodiVersio());
        koodinSuhde.setAlakoodiVersio(isChildRelation ? toBeAdded.getKoodiVersio() : codeElementVersion);
        koodinSuhde.setVersio(1);
        koodinSuhdeDAO.insertNonFlush(koodinSuhde);
    }

    private void checkCodesHaveRelation(String codesUri, Set<KoodistonSuhde> existingRelations, String codesUriToBeFound, SuhteenTyyppi st) {
        if (codesUri.equals(codesUriToBeFound)) {
            return;
        }
        for (KoodistonSuhde koodistonSuhde : existingRelations) {
            String childCodesUri = koodistonSuhde.getAlakoodistoVersio().getKoodisto().getKoodistoUri();
            String parentCodesUri = koodistonSuhde.getYlakoodistoVersio().getKoodisto().getKoodistoUri();
            String codesUriToBeCompared = childCodesUri.equals(codesUri) ? parentCodesUri : childCodesUri;
            if (koodistonSuhde.getSuhteenTyyppi().equals(st) && codesUriToBeCompared.equals(codesUriToBeFound)) {
                return;
            }
        }
        logger.error("Tried to add {} relation between codeelements in codes '{}' and '{}'.", st, codesUri, codesUriToBeFound);
        throw new KoodistosHaveNoRelationException();
    }

    @Override
    public void addRelation(KoodiRelaatioListaDto krl) {
        String codeElementUri = krl.getCodeElementUri();
        List<String> relatedCodeElements = krl.getRelations();
        SuhteenTyyppi st = SuhteenTyyppi.valueOf(krl.getRelationType());
        boolean isChild = krl.isChild();
        addRelation(codeElementUri, relatedCodeElements, st, isChild);
    }

    private List<KoodinSuhde> getRelations(KoodiVersio latestYlakoodi, List<String> alakoodiUris, SuhteenTyyppi st, boolean isChild) {
        KoodiUriAndVersioType yk = new KoodiUriAndVersioType();
        yk.setKoodiUri(latestYlakoodi.getKoodi().getKoodiUri());
        yk.setVersio(latestYlakoodi.getVersio());

        List<KoodiUriAndVersioType> aks = new ArrayList<>();
        for (KoodiVersioWithKoodistoItem ak : getLatestKoodiVersios(alakoodiUris.toArray(new String[alakoodiUris.size()]))) {
            KoodiUriAndVersioType a = new KoodiUriAndVersioType();
            a.setKoodiUri(ak.getKoodiVersio().getKoodi().getKoodiUri());
            a.setVersio(ak.getKoodiVersio().getVersio());

            aks.add(a);
        }

        if (isChild) {
            return koodinSuhdeDAO.getWithinRelations(yk, aks, st);
        } else {
            return koodinSuhdeDAO.getRelations(yk, aks, st);
        }
    }

    @Override
    public void removeRelation(String codeElementUri, List<String> relatedCodeElements, SuhteenTyyppi st, boolean isChild) {
        removeRelation(getLatestKoodiVersio(codeElementUri), relatedCodeElements, st, isChild);
    }

    private void removeRelation(KoodiVersio latest, List<String> relatedCodeElements, SuhteenTyyppi st, boolean isChild) {
        if (relatedCodeElements == null || relatedCodeElements.isEmpty()) {
            return;
        }

        List<KoodinSuhde> relations = new ArrayList<>();
        if (!isChild || st.equals(SuhteenTyyppi.RINNASTEINEN)) { // SISALTAA OR RINNASTUU
            if (SuhteenTyyppi.SISALTYY.equals(st)) {
                String koodistoUri = latest.getKoodi().getKoodisto().getKoodistoUri();
                koodistoBusinessService.createNewVersion(koodistoUri);
                flushAfterCreation();
                latest = getLatestKoodiVersio(latest.getKoodi().getKoodiUri());
            }
            relations = getRelations(latest, relatedCodeElements, st, false);

        } else { // SISALTYY
            List<KoodiVersioWithKoodistoItem> ylakoodiVersios = getLatestKoodiVersios(relatedCodeElements.toArray(new String[relatedCodeElements.size()]));
            ArrayList<String> koodistos = new ArrayList<>();
            for (KoodiVersioWithKoodistoItem string : ylakoodiVersios) {
                String latestYlakoodiKoodisto = string.getKoodistoItem().getKoodistoUri();
                if (!koodistos.contains(latestYlakoodiKoodisto)) {
                    koodistos.add(latestYlakoodiKoodisto);
                    koodistoBusinessService.createNewVersion(latestYlakoodiKoodisto);
                    flushAfterCreation();
                }
            }
            relations.addAll(getRelations(latest, relatedCodeElements, st, true));
        }
        koodinSuhdeDAO.massRemove(relations);
        flushAfterCreation();
    }

    @Override
    public void removeRelation(KoodiRelaatioListaDto krl) {
        String codeElementUri = krl.getCodeElementUri();
        List<String> relatedCodeElements = krl.getRelations();
        SuhteenTyyppi st = SuhteenTyyppi.valueOf(krl.getRelationType());
        boolean isChild = krl.isChild();
        removeRelation(codeElementUri, relatedCodeElements, st, isChild);
    }

    private KoodiVersio createNewVersionIfNeeded(KoodiVersio latest, UpdateKoodiDataType updateKoodiData) {
        if ((Tila.HYVAKSYTTY.equals(latest.getTila()) && newVersionIsRequired(latest, updateKoodiData))
                || (Tila.HYVAKSYTTY.equals(latest.getTila()) && UpdateKoodiTilaType.LUONNOS.equals(updateKoodiData.getTila()))
                || (!Tila.PASSIIVINEN.equals(latest.getTila()) && updateKoodiData.getTila() != null && UpdateKoodiTilaType.PASSIIVINEN.equals(updateKoodiData
                        .getTila()))) {
            logger.info("KoodiVersio: " + latest.getVersio());

            // Create a new version (if needed) of the koodisto too
            KoodistoVersio newKoodistoVersion = koodistoBusinessService.createNewVersion(latest.getKoodi().getKoodisto().getKoodistoUri());

            // create new version
            return createNewVersion(updateKoodiData, newKoodistoVersion);
        } else {
            // --> just update old version
            return updateOldVersion(latest, updateKoodiData);
        }
    }

    private boolean newVersionIsRequired(KoodiVersio latest, UpdateKoodiDataType updateKoodiData) {
        // if arvo has changed, new version is required
        if (!latest.getKoodiarvo().equals(updateKoodiData.getKoodiArvo())) {
            return true;
        }

        if (latest.getMetadatas().size() != updateKoodiData.getMetadata().size()) {
            return true;
        }

        // otherwise, if name in any metadata has changed, new version is
        // required
        for (KoodiMetadata md : latest.getMetadatas()) {
            for (KoodiMetadataType updateMetadata : updateKoodiData.getMetadata()) {
                if (md.getKieli().name().equals(updateMetadata.getKieli().name())
                        && !(StringUtils.equals(md.getNimi(), updateMetadata.getNimi()) && StringUtils.equals(md.getLyhytNimi(), updateMetadata.getLyhytNimi()))) {
                    return true;
                }
            }
        }
        return false;
    }

    private KoodiVersio updateOldVersion(KoodiVersio latest, UpdateKoodiDataType updateKoodiData) {

        List<KoodiMetadata> latestMetadatas = new ArrayList<>(latest.getMetadatas());

        outer: for (KoodiMetadataType updateMetadata : updateKoodiData.getMetadata()) {
            for (int i = 0; i < latestMetadatas.size(); ++i) {
                KoodiMetadata oldMd = latestMetadatas.get(i);
                // Update the old metadata if the language fields match
                if (oldMd.getKieli().name().equals(updateMetadata.getKieli().name())) {
                    EntityUtils.copyFields(updateMetadata, oldMd);
                    latestMetadatas.remove(i);
                    continue outer;
                }
            }

            // No old metadata for the language was found so we must insert new
            // metadata
            KoodiMetadata newMetadata = new KoodiMetadata();
            EntityUtils.copyFields(updateMetadata, newMetadata);
            newMetadata.setKoodiVersio(latest);
            latest.addMetadata(newMetadata);
            koodiMetadataDAO.insert(newMetadata);
        }

        // Delete old metadatas
        for (KoodiMetadata oldMd : latestMetadatas) {
            latest.removeMetadata(oldMd);
            koodiMetadataDAO.remove(oldMd);
        }

        // Update the version itself by copying all the fields
        EntityUtils.copyFields(updateKoodiData, latest);
        if (UpdateKoodiTilaType.PASSIIVINEN.equals(updateKoodiData.getTila())) {
            setRelationsInPreviousVersionToPassive(latest);
        }

        // Set update information
        latest.setPaivitysPvm(new Date());
        latest.setPaivittajaOid(this.userDetailService.getCurrentUserOid());
        return latest;
    }

    private void setRelationsInPreviousVersionToPassive(KoodiVersio previous) {
        setRelationsToPassiveOrActive(previous, true);
    }

    private void setRelationsToPassiveOrActive(KoodiVersio koodiVersio, boolean setToPassive) {
        for (KoodinSuhde ks : koodiVersio.getAlakoodis()) {
            ks.setYlaKoodiPassive(setToPassive);
        }
        for (KoodinSuhde ks : koodiVersio.getYlakoodis()) {
            ks.setAlaKoodiPassive(setToPassive);
        }
    }

    private KoodiVersio createNewVersion(UpdateKoodiDataType updateKoodiData, KoodistoVersio newKoodistoVersio) {
        KoodiVersio latest = getLatestKoodiVersio(updateKoodiData.getKoodiUri());
        if (updateKoodiData.getTila() == null || !UpdateKoodiTilaType.PASSIIVINEN.equals(updateKoodiData.getTila())) {
            updateKoodiData.setTila(UpdateKoodiTilaType.LUONNOS);
        }

        if (Tila.LUONNOS.equals(latest.getTila())) {
            return updateOldVersion(latest, updateKoodiData);
        }

        logger.info("Creating new version of KoodiVersio, base version =" + latest.getVersio());

        KoodiVersio newVersio = new KoodiVersio();
        EntityUtils.copyFields(updateKoodiData, newVersio);
        newVersio.setVersio(latest.getVersio() + 1);
        newVersio.setKoodi(latest.getKoodi());

        // insert new metadatas
        for (KoodiMetadataType md : updateKoodiData.getMetadata()) {
            KoodiMetadata metadata = new KoodiMetadata();
            EntityUtils.copyFields(md, metadata);
            newVersio.addMetadata(metadata);
        }

        // insert new version
        KoodiVersio inserted = koodiVersioDAO.insert(newVersio);
        copyRelations(latest, inserted);

        KoodistoVersioKoodiVersio relation = koodistoVersioKoodiVersioDAO.findByKoodistoVersioAndKoodiVersio(newKoodistoVersio.getId(), latest.getId());
        if (relation != null) {
            latest.removeKoodistoVersio(relation);
            newKoodistoVersio.removeKoodiVersio(relation);
            koodistoVersioKoodiVersioDAO.remove(relation);
        }

        KoodistoVersioKoodiVersio newRelation = new KoodistoVersioKoodiVersio();
        newRelation.setKoodistoVersio(newKoodistoVersio);
        newRelation.setKoodiVersio(inserted);
        inserted.addKoodistoVersio(newRelation);
        koodistoVersioKoodiVersioDAO.insert(newRelation);

        return inserted;
    }

    private KoodiVersio createNewVersion(KoodiVersio latest, boolean doNonFlushingInsert) {
        if (!Tila.HYVAKSYTTY.equals(latest.getTila())) {
            return latest;
        }

        KoodiVersio newVersio = new KoodiVersio();
        EntityUtils.copyFields(latest, newVersio);
        for (KoodiMetadata m : latest.getMetadatas()) {
            KoodiMetadata newMeta = new KoodiMetadata();
            EntityUtils.copyFields(m, newMeta);
            newVersio.addMetadata(newMeta);
        }
        return createNewVersion(latest, newVersio, doNonFlushingInsert);
    }

    private KoodiVersio createNewVersion(KoodiVersio base, KoodiVersio input, boolean doNonFlushingInsert) {
        input.setId(null);
        input.setVersio(base.getVersio() + 1);
        input.setTila(Tila.LUONNOS);

        input.setKoodi(base.getKoodi());
        KoodiVersio inserted = doNonFlushingInsert ? koodiVersioDAO.insertNonFlush(input) : koodiVersioDAO.insert(input);
        copyRelations(base, inserted);

        return inserted;
    }

    private void copyRelations(KoodiVersio latest, KoodiVersio newVersio) {
        for (KoodinSuhde ks : latest.getYlakoodis()) {
            createNewKoodinSuhde(ks, newVersio, ks.getYlakoodiVersio());
        }
        for (KoodinSuhde ks : latest.getAlakoodis()) {
            createNewKoodinSuhde(ks, ks.getAlakoodiVersio(), newVersio);
        }
        setRelationsInPreviousVersionToPassive(latest);
    }

    private void createNewKoodinSuhde(KoodinSuhde ks, KoodiVersio ala, KoodiVersio yla) {
        if (ks.isPassive()) {
            return;
        }
        KoodinSuhde newSuhde = new KoodinSuhde();
        newSuhde.setVersio(ks.getVersio() + 1);
        newSuhde.setAlakoodiVersio(ala);
        newSuhde.setYlakoodiVersio(yla);
        newSuhde.setSuhteenTyyppi(ks.getSuhteenTyyppi());
        yla.addAlakoodi(newSuhde);
        ala.addYlakoodi(newSuhde);
    }

    @Override
    public KoodiVersio createNewVersion(String koodiUri) {
        return createNewVersion(getLatestKoodiVersio(koodiUri), false);
    }

    @Override
    public Set<KoodiVersio> createNewVersionsNonFlushing(Set<KoodistoVersioKoodiVersio> koodiVersios) {
        HashSet<KoodiVersio> inserted = new HashSet<>();
        for (KoodistoVersioKoodiVersio koodiVersio : koodiVersios) {
            inserted.add(this.createNewVersion(koodiVersio.getKoodiVersio(), true));
        }
        return inserted;
    }

    @Override
    public void setKoodiTila(KoodiVersio latest, TilaType tila) {
        if (Tila.LUONNOS.equals(latest.getTila()) && TilaType.HYVAKSYTTY.equals(tila)) {
            setPreviousVersionEndDateToNowOrKeepFutureDate(latest);
            latest.setTila(Tila.valueOf(tila.name()));
        }
    }

    private void setPreviousVersionEndDateToNowOrKeepFutureDate(KoodiVersio latest) {
        KoodiVersio previousVersion = koodiVersioDAO.getPreviousKoodiVersio(latest.getKoodi().getKoodiUri(), latest.getVersio());
        if (previousVersion != null) {
            previousVersion.setVoimassaLoppuPvm(getValidEndDateForKoodiVersio(previousVersion, latest));
        }
    }

    private Date getValidEndDateForKoodiVersio(KoodiVersio previous, KoodiVersio latest) {
        final Date previousStartDate = previous.getVoimassaAlkuPvm();
        final Date latestStartDate = latest.getVoimassaAlkuPvm();
        if (latestStartDate.after(previousStartDate) && latestStartDate.after(new Date())) {
            return latestStartDate;
        }
        return previousStartDate.after(new Date()) ? previousStartDate : new Date();
    }

    @Override
    public void setKoodiTila(String koodiUri, TilaType tila) {
        KoodiVersio latest = getLatestKoodiVersio(koodiUri);
        setKoodiTila(latest, tila);
    }

    @Override
    public void acceptCodeElements(KoodistoVersio latest) {
        List<KoodiVersio> koodis = koodiVersioDAO.getKoodiVersiosByKoodistoAndKoodiTila(latest.getId(), Tila.LUONNOS);
        if (koodis.size() > 0) {
            ArrayList<String> koodiUris = new ArrayList<>();
            for (KoodiVersio koodiVersio : koodis) {
                koodiUris.add(koodiVersio.getKoodi().getKoodiUri());
            }
            List<KoodiVersio> latestKoodis = koodiDAO.getLatestCodeElementVersiosByUrisAndTila(koodiUris, Tila.HYVAKSYTTY);
            Map<KoodiVersio, KoodiVersio> previousVersions = koodiVersioDAO.getPreviousKoodiVersios(koodis);
            for (KoodiVersio latestVersio : latestKoodis) {
                latestVersio.setTila(Tila.HYVAKSYTTY);
            }
            for (Entry<KoodiVersio, KoodiVersio> entry : previousVersions.entrySet()) {
                KoodiVersio latestVersion = entry.getKey();
                KoodiVersio previousVersion = entry.getValue();
                previousVersion.setVoimassaLoppuPvm(getValidEndDateForKoodiVersio(previousVersion, latestVersion));
            }
        } else {
            throw new KoodistoEmptyException();
        }
    }

    @Override
    public void delete(String koodiUri, Integer koodiVersio, boolean skipPassiivinenCheck) {
        KoodiVersioWithKoodistoItem kvkoodisto = getKoodiVersioWithKoodistoVersioItems(koodiUri, koodiVersio);
        KoodiVersio versio = kvkoodisto.getKoodiVersio();
        if (!skipPassiivinenCheck && !Tila.PASSIIVINEN.equals(versio.getTila())) {
            logger.error("Cannot delete koodi version. Tila must be " + Tila.PASSIIVINEN.name() + ".");
            throw new KoodiVersioNotPassiivinenException();
        }
        List<KoodiVersioWithKoodistoItem> codes = listByRelation(koodiUri, koodiVersio, false, SuhteenTyyppi.RINNASTEINEN);
        if (codes == null || codes.size() == 0) {
            codes = listByRelation(koodiUri, koodiVersio, false, SuhteenTyyppi.SISALTYY);
        }

        if (codes == null || codes.size() == 0) {
            codes = listByRelation(koodiUri, koodiVersio, true, SuhteenTyyppi.SISALTYY);
        }

        if (codes != null && codes.size() > 0) {
            throw new KoodiVersioHasRelationsException();

        }
        KoodistoVersio latestKoodistoVersio = koodistoBusinessService.getLatestKoodistoVersio(kvkoodisto.getKoodistoItem().getKoodistoUri());

        if (kvkoodisto.getKoodistoItem().getVersios().contains(latestKoodistoVersio.getVersio()) && latestKoodistoVersio.getTila().equals(Tila.HYVAKSYTTY)) {

            KoodistoVersio newKoodistoVersio = koodistoBusinessService.createNewVersion(latestKoodistoVersio.getKoodisto().getKoodistoUri());
            koodistoVersioKoodiVersioDAO.flush();
            KoodistoVersioKoodiVersio koodistoVersioKoodiVersio = koodistoVersioKoodiVersioDAO.findByKoodistoVersioAndKoodiVersio(newKoodistoVersio.getId(),
                    versio.getId());
            koodistoVersioKoodiVersioDAO.remove(koodistoVersioKoodiVersio);

        } else {

            List<KoodistoVersio> koodistoVersios = koodistoVersioDAO.getKoodistoVersiosForKoodiVersio(koodiUri, koodiVersio);
            for (KoodistoVersio kv : koodistoVersios) {
                kv.removeKoodiVersio(koodistoVersioKoodiVersioDAO.findByKoodistoVersioAndKoodiVersio(kv.getId(), versio.getId()));
            }

            Koodi koodi = koodiDAO.readByUri(koodiUri);
            koodi.removeKoodiVersion(versio);

            koodiVersioDAO.remove(versio);
            if (koodi.getKoodiVersios().size() == 0) {
                koodiDAO.remove(koodi);
            } else {
                activateRelationsInLatestKoodiVersio(koodi);
            }

        }

    }

    private void activateRelationsInLatestKoodiVersio(Koodi koodi) {
        KoodiVersio latest = null;
        for (KoodiVersio kv : koodi.getKoodiVersios()) {
            latest = latest == null || latest.getVersio() < kv.getVersio() ? kv : latest;
        }
        setRelationsToPassiveOrActive(latest, false);
    }

    @Override
    public void delete(String koodiUri, Integer koodiVersio) {
        delete(koodiUri, koodiVersio, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KoodiVersioWithKoodistoItem> searchKoodis(SearchKoodisByKoodistoCriteriaType searchCriteria) {

        if (searchCriteria == null) {
            logger.error("No search criteria for koodisto");
            throw new SearchCriteriaEmptyException();
        }

        if (!koodistoDAO.koodistoUriExists(searchCriteria.getKoodistoUri())) {
            logger.error("No koodisto found for URI " + searchCriteria.getKoodistoUri());
            throw new KoodistoNotFoundException();
        }

        if (searchCriteria.getKoodistoVersioSelection() == null) {
            if (searchCriteria.getKoodistoVersio() != null) {
                searchCriteria.setKoodistoVersioSelection(SearchKoodisByKoodistoVersioSelectionType.SPECIFIC);
            } else {
                searchCriteria.setKoodistoVersioSelection(SearchKoodisByKoodistoVersioSelectionType.LATEST);
            }
        } else if (SearchKoodisByKoodistoVersioSelectionType.SPECIFIC.equals(searchCriteria.getKoodistoVersioSelection())
                && searchCriteria.getKoodistoVersio() == null) {
            logger.error("Koodisto version number is empty");
            throw new KoodistoVersionNumberEmptyException();
        }

        List<KoodiVersioWithKoodistoItem> list = koodiVersioDAO.searchKoodis(searchCriteria);

        return list;
    }

    private void checkKoodistoExists(String koodistoUri) {
        if (!koodistoBusinessService.koodistoExists(koodistoUri)) {
            logger.error("No koodisto found with koodisto URI " + koodistoUri);
            throw new KoodistoNotFoundException();
        }
    }

    private void checkKoodistoVersioExists(String koodistoUri, Integer koodistoVersio) {
        if (!koodistoBusinessService.koodistoExists(koodistoUri, koodistoVersio)) {
            logger.error("No koodisto found with koodisto URI " + koodistoUri + " and versio " + koodistoVersio);
            throw new KoodistoNotFoundException();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<KoodiVersioWithKoodistoItem> searchKoodis(SearchKoodisCriteriaType searchCriteria) {
        if (searchCriteria != null) {
            if (searchCriteria.getKoodiVersioSelection() == null) {
                if (searchCriteria.getKoodiVersio() != null) {
                    searchCriteria.setKoodiVersioSelection(SearchKoodisVersioSelectionType.SPECIFIC);
                } else {
                    searchCriteria.setKoodiVersioSelection(SearchKoodisVersioSelectionType.LATEST);
                }
            } else if (SearchKoodisVersioSelectionType.SPECIFIC.equals(searchCriteria.getKoodiVersioSelection()) && searchCriteria.getKoodiVersio() == null) {
                logger.error("Koodi version number is empty");
                throw new KoodiVersionNumberEmptyException();
            }
        }

        List<KoodiVersioWithKoodistoItem> versios = koodiVersioDAO.searchKoodis(searchCriteria);
        if (!versios.isEmpty()) {
            initializeRelations(versios.get(0).getKoodiVersio().getYlakoodis(), true);
            initializeRelations(versios.get(0).getKoodiVersio().getAlakoodis(), false);
        }
        return versios;
    }

    private void initializeRelations(Set<KoodinSuhde> relations, boolean upperRelation) {
        Set<Long> koodiVersioIdList = relations.stream()
                .map(koodinSuhde -> upperRelation ? koodinSuhde.getYlakoodiVersio() : koodinSuhde.getAlakoodiVersio())
                .map(KoodiVersio::getId)
                .collect(Collectors.toSet());
        this.koodiMetadataDAO.initializeByKoodiVersioIds(koodiVersioIdList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KoodiVersioWithKoodistoItem> getKoodisByKoodisto(String koodistoUri, boolean onlyValidKoodis) {
        checkKoodistoExists(koodistoUri);

        SearchKoodisByKoodistoCriteriaType searchData = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(koodistoUri);

        if (onlyValidKoodis) {
            onlyValidKoodis(searchData);
        }

        return searchKoodis(searchData);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KoodiVersioWithKoodistoItem> getKoodisByKoodistoWithKoodiArvo(String koodistoUri, String koodiArvo) {
        checkKoodistoExists(koodistoUri);

        SearchKoodisByKoodistoCriteriaType searchData = KoodiServiceSearchCriteriaBuilder.koodisByArvoAndKoodistoUri(koodiArvo, koodistoUri);

        return searchKoodis(searchData);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KoodiVersioWithKoodistoItem> getKoodisByKoodistoVersioWithKoodiArvo(String koodistoUri, Integer koodistoVersio, String koodiArvo) {
        checkKoodistoVersioExists(koodistoUri, koodistoVersio);

        SearchKoodisByKoodistoCriteriaType searchData = KoodiServiceSearchCriteriaBuilder.koodisByArvoAndKoodistoUriAndKoodistoVersio(koodiArvo, koodistoUri,
                koodistoVersio);

        return searchKoodis(searchData);
    }

    @Override
    @Transactional(readOnly = true)
    public KoodiVersioWithKoodistoItem getKoodiByKoodisto(String koodistoUri, String koodiUri) {
        checkKoodistoExists(koodistoUri);

        SearchKoodisByKoodistoCriteriaType searchData = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(Arrays.asList(koodiUri), koodistoUri);

        List<KoodiVersioWithKoodistoItem> result = searchKoodis(searchData);
        if (result.size() < 1) {
            logger.warn("No koodi found with URI " + koodiUri + " in koodisto with URI " + koodistoUri);
            throw new KoodiNotFoundException();
        }

        return result.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public KoodiVersioWithKoodistoItem getKoodiByKoodistoVersio(String koodistoUri, Integer koodistoVersio, String koodiUri) {
        checkKoodistoVersioExists(koodistoUri, koodistoVersio);

        SearchKoodisByKoodistoCriteriaType searchData = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUriAndKoodistoVersio(Arrays.asList(koodiUri),
                koodistoUri, koodistoVersio);

        List<KoodiVersioWithKoodistoItem> result = searchKoodis(searchData);
        if (result.size() < 1) {
            logger.warn("No koodi found with URI " + koodiUri + " in koodisto with URI " + koodistoUri + ", versio " + koodistoVersio);
            throw new KoodiNotFoundException();
        }

        return result.get(0);
    }

    @Override
    public List<KoodiVersioWithKoodistoItem> listByRelation(String koodiUri, boolean isChild, SuhteenTyyppi suhteenTyyppi) {
        KoodiVersio koodi = getLatestKoodiVersio(koodiUri);

        KoodiUriAndVersioType kv = new KoodiUriAndVersioType();
        kv.setKoodiUri(koodiUri);
        kv.setVersio(koodi.getVersio());
        return listByRelation(kv, suhteenTyyppi, isChild);
    }

    @Override
    public List<KoodiVersioWithKoodistoItem> listByRelation(String koodiUri, Integer koodiVersio, boolean isChild, SuhteenTyyppi suhteenTyyppi) {
        KoodiVersio koodi = getKoodiVersio(koodiUri, koodiVersio);

        KoodiUriAndVersioType kv = new KoodiUriAndVersioType();
        kv.setKoodiUri(koodiUri);
        kv.setVersio(koodi.getVersio());
        return listByRelation(kv, suhteenTyyppi, isChild);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KoodiVersioWithKoodistoItem> getKoodisByKoodistoVersio(String koodistoUri, Integer koodistoVersio, boolean onlyValidKoodis) {
        checkKoodistoVersioExists(koodistoUri, koodistoVersio);

        SearchKoodisByKoodistoCriteriaType searchData = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUriAndKoodistoVersio(koodistoUri, koodistoVersio);

        if (onlyValidKoodis) {
            onlyValidKoodis(searchData);
        }

        return searchKoodis(searchData);
    }

    private void onlyValidKoodis(SearchKoodisByKoodistoCriteriaType searchData) {
        if (searchData.getKoodiSearchCriteria() == null) {
            searchData.setKoodiSearchCriteria(new SearchKoodisCriteriaType());
        }
        searchData.getKoodiSearchCriteria().setValidAt(DateHelper.DateToXmlCal(new Date()));
    }

    @Override
    public boolean hasRelationBetweenCodeElements(KoodiVersio ylaKoodiVersio, final KoodiVersio alaKoodiVersio) {
        return Iterables.tryFind(ylaKoodiVersio.getAlakoodis(), new Predicate<KoodinSuhde>() {

            @Override
            public boolean apply(@Nonnull KoodinSuhde input) {
                return input.getAlakoodiVersio().equals(alaKoodiVersio);
            }

        }).isPresent();
    }

    private boolean koodisHaveSameOrganisaatio(String ylakoodiUri, List<String> alakoodiUris) {
        boolean result = true;
        KoodiVersio ylakoodi = getLatestKoodiVersio(ylakoodiUri);
        String organisaatio1 = ylakoodi.getKoodi().getKoodisto().getOrganisaatioOid();
        ArrayList<String> alreadyChecked = new ArrayList<String>();
        List<KoodiVersioWithKoodistoItem> alakoodiVersios = getLatestKoodiVersios(alakoodiUris.toArray(new String[alakoodiUris.size()]));
        for (KoodiVersioWithKoodistoItem ak : alakoodiVersios) {
            String organisaatio2 = ak.getKoodistoItem().getOrganisaatioOid();
            if (!alreadyChecked.contains(organisaatio2)) {
                authorizer.checkOrganisationAccess(organisaatio1, KoodistoRole.CRUD);
                result = result && StringUtils.equals(organisaatio1, organisaatio2); // false if any organisation mismatches
                alreadyChecked.add(organisaatio2);
            }
        }
        return result;
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
    public boolean isLatestKoodiVersio(String koodiUri, Integer versio) {
        return koodiVersioDAO.isLatestKoodiVersio(koodiUri, versio);
    }

    @Transactional(readOnly = true)
    @Override
    public Koodi getKoodi(String koodiUri) {
        Koodi koodi = koodiDAO.readByUri(koodiUri);
        Hibernate.initialize(koodi);
        for (KoodiVersio kv : koodi.getKoodiVersios()) {
            Hibernate.initialize(kv.getMetadatas());
            for (KoodiMetadata data : kv.getMetadatas()) {
                Hibernate.initialize(data);
            }
            initializeRelations(kv.getAlakoodis(), false);
            initializeRelations(kv.getYlakoodis(), true);
        }
        return koodi;
    }

    @Override
    @Transactional
    public KoodiVersio saveKoodi(ExtendedKoodiDto koodiDTO) {

        String koodiUri = koodiDTO.getKoodiUri();
        KoodiVersio latest = getLatestKoodiVersio(koodiUri);

        Set<KoodinSuhde> existingAlaKoodis = latest.getAlakoodis();
        Set<KoodinSuhde> existingYlaKoodis = latest.getYlakoodis();

        HashSet<String> existingIncludesUris = new HashSet<>();
        HashSet<String> existingLevelsWithChildUris = new HashSet<>();
        HashSet<String> existingLevelsWithParentUris = new HashSet<>();
        HashSet<String> existingWithinUris = new HashSet<>();
        separateKoodiRelationsToUriLists(existingAlaKoodis, existingIncludesUris, existingLevelsWithChildUris, true);
        separateKoodiRelationsToUriLists(existingYlaKoodis, existingWithinUris, existingLevelsWithParentUris, false);

        List<String> removedIncludesUris = filterRemovedRelationUrisToSet(koodiDTO.getIncludesCodeElements(), existingIncludesUris);
        List<String> removedLevelsWithChildUris = filterRemovedRelationUrisToSet(koodiDTO.getLevelsWithCodeElements(), existingLevelsWithChildUris);
        List<String> removedLevelsWithParentUris = filterRemovedRelationUrisToSet(koodiDTO.getLevelsWithCodeElements(), existingLevelsWithParentUris);
        List<String> removedWithinUris = filterRemovedRelationUrisToSet(koodiDTO.getWithinCodeElements(), existingWithinUris);

        HashSet<String> existingLevelsWithUris = new HashSet<>();
        existingLevelsWithUris.addAll(existingLevelsWithChildUris);
        existingLevelsWithUris.addAll(existingLevelsWithParentUris);
        List<String> addedIncludesUris = filterNewRelationUrisToSet(koodiDTO.getIncludesCodeElements(), existingIncludesUris);
        List<String> addedLevelsWithUris = filterNewRelationUrisToSet(koodiDTO.getLevelsWithCodeElements(), existingLevelsWithUris);
        List<String> addedWithinUris = filterNewRelationUrisToSet(koodiDTO.getWithinCodeElements(), existingWithinUris);

        latest = updateKoodi(converter.convertFromDTOToUpdateKoodiDataType(koodiDTO), true).getKoodiVersio();

        if (removedIncludesUris.size() > 0 || addedIncludesUris.size() > 0) {
            koodistoBusinessService.createNewVersion(latest.getKoodi().getKoodisto().getKoodistoUri());
            latest = getLatestKoodiVersio(latest.getKoodi().getKoodiUri());
            flushAfterCreation();
        }

        removeRelation(latest, removedIncludesUris, SuhteenTyyppi.SISALTYY, false);
        addRelation(latest, addedIncludesUris, SuhteenTyyppi.SISALTYY, false);

        removeRelation(latest, removedWithinUris, SuhteenTyyppi.SISALTYY, true);
        addRelation(latest, addedWithinUris, SuhteenTyyppi.SISALTYY, true);

        addRelation(koodiUri, addedLevelsWithUris, SuhteenTyyppi.RINNASTEINEN, false);
        removeRelation(koodiUri, removedLevelsWithChildUris, SuhteenTyyppi.RINNASTEINEN, false);

        List<String> koodiuriAsList = new ArrayList<>();
        koodiuriAsList.add(koodiUri);
        for (String parentUri : removedLevelsWithParentUris) {
            removeRelation(parentUri, koodiuriAsList, SuhteenTyyppi.RINNASTEINEN, false);
        }

        return getLatestKoodiVersio(koodiUri);

    }

    private void separateKoodiRelationsToUriLists(Set<KoodinSuhde> koodiRelations, HashSet<String> sisaltyyUris, HashSet<String> rinnasteinenUris,
            boolean isAlaKoodis) {
        for (KoodinSuhde koodinSuhde : koodiRelations) {
            if (!koodinSuhde.isPassive()) {
                KoodiVersio koodiVersio = isAlaKoodis ? koodinSuhde.getAlakoodiVersio() : koodinSuhde.getYlakoodiVersio();
                if (koodiVersioDAO.isLatestKoodiVersio(koodiVersio.getKoodi().getKoodiUri(), koodiVersio.getVersio())) {
                    if (koodinSuhde.getSuhteenTyyppi().equals(SuhteenTyyppi.SISALTYY)) {
                        sisaltyyUris.add(koodiVersio.getKoodi().getKoodiUri());
                    } else if (koodinSuhde.getSuhteenTyyppi().equals(SuhteenTyyppi.RINNASTEINEN)) {
                        rinnasteinenUris.add(koodiVersio.getKoodi().getKoodiUri());
                    }
                }
            }
        }
    }

    private List<String> filterRemovedRelationUrisToSet(List<RelationCodeElement> newRelations, HashSet<String> existingUris) {
        ArrayList<String> toBeRemoved = new ArrayList<String>();
        for (String existingUri : existingUris) {
            boolean found = false;
            for (RelationCodeElement koodinSuhde : newRelations) {
                if (!koodinSuhde.passive) {
                    found = found || koodinSuhde.codeElementUri.equals(existingUri);
                }
            }
            if (!found) {
                toBeRemoved.add(existingUri);
            }
        }
        return toBeRemoved;
    }

    private List<String> filterNewRelationUrisToSet(List<RelationCodeElement> newRelations, HashSet<String> existingUris) {
        ArrayList<String> toBeAdded = new ArrayList<>();
        for (RelationCodeElement koodinSuhde : newRelations) {
            if (!koodinSuhde.passive && !existingUris.contains(koodinSuhde.codeElementUri)) {
                toBeAdded.add(koodinSuhde.codeElementUri);
            }
        }
        return toBeAdded;
    }

}
