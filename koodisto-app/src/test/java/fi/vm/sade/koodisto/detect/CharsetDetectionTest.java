/**
 * 
 */
package fi.vm.sade.koodisto.detect;

import static org.junit.Assert.assertEquals;
import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;

import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author tommiha
 * 
 */
public class CharsetDetectionTest {

    CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        detector.add(new ByteOrderMarkDetector());
        // The first instance delegated to tries to detect the meta charset
        // attribute in html pages.
        detector.add(new ParsingDetector(false)); // be verbose about parsing.
        // This one does the tricks of exclusion and frequency detection, if
        // first implementation is unsuccessful:
        detector.add(JChardetFacade.getInstance()); // Another singleton.
        detector.add(ASCIIDetector.getInstance());
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDetectUtf8() throws IOException {
        Resource res = new ClassPathResource("Kunnat.csv");

        Charset charset = detector.detectCodepage(res.getURL());
        assertEquals("UTF-8", charset.name());
    }

    @Test
    public void testDetectIso88591() throws IOException {
        // Detects all iso-8859-1 as windows-1252
        Resource res = new ClassPathResource("Kunnat-ISO-8859-1.csv");
        Charset charset = detector.detectCodepage(res.getURL());
        assertEquals("windows-1252", charset.name());

        res = new ClassPathResource("latin-1.txt");
        charset = detector.detectCodepage(res.getURL());
        assertEquals("windows-1252", charset.name());

        res = new ClassPathResource("windows_encoding.txt");
        charset = detector.detectCodepage(res.getURL());
        assertEquals("windows-1252", charset.name());
    }

}
