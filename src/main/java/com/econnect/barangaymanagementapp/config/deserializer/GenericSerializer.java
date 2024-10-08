package com.econnect.barangaymanagementapp.config.deserializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.lang.reflect.Field;

public class GenericSerializer<T> extends JsonSerializer<T> {
    public GenericSerializer() {
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        Field[] fields = value.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);  // Allow access to private fields
            try {
                Object fieldValue = field.get(value);
                // Convert null fields to empty strings, and handle non-string values appropriately
                if (field.getType() == String.class) {
                    gen.writeStringField(field.getName(), fieldValue != null ? (String) fieldValue : "");
                } else {
                    gen.writeObjectField(field.getName(), fieldValue);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace(); // Handle exceptions as needed
            }
        }

        gen.writeEndObject();
    }
}
