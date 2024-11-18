package com.econnect.barangaymanagementapp.util.data;

import com.econnect.barangaymanagementapp.config.deserializer.JacksonFactory;
import com.econnect.barangaymanagementapp.enumeration.type.CertificateType;
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
        customModule.addDeserializer(ResidencyStatus.class,
                new JacksonFactory.EnumDeserializer<>(ResidencyStatus::fromName));
        customModule.addDeserializer(CertificateType.class,
                new JacksonFactory.EnumDeserializer<>(CertificateType::fromName));


        customModule.addSerializer(GenderType.class,
                new JacksonFactory.EnumSerializer<>(GenderType::getName));
        customModule.addSerializer(CivilStatus.class,
                new JacksonFactory.EnumSerializer<>(CivilStatus::getName));
        customModule.addSerializer(BloodType.class,
                new JacksonFactory.EnumSerializer<>(BloodType::getName));
        customModule.addSerializer(ResidencyStatus.class,
                new JacksonFactory.EnumSerializer<>(ResidencyStatus::getName));
        customModule.addSerializer(CertificateType.class,
                new JacksonFactory.EnumSerializer<>(CertificateType::getName));


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
