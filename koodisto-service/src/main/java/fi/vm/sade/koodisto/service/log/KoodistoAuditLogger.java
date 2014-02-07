package fi.vm.sade.koodisto.service.log;

import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.types.CreateKoodiDataType;
import fi.vm.sade.koodisto.service.types.UpdateKoodiDataType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.log.client.Logger;
import fi.vm.sade.log.client.LoggerHelper;
import fi.vm.sade.log.model.SimpleBeanSerializer;
import fi.vm.sade.log.model.Tapahtuma;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

/**
 * Created by wuoti on 30.1.2014.
 */
@Component
public class KoodistoAuditLogger {

    public static final String SYSTEM = "koodisto-service";

    public static final String TARGET_TYPE_KOODISTO = "Koodisto";
    public static final String TARGET_TYPE_KOODI = "Koodi";

    @Autowired
    private Logger logger;


    private void init() {
        LoggerHelper.init(logger);
    }

    private static class KeyValue {
        private KeyValue(String key, Serializable value) {
            this.key = key;
            this.value = value;
        }

        public final String key;
        public final Serializable value;
    }

    private KeyValue keyValue(String key, Serializable value) {
        return new KeyValue(key, value);
    }

    private Map<String, Serializable> createMap(KeyValue... entries) {
        Map<String, Serializable> map = new HashMap<String, Serializable>();
        for (KeyValue e : entries) {
            map.put(e.key, e.value);
        }

        return map;
    }

    public void logMassCreate(String koodistoUri, List<UpdateKoodiDataType> koodiList) {
        init();

        int newSeq = 1;

        Map<String, Serializable> tapahtumaValues = new HashMap<String, Serializable>();
        for (UpdateKoodiDataType up : koodiList) {
            String key = null;

            if (StringUtils.isBlank(up.getKoodiUri())) {
                key = "new koodi " + newSeq;
                ++newSeq;
            } else {
                key = "koodi URI: " + up.getKoodiUri();
            }


            tapahtumaValues.put(key, (Serializable) serializeUpdatKoodiDataType(up));
        }
        logAuditTapahtuma(createTapahtuma(koodistoUri, TARGET_TYPE_KOODISTO, "massCreate", tapahtumaValues));
    }


    public void logUpdateKoodi(UpdateKoodiDataType updateKoodiData) {
        init();
        logAuditTapahtuma(createTapahtuma(updateKoodiData.getKoodiUri(), TARGET_TYPE_KOODI, "updateKoodi",
                serializeUpdatKoodiDataType(updateKoodiData)));
    }

    public void logDeleteKoodiVersion(String koodiUri, int koodiVersio) {
        init();
        logAuditTapahtuma(createTapahtuma(koodiUri, TARGET_TYPE_KOODI, "deleteKoodiVersion",
                createMap(keyValue("koodiUri", koodiUri), keyValue("koodiVersio", koodiVersio))));
    }

    public void logAddRelation(String ylaKoodi, String alaKoodi, SuhteenTyyppiType suhteenTyyppi) {
        init();
        logAuditTapahtuma(createTapahtuma(ylaKoodi, TARGET_TYPE_KOODI, "addRelation",
                createMap(
                        keyValue("ylakoodi", ylaKoodi),
                        keyValue("alakoodi", alaKoodi),
                        keyValue("suhteenTyyppi", suhteenTyyppi.name()))));
    }


    public void logAddRelationByAlakoodi(String ylaKoodi, List<String> alaKoodis, SuhteenTyyppiType suhteenTyyppi) {
        init();
        logAuditTapahtuma(createTapahtuma(ylaKoodi, TARGET_TYPE_KOODI, "addRelationByAlakoodi",
                createMap(
                        keyValue("ylakoodi", ylaKoodi),
                        keyValue("alakoodis", (Serializable) alaKoodis),
                        keyValue("suhteenTyyppi", suhteenTyyppi.name()))));
    }


    public void logRemoveRelationByAlakoodi(String ylaKoodi, List<String> alaKoodis, SuhteenTyyppi st) {
        init();
        logAuditTapahtuma(createTapahtuma(ylaKoodi, TARGET_TYPE_KOODI, "removeRelationByAlakoodi",
                createMap(
                        keyValue("ylakoodi", ylaKoodi),
                        keyValue("alakoodis", (Serializable) alaKoodis),
                        keyValue("suhteenTyyppi", st.name()))));
    }


    public void logCreateKoodi(String koodistoUri, CreateKoodiDataType createKoodiData) {
        init();
    }

    private Tapahtuma createTapahtuma(String target, String targetType, String type, Map<String, Serializable> values) {
        return Tapahtuma.createTapahtuma(SYSTEM, target, targetType, new Date(), type, getTekija(), null, values);
    }

    private Map<String, Serializable> serializeUpdatKoodiDataType(UpdateKoodiDataType up) {
        Map<String, Serializable> values = new HashMap<String, Serializable>(SimpleBeanSerializer.getBeanAsMap(up));
        List<Map<String, String>> metadatas = new ArrayList<Map<String, String>>();

        for (KoodiMetadataType meta : up.getMetadata()) {
            metadatas.add(SimpleBeanSerializer.getBeanAsMap(meta));
        }

        values.put("metadatas", (ArrayList<Map<String, String>>) metadatas);

        return values;
    }


    private void logAuditTapahtuma(Tapahtuma tapahtuma) {
        // Get the (possible) composite log event that can contain "sub" events and update it.
        Tapahtuma rootTapahtuma = LoggerHelper.getAuditRootTapahtuma();
        rootTapahtuma.setHost(tapahtuma.getHost());
        rootTapahtuma.setSystem(tapahtuma.getSystem());
        rootTapahtuma.setTarget(tapahtuma.getTarget());
        rootTapahtuma.setTargetType(tapahtuma.getTargetType());
        rootTapahtuma.setTimestamp(tapahtuma.getTimestamp());
        rootTapahtuma.setType(tapahtuma.getType());
        rootTapahtuma.setUser(tapahtuma.getUser());
        rootTapahtuma.setUserActsForUser(tapahtuma.getUserActsForUser());

        // Log the root event
        LoggerHelper.log();
    }

    private String getTekija() {
        String tekija = null;
        if (SecurityContextHolder.getContext() != null
                && SecurityContextHolder.getContext().getAuthentication() != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (StringUtils.isNotBlank(authentication.getName())) {
                tekija = authentication.getName();
            } else if (authentication.getPrincipal() != null && StringUtils.isNotBlank(authentication.getPrincipal().toString())) {
                tekija = authentication.getPrincipal().toString();
            }
        }

        return tekija;
    }
}
