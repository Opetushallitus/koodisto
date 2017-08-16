package fi.vm.sade.koodisto.audit;

import fi.vm.sade.auditlog.Logger;
import org.slf4j.LoggerFactory;

/**
 * Koodiston auditlokitus slf4j:llä.
 *
 * Jos muutat tämän luokan pakettia tai nimeä, tee muutos myös log4j.properties
 * -konfiguraatioihin!
 */
public class AuditLogger implements Logger {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AuditLogger.class);

    @Override
    public void log(String msg) {
        LOGGER.info(msg);
    }

}
