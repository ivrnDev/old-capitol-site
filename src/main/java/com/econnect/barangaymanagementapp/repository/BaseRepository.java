package com.econnect.barangaymanagementapp.repository;

import com.econnect.barangaymanagementapp.domain.BaseEntity;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.HTTPClient;
import com.econnect.barangaymanagementapp.util.data.JsonConverter;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class BaseRepository<T> {
    protected final HTTPClient client;
    protected final JsonConverter jsonConverter;
    private final ModalUtils modalUtils;

    public BaseRepository(DependencyInjector dependencyInjector) {
        this.client = dependencyInjector.getHttpClient();
        this.jsonConverter = dependencyInjector.getJsonConverter();
        this.modalUtils = dependencyInjector.getModalUtils();
    }

    protected <T extends BaseEntity> Response create(String apiUrl, T object) {
        object.setId(null);
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
                return new ArrayList<>(objectsMap.values());
            }
            return Collections.emptyList();
        } catch (IOException e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    protected Optional<T> findById(String apiUrl, String id, TypeReference<T> typeReference) {
        Request request = new Request.Builder()
                .url(apiUrl + "/" + id + ".json")
                .get()
                .build();

        try (Response response = client.getClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP error, code: " + response.code() + " - " + response.message());
            }
            String responseBody = response.body().string();

            if (responseBody.equals("\"\"") || responseBody.isEmpty() || responseBody.equals("null")) {
                return Optional.empty();
            }

            T object = jsonConverter.convertJsonToObject(responseBody, typeReference);

            if (object != null) {
                ((BaseEntity) object).setId(id);
            }

            return Optional.ofNullable(object);
        } catch (IOException e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            return Optional.empty();
        }
    }

    protected Boolean deleteById(String apiUrl, String id) {
        Request request = new Request.Builder()
                .url(apiUrl + "/" + id + ".json")
                .delete()
                .build();

        try (Response response = client.getClient().newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            return false;
        }
    }

    protected Response update(String apiUrl, String id, T object) {
        Request request = new Request.Builder()
                .url(apiUrl + "/" + id + ".json")
                .patch(RequestBody.create(
                        jsonConverter.convertObjectToJson(object), MediaType.parse("application/json")
                ))
                .build();
        try (Response response = client.getClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP error, code: " + response.code() + " - " + response.message());
            }
            return response;
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            return null;
        }
    }

    public Response updateBy(String apiUrl, String id, TypeReference<T> typeReference, Consumer<T> statusUpdater) {
        Optional<T> entity = findById(apiUrl, id, typeReference);
        if (entity.isPresent()) {
            statusUpdater.accept(entity.get());
            return update(apiUrl, id, entity.get());
        }
        return null;
    }

    public List<T> findAllByFilter(String apiUrl, TypeReference<Map<String, T>> typeReference, Predicate<T> filter) {
        List<T> allEntities = findAll(apiUrl, typeReference);

        return allEntities.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }
}