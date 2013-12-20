/**
 * 
 */
package fi.vm.sade.koodisto.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

/**
 * @author tommiha
 *
 */
public class ByteArrayDataSource implements DataSource {

    private String name = "ByteArrayDataSource";
    private String contentType = "application/octet-stream";
    private byte[] content;
    
    /**
     * Creates new string data source of mimetype application/octet-stream and name ByteArrayDataSource.
     * @param payload
     */
    public ByteArrayDataSource(byte[] payload) {
        content = payload.clone();
    }

    /**
     * Creates new string data source of mimetype application/octet-stream.
     * @param payload
     * @param name
     */
    public ByteArrayDataSource(byte[] payload, String name) {
        content = payload.clone();
        this.name = name;
    }

    /**
     * Creates new string data source
     * @param content
     * @param name
     * @param contentType
     */
    public ByteArrayDataSource(byte[] content, String name, String contentType) {
        this.content = content.clone();
        this.contentType = contentType;
        this.name = name;
    }
    
    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Read-only javax.activation.DataSource");
    }

}
