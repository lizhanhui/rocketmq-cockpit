package com.ndpmedia.rocketmq.cockpit.spring;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateObjectMapper extends ObjectMapper {
    public DateObjectMapper() {
        CustomSerializerFactory factory = new CustomSerializerFactory();
        factory.addGenericMapping(Date.class, new JsonSerializer<Date>() {
            @Override
            public void serialize(Date date,
                                  JsonGenerator jsonGenerator,
                                  SerializerProvider provider)
                    throws IOException, JsonProcessingException {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                jsonGenerator.writeString(simpleDateFormat.format(date));
            }
        });
        this.setSerializerFactory(factory);
    }
}
