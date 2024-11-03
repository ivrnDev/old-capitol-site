package com.econnect.barangaymanagementapp.repository;

import com.econnect.barangaymanagementapp.domain.BaseEntity;
import com.econnect.barangaymanagementapp.enumeration.modal.Modal;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.HTTPClient;
import com.econnect.barangaymanagementapp.util.LiveReloadUtils;
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
    private final LiveReloadUtils liveReloadUtils;

    public BaseRepository(DependencyInjector dependencyInjector) {
        this.client = dependencyInjector.getHttpClient();
        this.jsonConverter = dependencyInjector.getJsonConverter();
        this.modalUtils = dependencyInjector.getModalUtils();
        this.liveReloadUtils = dependencyInjector.getLiveReloadUtils();
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

    // You can also provide a method to start listening for updates
    public void enableLiveReload(String apiKey, Consumer<String> handleDataUpdate, String context) {
        liveReloadUtils.startListeningToUpdates(apiKey, handleDataUpdate, context);
    }

//    public void startListeningToUpdates(String apiKey, Consumer<String> handleDataUpdate, String context) {
//        Request request = new Request.Builder()
//                .url(apiKey + ".json")
//                .addHeader("Accept", "text/event-stream")
//                .get().build();
//
//
//        try {
//            Call sseCall = new OkHttpClient.Builder().readTimeout(0, TimeUnit.SECONDS).build().newCall(request);
//            sseCalls.put(context, sseCall);
//        } catch (IllegalArgumentException e) {
//            System.err.println(context + " Failed to create call for SSE: " + e.getMessage());
//            return;
//        }
//
//        System.out.println(context + " Initializing SSE connection...");
//
//        sseCalls.get(context).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                if (!call.isCanceled()) {
//                    System.err.println(context + " SSE Connection failed" + e.getMessage());
//                    retryConnection(apiKey, handleDataUpdate, context);
//                } else {
//                    System.out.println(context + " SSE connection has been closed");
//                }
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (!response.isSuccessful()) {
//                    if (!call.isCanceled()) {
//                        System.err.println(context + " Failed to receive SSE updates" + response.message());
//                        retryConnection(apiKey, handleDataUpdate, context);
//                    } else {
//                        System.out.println(context + " SSE connection has been closed");
//                    }
//                    return;
//                }
//
//                System.out.println(context + " Live connection has established successfully!");
//
//                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
//                    String line;
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    while ((line = reader.readLine()) != null) {
//                        if (line.equals("event: keep-alive")) printSSEStatus(context);
//                        if (!line.startsWith("data: ")) continue;
//                        String jsonData = line.substring("data: ".length()).trim();
//                        if (jsonData.equals("null")) continue;
//                        if (jsonData.contains("\"path\":\"/\"")) {
//                            continue;
//                        }
//                        try {
//                            JsonNode jsonNode = objectMapper.readTree(jsonData);
//                            JsonNode pathNode = jsonNode.get("path");
//                            if (pathNode == null) return;
//                            String path = pathNode.asText();
//                            if (!path.contains("/")) return;
//                            String id = path.split("/")[1];
//                            System.out.println(context + " Syncing Data for ID: " + id);
//                            handleDataUpdate.accept(id);
//                        } catch (Exception e) {
//                            System.err.println(context + " Error parsing JSON data:");
//                        }
//                    }
//                } catch (SocketTimeoutException e) {
//                    if (!call.isCanceled()) {
//                        retryConnection(apiKey, handleDataUpdate, context);
//                    } else {
//                        System.out.println(context + " SSE connection has been closed");
//                    }
//                    System.err.println(context + " SocketTimeoutException: " + e.getMessage());
//                } catch (IOException e) {
//                    if (!call.isCanceled()) {
//                        retryConnection(apiKey, handleDataUpdate, context);
//                    } else {
//                        System.out.println(context + " SSE connection has been closed");
//                    }
//                    System.err.println(context + " Error reading SSE updates: " + e.getMessage());
//                }
//            }
//        });
//    }
//
//    private void retryConnection(String apiKey, Consumer<String> handleDataUpdate, String context) {
//        System.out.println(context + " Retrying connection in 5 seconds...");
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            System.err.println(context + " Retry interrupted" + e.getMessage());
//        }
//        startListeningToUpdates(apiKey, handleDataUpdate, context);
//    }
//
////    private void stopListeningToUpdates(String context) {
////        Call sseCall = sseCalls.remove(context);
////        if (sseCall != null) {
////            sseCall.cancel();
////            System.out.println(context + " SSE connection closed");
////        } else {
////            System.out.println(context + " SSE connection was not found");
////        }
////    }
//
//    public void stopListeningToUpdates() {
//        sseCalls.forEach((context, call) -> {
//            if (call != null) {
//                call.cancel();
//                System.out.println(context + " SSE connection closed");
//            }
//        });
//        sseCalls.clear();
//    }
//
//    public void printSSEStatus(String context) {
//        Call call = sseCalls.get(context); // Get the call by context
//        if (call == null) {
//            System.out.println(context + " connection has not been initialized");
//        } else if (call.isExecuted() && !call.isCanceled()) {
//            System.out.println(context + " connection is running for call: " + call.hashCode());
//        } else if (call.isCanceled()) {
//            System.out.println(context + " connection is canceled for call: " + call.hashCode());
//        } else {
//            System.out.println(context + " connection is not running for call: " + call.hashCode());
//        }
//    }

}