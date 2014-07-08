package fi.vm.sade.koodisto.service.impl.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemporaryFileInputStream extends FileInputStream {

    private File file;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public TemporaryFileInputStream(File file) throws FileNotFoundException {
        super(file);
        this.file = file;
    }

    /**
     * Deletes file after closing the stream.
     */
    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            if (file != null && file.delete()) {
                logger.debug("Deleted temporary file " + file.getAbsolutePath());
            } else {
                logger.error("Failed to delete temporary file " + file.getAbsolutePath());
            }
        }
    }

}
