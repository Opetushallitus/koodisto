package fi.vm.sade.koodisto.service.business.marshaller;


import fi.vm.sade.koodisto.service.types.common.KoodiType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Component;

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
    public Resource marshal(List<KoodiType> koodis, String encoding) throws IOException {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, getCharset(encoding)))) {
            marshaller.marshal(koodis, new StreamResult(writer));
            outputStream.flush();
            writer.flush();
            return new ByteArrayResource(outputStream.toByteArray(), "application/octet-stream");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<KoodiType> unmarshal(Resource resource, String encoding) throws IOException {

        try {
            StreamSource source = new StreamSource(new BufferedReader(new InputStreamReader(resource.getInputStream(),
                    getCharset(encoding))));
            return (List<KoodiType>) unmarshaller.unmarshal(source);
        } finally {
            resource.getInputStream().close();
        }
    }

}
