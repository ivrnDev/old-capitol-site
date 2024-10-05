package com.econnect.barangaymanagementapp.Utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonConverter {
    private ObjectMapper objectMapper;

    public JsonConverter() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }

    public String convertObjectToJson(Object object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T convertJsonToObject(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
