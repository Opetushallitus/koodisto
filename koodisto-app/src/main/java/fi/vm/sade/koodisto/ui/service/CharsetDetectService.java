/**
 * 
 */
package fi.vm.sade.koodisto.ui.service;

import java.io.InputStream;

/**
 * @author tommiha
 * 
 */
public interface CharsetDetectService {

    /**
     * Returns detected charset if found.
     * 
     * @param stream
     * @return
     */
    String detectCharset(InputStream stream);
}
