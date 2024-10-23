package com.econnect.barangaymanagementapp.config.deserializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.function.Function;

public class JacksonFactory {
    public static class EnumDeserializer<T extends Enum<T>> extends JsonDeserializer<T> {
        private final Function<String, T> fromNameFunction;

        public EnumDeserializer(Function<String, T> fromNameFunction) {
            this.fromNameFunction = fromNameFunction;
        }

        @Override
        public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getText();
            return fromNameFunction.apply(value);
        }
    }

    public static class EnumSerializer<T extends Enum<T>> extends JsonSerializer<T> {
        private final Function<T, String> getNameFunction;

        public EnumSerializer(Function<T, String> getNameFunction) {
            this.getNameFunction = getNameFunction;
        }

        @Override
        public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value != null ? getNameFunction.apply(value) : null);
        }
    }
}
