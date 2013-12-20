package fi.vm.sade.koodisto.service.business.marshaller;


import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.List;

/**
 * User: kwuoti
 * Date: 8.4.2013
 * Time: 12.12
 */
@Component
public class KoodistoXmlConverter extends KoodistoConverter {

    @Autowired
    private Marshaller marshaller;

    @Autowired
    private Unmarshaller unmarshaller;

    @Override
    public DataHandler marshal(List<KoodiType> koodis, String encoding) throws IOException {

        ByteArrayOutputStream outputStream = null;
        BufferedWriter writer = null;
        try {
            outputStream = new ByteArrayOutputStream();
            writer = new BufferedWriter(new OutputStreamWriter(outputStream, getCharset(encoding)));

            marshaller.marshal(koodis, new StreamResult(writer));
            outputStream.flush();
            writer.flush();
            return new DataHandler(new ByteArrayDataSource(outputStream.toByteArray(), "application/octet-stream"));
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<KoodiType> unmarshal(DataHandler handler, String encoding) throws IOException {

        try {
            StreamSource source = new StreamSource(new BufferedReader(new InputStreamReader(handler.getInputStream(),
                    getCharset(encoding))));
            return (List<KoodiType>) unmarshaller.unmarshal(source);
        } finally {
            if (handler.getInputStream() != null) {
                handler.getInputStream().close();
            }
        }
    }

}