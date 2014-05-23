/**
 *
 */
package fi.vm.sade.koodisto.service.business.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import fi.vm.sade.authentication.business.service.Authorizer;
import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.koodisto.dao.KoodiDAO;
import fi.vm.sade.koodisto.dao.KoodiMetadataDAO;
import fi.vm.sade.koodisto.dao.KoodiVersioDAO;
import fi.vm.sade.koodisto.dao.KoodinSuhdeDAO;
import fi.vm.sade.koodisto.dao.KoodistoDAO;
import fi.vm.sade.koodisto.dao.KoodistoVersioDAO;
import fi.vm.sade.koodisto.dao.KoodistoVersioKoodiVersioDAO;
import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.UriTransliterator;
import fi.vm.sade.koodisto.service.business.exception.KoodiKuvausEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodiLyhytNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodiNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodiNotFoundException;
import fi.vm.sade.koodisto.service.business.exception.KoodiNotInKoodistoException;
import fi.vm.sade.koodisto.service.business.exception.KoodiOptimisticLockingException;
import fi.vm.sade.koodisto.service.business.exception.KoodiUriEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodiValueNotUniqueException;
import fi.vm.sade.koodisto.service.business.exception.KoodiVersioHasRelationsException;
import fi.vm.sade.koodisto.service.business.exception.KoodiVersioNotPassiivinenException;
import fi.vm.sade.koodisto.service.business.exception.KoodiVersionNumberEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodisHaveDifferentOrganizationsException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNotFoundException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoUriEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoVersionNumberEmptyException;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;
import fi.vm.sade.koodisto.service.business.exception.SearchCriteriaEmptyException;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.business.util.KoodistoItem;
import fi.vm.sade.koodisto.service.impl.KoodistoRole;
import fi.vm.sade.koodisto.service.types.CreateKoodiDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoVersioSelectionType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisVersioSelectionType;
import fi.vm.sade.koodisto.service.types.UpdateKoodiDataType;
import fi.vm.sade.koodisto.service.types.UpdateKoodiTilaType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;

/**
 * @author tommiha
 */
@Transactional
@Service("koodiBusinessService")
public class KoodiBusinessServiceImpl implements KoodiBusinessService {

    private static final String ROOT_USER_OID = "1.2.246.562.10.00000000001";
    
    private Logger logger = LoggerFactory.getLogger(getClass());

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
    private KoodistoBusinessService koodistoBusinessService;

    @Autowired
    private Authorizer authorizer;

    @Autowired
    private KoodistoVersioKoodiVersioDAO koodistoVersioKoodiVersioDAO;

    @Autowired
    private UriTransliterator uriTransliterator;

    @Override
    public KoodiVersioWithKoodistoItem createKoodi(String koodistoUri, CreateKoodiDataType createKoodiData) {
        if (createKoodiData == null || StringUtils.isBlank(koodistoUri)) {
            throw new KoodistoUriEmptyException("codes.uri.is.empty");
        }

        checkMetadatas(createKoodiData.getMetadata());

        // new version of Koodisto is created if necessary
        koodistoBusinessService.createNewVersion(koodistoUri);

        KoodistoVersio koodistoVersio = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
        authorizer.checkOrganisationAccess(koodistoVersio.getKoodisto().getOrganisaatioOid(), KoodistoRole.CRUD);

        checkIfCodeElementValueExistsAlready("", createKoodiData.getKoodiArvo(), koodistoVersio.getKoodiVersios());

        Koodi koodi = new Koodi();
        koodi.setKoodisto(koodistoVersio.getKoodisto());
        koodi.setKoodiUri(uriTransliterator.generateKoodiUriByKoodistoUriAndKoodiArvo(koodistoUri, createKoodiData.getKoodiArvo()));
        koodi = koodiDAO.insert(koodi);

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

        koodiVersio = koodiVersioDAO.insert(koodiVersio);

        KoodistoVersioKoodiVersio koodistoVersioRelation = new KoodistoVersioKoodiVersio();
        koodistoVersioRelation.setKoodistoVersio(koodistoVersio);
        koodistoVersioRelation.setKoodiVersio(koodiVersio);
        koodistoVersioKoodiVersioDAO.insert(koodistoVersioRelation);

        Set<Integer> versio = new HashSet<Integer>();
        versio.add(koodistoVersio.getVersio());

        return new KoodiVersioWithKoodistoItem(koodiVersio, new KoodistoItem(koodistoUri, versio));
    }

    private void checkIfCodeElementValueExistsAlready(String koodiUri,
                                                      String koodiArvo,
                                                      Set<KoodistoVersioKoodiVersio> koodiVersios) {
        for (KoodistoVersioKoodiVersio koodiVersio : koodiVersios) {
            if (!koodiUri.equals(koodiVersio.getKoodiVersio().getKoodi().getKoodiUri()) &&
                    koodiArvo.equals(koodiVersio.getKoodiVersio().getKoodiarvo())) {
                throw new KoodiValueNotUniqueException("codeelement.value.not.unique");
            }
        }
    }

    private KoodiVersioWithKoodistoItem getLatestKoodiVersioWithKoodistoVersioItems(String koodiUri) {
        SearchKoodisCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(koodiUri);
        List<KoodiVersioWithKoodistoItem> result = koodiVersioDAO.searchKoodis(searchCriteria);
        if (result.size() != 1) {
            throw new KoodiNotFoundException("codeelement.uri.not.found");
        }

        return result.get(0);
    }

    public KoodiVersio getLatestKoodiVersio(String koodiUri) {
        if(StringUtils.isBlank(koodiUri)) return null;
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
            throw new KoodiNotFoundException("codeelement.uri.not.found");
        }

        return result.get(0);
    }

    private KoodiVersio getKoodiVersio(String koodiUri, Integer koodiVersio) {
        return getKoodiVersioWithKoodistoVersioItems(koodiUri, koodiVersio).getKoodiVersio();
    }

    private void checkRequiredMetadataFields(Collection<KoodiMetadataType> metadatas) {
        for (KoodiMetadataType md : metadatas) {
            if (StringUtils.isBlank(md.getNimi())) {
                throw new KoodiNimiEmptyException("No koodi nimi defined for language " + md.getKieli().name());
            } else if (StringUtils.isBlank(md.getKuvaus())) {
                throw new KoodiKuvausEmptyException("No koodi kuvaus defined for language " + md.getKieli().name());
            } else if (StringUtils.isBlank(md.getLyhytNimi())) {
                throw new KoodiLyhytNimiEmptyException("No koodi lyhyt nimi defined for language " + md.getKieli().name());
            }
        }
    }

    private void checkMetadatas(Collection<KoodiMetadataType> metadatas) {
        if (metadatas == null || metadatas.isEmpty()) {
            throw new MetadataEmptyException("codeelement.metadata.is.empty");
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
            throw new KoodiUriEmptyException("codeelement.uri.is.empty");
        }
        KoodiVersioWithKoodistoItem latest = getLatestKoodiVersioWithKoodistoVersioItems(updateKoodiData.getKoodiUri());

        KoodiVersio latestKoodiVersion = latest.getKoodiVersio();
        if (!skipAlreadyUpdatedVerification
                && (latestKoodiVersion.getVersio() != updateKoodiData.getVersio() || (latestKoodiVersion.getVersio() == updateKoodiData.getVersio() && latestKoodiVersion
                        .getVersion() != updateKoodiData.getLockingVersion()))) {
            throw new KoodiOptimisticLockingException("codeelement.already.modified");
        }

        KoodistoVersio latestKoodisto = koodistoBusinessService.getLatestKoodistoVersio(latest.getKoodiVersio().getKoodi().getKoodisto().getKoodistoUri());

        if (!latest.getKoodistoItem().getVersios().contains(latestKoodisto.getVersio())) {
            throw new KoodiNotInKoodistoException("codeelement.is.not.part.of.codes");
        }

        checkMetadatas(updateKoodiData.getMetadata());

        checkIfCodeElementValueExistsAlready(updateKoodiData.getKoodiUri(), updateKoodiData.getKoodiArvo(), latestKoodisto.getKoodiVersios());

        KoodiVersio newVersion = createNewVersionIfNeeded(latest.getKoodiVersio(), updateKoodiData);

        return getKoodiVersioWithKoodistoVersioItems(newVersion.getKoodi().getKoodiUri(), newVersion.getVersio());
    }

    @Override
    public void massCreate(String koodistoUri, List<UpdateKoodiDataType> koodiList) {
        if (StringUtils.isBlank(koodistoUri)) {
            throw new KoodistoUriEmptyException("Koodisto URI is empty");
        }

        KoodistoVersio koodisto = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
        for (UpdateKoodiDataType updateData : koodiList) {
            if (StringUtils.isNotBlank(updateData.getKoodiUri())) {
                KoodiVersio latest = null;
                try {
                    latest = getLatestKoodiVersio(updateData.getKoodiUri());
                } catch (KoodiNotFoundException e) {
                    logger.warn("Koodia ei löytynyt. Luodaan uusi " + updateData.getKoodiUri());
                }

                if (latest != null && latest.getKoodi().getKoodisto().getKoodistoUri().equals(koodisto.getKoodisto().getKoodistoUri())) {
                    KoodiVersioWithKoodistoItem updated = updateKoodi(updateData, true);
                    KoodistoVersioKoodiVersio result = koodistoVersioKoodiVersioDAO.findByKoodistoVersioAndKoodiVersio(koodisto.getId(), updated.getKoodiVersio()
                            .getId());

                    if (result == null) {
                        KoodistoVersioKoodiVersio newRelationEntry = new KoodistoVersioKoodiVersio();
                        newRelationEntry.setKoodistoVersio(koodisto);
                        newRelationEntry.setKoodiVersio(updated.getKoodiVersio());
                        koodistoVersioKoodiVersioDAO.insert(newRelationEntry);
                    }

                } else {
                    CreateKoodiDataType createData = new CreateKoodiDataType();
                    EntityUtils.copyFields(updateData, createData);
                    createKoodi(koodistoUri, createData);
                }
            }
        }
        koodisto.setPaivitysPvm(new Date());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KoodiVersioWithKoodistoItem> listByRelation(KoodiUriAndVersioType koodi, SuhteenTyyppi suhdeTyyppi, Boolean isChild) {
        Set<KoodiVersioWithKoodistoItem> koodis = new HashSet<KoodiVersioWithKoodistoItem>();
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

        return new ArrayList<KoodiVersioWithKoodistoItem>(koodis);
    }

    @Override
    public void addRelation(String ylaKoodi, String alaKoodi, SuhteenTyyppi suhteenTyyppi) {
        KoodiVersio yla = getLatestKoodiVersio(ylaKoodi);
        KoodiVersioWithKoodistoItem ala = getLatestKoodiVersioWithKoodistoVersioItems(alaKoodi);

        addRelation(yla, suhteenTyyppi, ala);
    }

    private void addRelation(KoodiVersio ylakoodi, SuhteenTyyppi suhteenTyyppi, KoodiVersioWithKoodistoItem... alakoodis) {
        if(suhteenTyyppi == SuhteenTyyppi.SISALTYY && this.haveDifferentOrganisations(ylakoodi, alakoodis) && !currentUserIsRootUser()) {
            throw new KoodisHaveDifferentOrganizationsException("codeelements.have.different.organizations");
        }

        
        KoodiVersio latestYlakoodi = ylakoodi;

        if (SuhteenTyyppi.SISALTYY.equals(suhteenTyyppi)) {
            koodistoBusinessService.createNewVersion(ylakoodi.getKoodi().getKoodisto().getKoodistoUri());

            latestYlakoodi = createNewVersion(getLatestKoodiVersio(ylakoodi.getKoodi().getKoodiUri()));
        }

        for (KoodiVersioWithKoodistoItem alakoodi : alakoodis) {
            KoodinSuhde koodinSuhde = new KoodinSuhde();
            koodinSuhde.setSuhteenTyyppi(suhteenTyyppi);
            koodinSuhde.setYlakoodiVersio(latestYlakoodi);
            koodinSuhde.setAlakoodiVersio(alakoodi.getKoodiVersio());
            koodinSuhde.setVersio(1);
            koodinSuhdeDAO.insert(koodinSuhde);
        }
    }

    @Override
    public void addRelation(String ylaKoodi, List<String> alaKoodis, SuhteenTyyppi suhteenTyyppi) {
        if (alaKoodis == null || alaKoodis.isEmpty()) {
            return;
        }

        KoodiVersio ylakoodiVersio = getLatestKoodiVersio(ylaKoodi);
        List<KoodiVersioWithKoodistoItem> alakoodiVersios = getLatestKoodiVersios(alaKoodis.toArray(new String[alaKoodis.size()]));

        addRelation(ylakoodiVersio, suhteenTyyppi, alakoodiVersios.toArray(new KoodiVersioWithKoodistoItem[alakoodiVersios.size()]));
    }

    private List<KoodinSuhde> getRelations(String ylakoodiUri, List<String> alakoodiUris, SuhteenTyyppi st) {
        KoodiVersio ylakoodi = getLatestKoodiVersio(ylakoodiUri);
        KoodiUriAndVersioType yk = new KoodiUriAndVersioType();
        yk.setKoodiUri(ylakoodi.getKoodi().getKoodiUri());
        yk.setVersio(ylakoodi.getVersio());

        List<KoodiUriAndVersioType> aks = new ArrayList<KoodiUriAndVersioType>();
        for (KoodiVersioWithKoodistoItem ak : getLatestKoodiVersios(alakoodiUris.toArray(new String[alakoodiUris.size()]))) {
            KoodiUriAndVersioType a = new KoodiUriAndVersioType();
            a.setKoodiUri(ak.getKoodiVersio().getKoodi().getKoodiUri());
            a.setVersio(ak.getKoodiVersio().getVersio());

            aks.add(a);
        }

        return koodinSuhdeDAO.getRelations(yk, aks, st);
    }

    @Override
    public void removeRelation(String ylakoodiUri, List<String> alakoodiUris, SuhteenTyyppi st) {
        if (alakoodiUris == null || alakoodiUris.isEmpty() || getRelations(ylakoodiUri, alakoodiUris, st).size() == 0) {
            return;
        }

        KoodiVersio ylakoodi = getLatestKoodiVersio(ylakoodiUri);
        if (SuhteenTyyppi.SISALTYY.equals(st)) {
            koodistoBusinessService.createNewVersion(ylakoodi.getKoodi().getKoodisto().getKoodistoUri());
            createNewVersion(getLatestKoodiVersio(ylakoodiUri));
        }

        List<KoodinSuhde> relations = getRelations(ylakoodiUri, alakoodiUris, st);

        for (KoodinSuhde k : relations) {
            koodinSuhdeDAO.remove(k);
        }
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

        List<KoodiMetadata> latestMetadatas = new ArrayList<KoodiMetadata>(latest.getMetadatas());

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

        // Set update date
        latest.setPaivitysPvm(new Date());
        return latest;
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

    private KoodiVersio createNewVersion(KoodiVersio latest) {
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
        return createNewVersion(latest, newVersio);
    }

    private KoodiVersio createNewVersion(KoodiVersio base, KoodiVersio input) {
        input.setId(null);
        input.setVersio(base.getVersio() + 1);
        input.setTila(Tila.LUONNOS);

        input.setKoodi(base.getKoodi());
        KoodiVersio inserted = koodiVersioDAO.insert(input);
        copyRelations(base, inserted);

        return inserted;
    }

    private void copyRelations(KoodiVersio latest, KoodiVersio newVersio) {
        // copy relations
        Iterator<KoodinSuhde> ylakooodiIterator = latest.getYlakoodis().iterator();
        while (ylakooodiIterator.hasNext()) {
            KoodinSuhde ks = ylakooodiIterator.next();
            KoodinSuhde newSuhde = new KoodinSuhde();
            newSuhde.setVersio(ks.getVersio() + 1);
            newSuhde.setAlakoodiVersio(newVersio);
            newSuhde.setYlakoodiVersio(ks.getYlakoodiVersio());
            newSuhde.setSuhteenTyyppi(ks.getSuhteenTyyppi());
            newVersio.addYlakoodi(newSuhde);
        }

        Iterator<KoodinSuhde> alakoodiIterator = latest.getAlakoodis().iterator();

        while (alakoodiIterator.hasNext()) {
            KoodinSuhde ks = alakoodiIterator.next();
            KoodinSuhde newSuhde = new KoodinSuhde();
            newSuhde.setVersio(ks.getVersio() + 1);
            newSuhde.setAlakoodiVersio(ks.getAlakoodiVersio());
            newSuhde.setYlakoodiVersio(newVersio);
            newSuhde.setSuhteenTyyppi(ks.getSuhteenTyyppi());
            newVersio.addAlakoodi(newSuhde);
        }
    }

    @Override
    public KoodiVersio createNewVersion(String koodiUri) {
        return createNewVersion(getLatestKoodiVersio(koodiUri));
    }

    private void setKoodiTila(KoodiVersio latest, TilaType tila) {
        if (Tila.LUONNOS.equals(latest.getTila()) && TilaType.HYVAKSYTTY.equals(tila)) {

            KoodiVersio previousVersion = koodiVersioDAO.getPreviousKoodiVersio(latest.getKoodi().getKoodiUri(), latest.getVersio());
            if (previousVersion != null) {
            	previousVersion.setVoimassaLoppuPvm(getValidEndDateForKoodiVersio(previousVersion, latest));            
            }
            latest.setTila(Tila.valueOf(tila.name()));
        }
    }
    
    private Date getValidEndDateForKoodiVersio(KoodiVersio previous, KoodiVersio latest) {
    	final Date previousStartDate = previous.getVoimassaAlkuPvm();
    	final Date latestStartDate = latest.getVoimassaAlkuPvm();
    	if (latestStartDate.after(previousStartDate) && latestStartDate.after(new Date())) {
    		return latestStartDate;
    	}
    	return previousStartDate.after(new Date()) ? previousStartDate: new Date();
    }

    @Override
    public void setKoodiTila(String koodiUri, TilaType tila) {
        KoodiVersio latest = getLatestKoodiVersio(koodiUri);
        setKoodiTila(latest, tila);
    }

    @Override
    public void delete(String koodiUri, Integer koodiVersio, boolean skipPassiivinenCheck) {
        KoodiVersioWithKoodistoItem kvkoodisto = getKoodiVersioWithKoodistoVersioItems(koodiUri, koodiVersio);
        KoodiVersio versio = kvkoodisto.getKoodiVersio();
        if (!skipPassiivinenCheck && !Tila.PASSIIVINEN.equals(versio.getTila())) {
            throw new KoodiVersioNotPassiivinenException("Cannot delete koodi version. Tila must be " + Tila.PASSIIVINEN.name() + ".");
        }
        List<KoodiVersioWithKoodistoItem> codes = listByRelation(koodiUri, koodiVersio, false, SuhteenTyyppi.RINNASTEINEN);
        if (codes == null || codes.size() == 0) {
            codes = listByRelation(koodiUri, koodiVersio, false, SuhteenTyyppi.SISALTYY);
        }

        if (codes == null || codes.size() == 0) {
            codes = listByRelation(koodiUri, koodiVersio, true, SuhteenTyyppi.SISALTYY);
        }

        if (codes != null && codes.size() > 0) {
            throw new KoodiVersioHasRelationsException("Cannot delete koodi version. Relations must be delete.");

        }
        KoodistoVersio latestKoodistoVersio = koodistoBusinessService.getLatestKoodistoVersio(kvkoodisto.getKoodistoItem().getKoodistoUri());

        if (kvkoodisto.getKoodistoItem().getVersios().contains(latestKoodistoVersio.getVersio()) && latestKoodistoVersio.getTila().equals(Tila.HYVAKSYTTY)) {

            KoodistoVersio newKoodistoVersio = koodistoBusinessService.createNewVersion(latestKoodistoVersio.getKoodisto().getKoodistoUri());
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
            }

        }

    }

    @Override
    public void delete(String koodiUri, Integer koodiVersio) {
        delete(koodiUri, koodiVersio, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KoodiVersioWithKoodistoItem> searchKoodis(SearchKoodisByKoodistoCriteriaType searchCriteria) {

        if (searchCriteria == null) {
            throw new SearchCriteriaEmptyException("No search criteria for koodisto");
        }

        if (!koodistoDAO.koodistoUriExists(searchCriteria.getKoodistoUri())) {
            throw new KoodistoNotFoundException("No koodisto found for URI " + searchCriteria.getKoodistoUri());
        }

        if (searchCriteria.getKoodistoVersioSelection() == null) {
            if (searchCriteria.getKoodistoVersio() != null) {
                searchCriteria.setKoodistoVersioSelection(SearchKoodisByKoodistoVersioSelectionType.SPECIFIC);
            } else {
                searchCriteria.setKoodistoVersioSelection(SearchKoodisByKoodistoVersioSelectionType.LATEST);
            }
        } else if (SearchKoodisByKoodistoVersioSelectionType.SPECIFIC.equals(searchCriteria.getKoodistoVersioSelection())
                && searchCriteria.getKoodistoVersio() == null) {
            throw new KoodistoVersionNumberEmptyException("Koodisto version number is empty");
        }

        List<KoodiVersioWithKoodistoItem> list = koodiVersioDAO.searchKoodis(searchCriteria);

        return list;
    }

    private void checkKoodistoExists(String koodistoUri) {
        if (!koodistoBusinessService.koodistoExists(koodistoUri)) {
            throw new KoodistoNotFoundException("No koodisto found with koodisto URI " + koodistoUri);
        }
    }

    private void checkKoodistoVersioExists(String koodistoUri, Integer koodistoVersio) {
        if (!koodistoBusinessService.koodistoExists(koodistoUri, koodistoVersio)) {
            throw new KoodistoNotFoundException("No koodisto found with koodisto URI " + koodistoUri + " and versio " + koodistoVersio);
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
                throw new KoodiVersionNumberEmptyException("Koodi version number is empty");
            }
        }

        List<KoodiVersioWithKoodistoItem> versios = koodiVersioDAO.searchKoodis(searchCriteria);
        if (!versios.isEmpty()) {
            Iterator<KoodinSuhde> itr = versios.get(0).getKoodiVersio().getYlakoodis().iterator();
            while(itr.hasNext()) {
                KoodinSuhde koodinSuhde = itr.next();
                Hibernate.initialize(koodinSuhde.getYlakoodiVersio().getMetadatas());
                Hibernate.initialize(koodinSuhde.getYlakoodiVersio().getKoodi());
            }
            itr = versios.get(0).getKoodiVersio().getAlakoodis().iterator();
            while(itr.hasNext()) {
                KoodinSuhde koodinSuhde = (KoodinSuhde)itr.next();
                Hibernate.initialize(koodinSuhde.getAlakoodiVersio().getMetadatas());
                Hibernate.initialize(koodinSuhde.getAlakoodiVersio().getKoodi());
            }
        }
        return versios;
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
            throw new KoodiNotFoundException("No koodi found with URI " + koodiUri + " in koodisto with URI " + koodistoUri);
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
            throw new KoodiNotFoundException("No koodi found with URI " + koodiUri + " in koodisto with URI " + koodistoUri + ", versio " + koodistoVersio);
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
			public boolean apply(KoodinSuhde input) {
				return input.getAlakoodiVersio().equals(alaKoodiVersio);
			}
    		
    	}).isPresent();
    }

    private boolean haveDifferentOrganisations(KoodiVersio ylakoodi, KoodiVersioWithKoodistoItem[] alakoodis) {
        if(alakoodis.length==0) return true;
        String organisaatio1 = ylakoodi.getKoodi().getKoodisto().getOrganisaatioOid();
        String organisaatio2 = alakoodis[0].getKoodiVersio().getKoodi().getKoodisto().getOrganisaatioOid();
        return !StringUtils.equals(organisaatio1, organisaatio2);
    }
    


    // Check if logged in user is not a root user
    private boolean currentUserIsRootUser() {
        try {
            String userOid = SecurityContextHolder.getContext().getAuthentication().getName();
            return userOid.equals(ROOT_USER_OID);
        } catch (NullPointerException e) { // Authentication is not available
            return false;
        }
    }

}
