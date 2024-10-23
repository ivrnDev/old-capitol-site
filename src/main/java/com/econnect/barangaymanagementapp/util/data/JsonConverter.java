package com.econnect.barangaymanagementapp.util.data;

import com.econnect.barangaymanagementapp.config.deserializer.JacksonFactory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static com.econnect.barangaymanagementapp.enumeration.type.ResidentInfomationType.*;

public class JsonConverter {
    private final ObjectMapper objectMapper;

    public JsonConverter() {
        SimpleModule customModule = new SimpleModule();
        customModule.addDeserializer(GenderType.class,
                new JacksonFactory.EnumDeserializer<>(GenderType::fromName));
        customModule.addDeserializer(CivilStatus.class,
                new JacksonFactory.EnumDeserializer<>(CivilStatus::fromName));
        customModule.addDeserializer(BloodType.class,
                new JacksonFactory.EnumDeserializer<>(BloodType::fromName));
        customModule.addDeserializer(Religion.class,
                new JacksonFactory.EnumDeserializer<>(Religion::fromName));
        customModule.addDeserializer(EconomicLevelType.class,
                new JacksonFactory.EnumDeserializer<>(EconomicLevelType::fromName));
        customModule.addDeserializer(MotherTongue.class,
                new JacksonFactory.EnumDeserializer<>(MotherTongue::fromName));

        customModule.addSerializer(GenderType.class,
                new JacksonFactory.EnumSerializer<>(GenderType::getName));
        customModule.addSerializer(CivilStatus.class,
                new JacksonFactory.EnumSerializer<>(CivilStatus::getName));
        customModule.addSerializer(BloodType.class,
                new JacksonFactory.EnumSerializer<>(BloodType::getName));
        customModule.addSerializer(Religion.class,
                new JacksonFactory.EnumSerializer<>(Religion::getName));
        customModule.addSerializer(EconomicLevelType.class,
                new JacksonFactory.EnumSerializer<>(EconomicLevelType::getName));
        customModule.addSerializer(MotherTongue.class,
                new JacksonFactory.EnumSerializer<>(MotherTongue::getName));

        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(customModule)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }

    public String convertObjectToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
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
