package fi.vm.sade.koodisto.service.provider;

import com.fasterxml.jackson.databind.*;
import org.springframework.stereotype.Component;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;


@Component
@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

    private final ObjectMapper objectMapper;

    public ObjectMapperProvider() {
        objectMapper = new ObjectMapper();

        objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }
}