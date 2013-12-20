/**
 * 
 */
package fi.vm.sade.koodisto.ui.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.vm.sade.koodisto.exception.KoodistoNotFoundException;
import fi.vm.sade.koodisto.service.KoodistoAdminService;
import fi.vm.sade.koodisto.service.KoodistoService;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.UpdateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.common.KoodistoRyhmaListType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;

/**
 * @author tommiha
 * 
 */
@Service
public class KoodistoUiServiceImpl implements KoodistoUiService {

    @Autowired
    private KoodistoAdminService koodistoAdminService;

    @Autowired
    private KoodistoService koodistoService;

    @Override
    public List<KoodistoRyhmaListType> listKoodistoRyhmas() {
        List<KoodistoRyhmaListType> koodistoRyhmas = koodistoService.listAllKoodistoRyhmas();
        if (koodistoRyhmas == null) {
            return new ArrayList<KoodistoRyhmaListType>();
        } else {
            return koodistoRyhmas;
        }
    }

    @Override
    public KoodistoType getKoodistoByUri(String koodistoUri) {
        SearchKoodistosCriteriaType searchCriteria = KoodistoServiceSearchCriteriaBuilder.latestKoodistoByUri(koodistoUri);

        List<KoodistoType> result = koodistoService.searchKoodistos(searchCriteria);
        if (result.size() == 0) {
            throw new KoodistoNotFoundException("Koodisto (uri: " + koodistoUri + ") does not exist");
        }

        return result.get(0);
    }

    @Override
    public KoodistoType getKoodistoByUriIfAvailable(String koodistoUri) {
        SearchKoodistosCriteriaType searchCriteria = KoodistoServiceSearchCriteriaBuilder.latestKoodistoByUri(koodistoUri);

        List<KoodistoType> result = koodistoService.searchKoodistos(searchCriteria);

        KoodistoType koodistoType = null;

        if (result.size() > 0) {
          koodistoType = result.get(0);
        }

        return koodistoType;
    }

    @Override
    public KoodistoType update(KoodistoType koodistoDTO) {

        UpdateKoodistoDataType updateData = new UpdateKoodistoDataType();
        KoodistoHelper.copyFields(koodistoDTO, updateData);

        return koodistoAdminService.updateKoodisto(updateData);
    }

    @Override
    public KoodistoType create(KoodistoType koodistoDTO, KoodistoRyhmaListType selectedKoodistoRyhma) {
        List<String> ryhmaUris = new ArrayList<String>();
        ryhmaUris.add(selectedKoodistoRyhma.getKoodistoRyhmaUri());

        CreateKoodistoDataType createKoodistoData = new CreateKoodistoDataType();
        KoodistoHelper.copyFields(koodistoDTO, createKoodistoData);

        return koodistoAdminService.createKoodisto(ryhmaUris, createKoodistoData);
    }

    @Override
    public KoodistoType getKoodistoByUriAndVersion(String koodistoUri, Integer version) {
        SearchKoodistosCriteriaType searchCriteria = KoodistoServiceSearchCriteriaBuilder.koodistoByUriAndVersio(koodistoUri, version);

        List<KoodistoType> result = koodistoService.searchKoodistos(searchCriteria);
        if (result.size() == 0) {
            throw new KoodistoNotFoundException("Koodisto (uri: " + koodistoUri + ", version: " + version + ") does not exist");
        }
        return result.get(0);
    }

    @Override
    public void delete(KoodistoType koodistoDTO) {
        koodistoAdminService.deleteKoodistoVersion(koodistoDTO.getKoodistoUri(), koodistoDTO.getVersio());
    }

    @Override
    public List<KoodistoType> getKoodistosByUris(Set<String> koodistoUris) {
        SearchKoodistosCriteriaType searchCriteria = KoodistoServiceSearchCriteriaBuilder.latestKoodistosByUri(koodistoUris.toArray(new String[koodistoUris
                .size()]));

        return koodistoService.searchKoodistos(searchCriteria);
    }

    @Override
    public Map<String, KoodistoType> getKoodistosByUrisMap(Set<String> koodistoUris) {
        List<KoodistoType> koodistosByUris = getKoodistosByUris(koodistoUris);

        Map<String, KoodistoType> map = new HashMap<String, KoodistoType>();
        for (KoodistoType k : koodistosByUris) {
            map.put(k.getKoodistoUri(), k);
        }

        return map;
    }
}
