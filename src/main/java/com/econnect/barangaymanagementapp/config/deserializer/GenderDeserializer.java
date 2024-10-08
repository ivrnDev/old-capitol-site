package com.econnect.barangaymanagementapp.config.deserializer;

import com.econnect.barangaymanagementapp.enumeration.type.GenderType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class GenderDeserializer extends JsonDeserializer<GenderType> {
    @Override
    public GenderType deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        String value = p.getText();
        return GenderType.fromString(value);
    }
}
