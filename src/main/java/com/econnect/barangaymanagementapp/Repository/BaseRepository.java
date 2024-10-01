package com.econnect.barangaymanagementapp.Repository;

import com.econnect.barangaymanagementapp.Domain.BaseEntity;
import com.econnect.barangaymanagementapp.Enumeration.Modal;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.HTTPClient;
import com.econnect.barangaymanagementapp.Utils.JsonConverter;
import com.econnect.barangaymanagementapp.Utils.ModalUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class BaseRepository<T> {
    protected final HTTPClient client;
    protected final JsonConverter jsonConverter;
    private final ModalUtils modalUtils;

    public BaseRepository(DependencyInjector dependencyInjector) {
        this.client = dependencyInjector.getHttpClient();
        this.jsonConverter = dependencyInjector.getJsonConverter();
        this.modalUtils = dependencyInjector.getModalUtils();
    }

    protected Response create(String apiUrl, T object) {
        try {
            Request request = new Request.Builder()
                    .url(apiUrl + ".json")
                    .put(RequestBody.create(
                            jsonConverter.convertObjectToJson(object), MediaType.parse("application/json")
                    ))
                    .build();
            Response response = client.getClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                modalUtils.showModal(Modal.ERROR, "Failed", "Failed to add employees. Please try again.");
            }
            return response;
        } catch (IOException | IllegalArgumentException e) {
            modalUtils.showModal(Modal.ERROR, "Error", "An error occurred while adding the employee.");
            System.err.println("Unexpected Error: " + e.getMessage());
            return null;
        }
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