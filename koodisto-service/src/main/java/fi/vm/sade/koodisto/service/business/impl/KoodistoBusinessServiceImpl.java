/**
 *
 */
package fi.vm.sade.koodisto.service.business.impl;

import fi.vm.sade.authentication.business.service.Authorizer;
import fi.vm.sade.koodisto.dao.*;
import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.UriTransliterator;
import fi.vm.sade.koodisto.service.business.exception.*;
import fi.vm.sade.koodisto.service.impl.KoodistoRole;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosVersioSelectionType;
import fi.vm.sade.koodisto.service.types.UpdateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodistoUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author tommiha
 */
@Transactional
@Service("koodistoBusinessService")
public class KoodistoBusinessServiceImpl implements KoodistoBusinessService {

    private Logger logger = LoggerFactory.getLogger(getClass());

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

    private List<KoodistonSuhde> getRelations(String ylakoodistoUri, List<String> alakoodistoUris, SuhteenTyyppi st) {
        KoodistoVersio ylakoodisto = getLatestKoodistoVersio(ylakoodistoUri);
        KoodistoUriAndVersioType yk = new KoodistoUriAndVersioType();
        yk.setKoodistoUri(ylakoodisto.getKoodisto().getKoodistoUri());
        yk.setVersio(ylakoodisto.getVersio());

        List<KoodistoUriAndVersioType> aks = new ArrayList<KoodistoUriAndVersioType>();
        for (KoodistoVersio ak : getLatestKoodistoVersios(alakoodistoUris.toArray(new String[alakoodistoUris.size()]))) {
            KoodistoUriAndVersioType a = new KoodistoUriAndVersioType();
            a.setKoodistoUri(ak.getKoodisto().getKoodistoUri());
            a.setVersio(ak.getVersio());

            aks.add(a);
        }

        return koodistonSuhdeDAO.getRelations(yk, aks, st);
    }

    @Override
    public void removeRelation(String ylakoodistoUri, List<String> alakoodistoUris, SuhteenTyyppi st) {
        if (alakoodistoUris == null || alakoodistoUris.isEmpty() || getRelations(ylakoodistoUri, alakoodistoUris, st).size() == 0) {
            return;
        }

        List<KoodistonSuhde> relations = getRelations(ylakoodistoUri, alakoodistoUris, st);

        for (KoodistonSuhde k : relations) {
            koodistonSuhdeDAO.remove(k);
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
        authorizer.checkOrganisationAccess(latest.getKoodisto().getOrganisaatioOid(),
                KoodistoRole.CRUD, KoodistoRole.UPDATE);
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
                if (koodistoRyhma.getKoodistoRyhmaUri().indexOf("kaikki") == -1 && !koodistoRyhma.getKoodistoRyhmaUri().equals(updateKoodistoData.getCodesGroupUri())) {
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
        while(itr.hasNext()) {
            KoodistoVersio koodistoVersio = (KoodistoVersio)itr.next();
            Hibernate.initialize(koodistoVersio);
            Iterator<KoodistoMetadata> itr2 = koodistoVersio.getMetadatas().iterator();
            while(itr2.hasNext()) {
                KoodistoMetadata koodistoMetadata = (KoodistoMetadata)itr2.next();
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
		for(KoodistonSuhde koodistonSuhde : koodistoVersio.getYlakoodistos()) {
            Hibernate.initialize(koodistonSuhde.getYlakoodistoVersio().getMetadatas());
            Hibernate.initialize(koodistonSuhde.getYlakoodistoVersio().getKoodisto());
        }
        for(KoodistonSuhde koodistonSuhde : koodistoVersio.getAlakoodistos()) {
            Hibernate.initialize(koodistonSuhde.getAlakoodistoVersio().getMetadatas());
            Hibernate.initialize(koodistonSuhde.getAlakoodistoVersio().getKoodisto());
        }
        for(KoodistoRyhma ryhma : koodistoVersio.getKoodisto().getKoodistoRyhmas()) {
            Hibernate.initialize(ryhma);
        }
        for(KoodistoVersio versio : koodistoVersio.getKoodisto().getKoodistoVersios()) {
            Hibernate.initialize(versio);
        }
	}

    private List<KoodistoVersio> getLatestKoodistoVersios(String... koodistoUris) {
        SearchKoodistosCriteriaType searchCriteria = KoodistoServiceSearchCriteriaBuilder.latestKoodistosByUri(koodistoUris);
        List<KoodistoVersio> result = koodistoVersioDAO.searchKoodistos(searchCriteria);
        if (result.size() != 1) {
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
            koodistoVersioKoodiVersioDAO.insert(newRelationEntry);
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

            for (KoodiVersio k : koodis) {
                koodiBusinessService.setKoodiTila(k.getKoodi().getKoodiUri(), TilaType.HYVAKSYTTY);
            }

            KoodistoVersio previousVersion = koodistoVersioDAO.getPreviousKoodistoVersio(latest.getKoodisto().getKoodistoUri(), latest.getVersio());
            if (previousVersion != null) {
                previousVersion.setVoimassaLoppuPvm(new Date());
            }
        }

        // Update the version itself
        EntityUtils.copyFields(updateKoodistoData, latest);

        // Set start date to current date
        latest.setVoimassaAlkuPvm(new Date());

        // Set update date
        latest.setPaivitysPvm(new Date());

        return latest;
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
}
