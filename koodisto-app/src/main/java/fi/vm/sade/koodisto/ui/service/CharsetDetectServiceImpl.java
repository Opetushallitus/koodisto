/**
 * 
 */
package fi.vm.sade.koodisto.ui.service;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * @author tommiha
 * 
 */
@Service
public class CharsetDetectServiceImpl implements CharsetDetectService, InitializingBean {

    CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public String detectCharset(InputStream stream) {
        Charset charset = null;
        try {
            charset = detector.detectCodepage(stream, Integer.MAX_VALUE);
        } catch (IllegalArgumentException e) {
            log.error("Error detecting charset.", e);
        } catch (IOException e) {
            log.error("Error detecting charset.", e);
        }

        return charsetToString(charset);
    }

    private String charsetToString(Charset charset) {
        if (charset == null) {
            return null;
        }

        if (charset.name().equals("windows-1252")) {
            return "ISO-8859-1";
        }
        return charset.name();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        detector.add(new ByteOrderMarkDetector());
        // The first instance delegated to tries to detect the meta charset
        // attribute in html pages.
        detector.add(new ParsingDetector(false)); // be verbose about parsing.
        // This one does the tricks of exclusion and frequency detection, if
        // first implementation is unsuccessful:
        detector.add(JChardetFacade.getInstance()); // Another singleton.
        detector.add(ASCIIDetector.getInstance());
    }

}
