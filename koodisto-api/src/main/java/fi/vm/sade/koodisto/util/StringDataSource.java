package fi.vm.sade.koodisto.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

public class StringDataSource implements DataSource {
    protected String content;
    protected String contentType = "text/plain";
    protected String name = "StringDataSource";

    /**
     * Creates new string data source of mimetype text/plain and name StringDataSource.
     * @param payload
     */
    public StringDataSource(String payload) {
        super();
        content = payload;
    }

    /**
     * Creates new string data source of mimetype text/plain.
     * @param payload
     * @param name
     */
    public StringDataSource(String payload, String name) {
        super();
        content = payload;
        this.name = name;
    }

    /**
     * Creates new string data source.
     * @param content
     * @param name
     * @param contentType
     */
    public StringDataSource(String content, String name, String contentType) {
        this.content = content;
        this.contentType = contentType;
        this.name = name;
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content.getBytes());
    }

    public OutputStream getOutputStream() {
        throw new UnsupportedOperationException("Read-only javax.activation.DataSource");
    }

    public String getContentType() {
        return contentType;
    }

    public String getName() {
        return name;
    }
}
