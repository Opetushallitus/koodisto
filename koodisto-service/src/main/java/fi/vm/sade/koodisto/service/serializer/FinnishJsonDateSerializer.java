package fi.vm.sade.koodisto.service.serializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class FinnishJsonDateSerializer extends JsonSerializer<Date>{
    
    private static final DateFormat finnishFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
    
    @Override
    public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        if (value != null) jgen.writeString(finnishFormat.format(value));
    }

}
