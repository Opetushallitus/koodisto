package fi.vm.sade.koodisto.service.business.marshaller;

import fi.jhs_suositukset.skeemat.oph._2012._05._03.Koodilistaus;
import fi.vm.sade.koodisto.service.impl.conversion.KoodilistausConverter;
import fi.vm.sade.koodisto.service.types.common.KoodiCollectionType;
import fi.vm.sade.koodisto.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

@Component
public class KoodistoXmlConverter extends KoodistoConverter {

    private final Marshaller marshaller;
    private final Unmarshaller unmarshaller;
    private final KoodilistausConverter converter;

    public KoodistoXmlConverter(@Autowired KoodilistausConverter converter) {
        try {
            JAXBContext context = JAXBContext.newInstance("fi.jhs_suositukset.skeemat.oph._2012._05._03");
            marshaller = context.createMarshaller();
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
        this.converter = converter;
    }

    @Override
    public DataHandler marshal(KoodiCollectionType koodis, String encoding) throws IOException {
        try (
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, getCharset(encoding)))) {
            marshaller.marshal(converter.koodilistausFrom(koodis), new StreamResult(writer));
            writer.flush();
            return new DataHandler(new ByteArrayDataSource(outputStream.toByteArray(), "application/octet-stream"));
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    @Override
    public KoodiCollectionType unmarshal(DataHandler handler, String encoding) throws IOException {
        try {
            StreamSource source = new StreamSource(new BufferedReader(new InputStreamReader(handler.getInputStream(),
                    getCharset(encoding))));
            Koodilistaus listaus = (Koodilistaus) unmarshaller.unmarshal(source);
            return converter.koodiCollectionFrom(listaus);
        } catch (JAXBException e) {
            throw new IOException(e);
        } finally {
            if (handler.getInputStream() != null) {
                handler.getInputStream().close();
            }
        }
    }

}
