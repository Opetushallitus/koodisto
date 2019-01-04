package fi.vm.sade.koodisto.service.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FinnishJsonDateSerializer extends JsonSerializer<Date> {
    
    private static final DateFormat finnishFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
    
    @Override
    public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        if (value != null) jgen.writeString(finnishFormat.format(value));
    }

}

