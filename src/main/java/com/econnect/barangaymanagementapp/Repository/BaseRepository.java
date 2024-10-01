package com.econnect.barangaymanagementapp.Repository;

import com.econnect.barangaymanagementapp.Domain.BaseEntity;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.HTTPClient;
import com.econnect.barangaymanagementapp.Utils.JsonConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class BaseRepository<T> {
    protected final HTTPClient client;
    protected final JsonConverter jsonConverter;

    public BaseRepository(DependencyInjector dependencyInjector) {
        this.client = dependencyInjector.getHttpClient();
        this.jsonConverter = dependencyInjector.getJsonConverter();
    }

    protected List<T> findAll(String apiUrl, TypeReference<Map<String, T>> typeReference) {
        Request request = new Request.Builder()
                .url(apiUrl + ".json")
                .get()
                .build();

        try (Response response = client.getClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP error, code: " + response.code() + " - " + response.message());
            }
            String responseBody = response.body().string();

            if (responseBody.equals("\"\"") || responseBody.isEmpty() || responseBody.equals("null")) {
                return Collections.emptyList();
            }
            Map<String, T> objectsMap = jsonConverter.convertJsonToObject(responseBody, typeReference);

            if (objectsMap != null) {
                objectsMap.forEach((id, object) -> {
                    if (object != null) {
                        ((BaseEntity) object).setId(id);
                    }
                });
            }
            return new ArrayList<>(objectsMap.values());
        } catch (IOException e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}