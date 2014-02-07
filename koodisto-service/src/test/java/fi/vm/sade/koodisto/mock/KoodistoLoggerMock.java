package fi.vm.sade.koodisto.mock;

import fi.vm.sade.log.client.Logger;
import fi.vm.sade.log.model.Tapahtuma;
import org.slf4j.LoggerFactory;

/**
 * Created by wuoti on 30.1.2014.
 */
public class KoodistoLoggerMock implements Logger {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void log(Tapahtuma tapahtuma) {
        logger.info("LOGGED: " + tapahtuma);
    }
}
