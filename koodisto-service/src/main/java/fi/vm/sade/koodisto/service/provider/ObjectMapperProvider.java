package fi.vm.sade.koodisto.service.provider;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.springframework.stereotype.Component;

import javax.ws.rs.ext.Provider;


@Component
@Provider
public class ObjectMapperProvider extends JacksonJaxbJsonProvider {

    public ObjectMapperProvider() {
        super();
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.setMapper(objectMapper);
    }
}
