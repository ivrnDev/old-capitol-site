package com.econnect.barangaymanagementapp.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class LiveReloadUtils {

    private Map<String, Call> sseCalls = new ConcurrentHashMap<>();

    public void startListeningToUpdates(String apiKey, Consumer<String> handleDataUpdate, String context) {
        Request request = new Request.Builder()
                .url(apiKey + ".json")
                .addHeader("Accept", "text/event-stream")
                .get().build();

        try {
            Call sseCall = new OkHttpClient.Builder().readTimeout(0, TimeUnit.SECONDS).build().newCall(request);
            sseCalls.put(context, sseCall);
        } catch (IllegalArgumentException e) {
            System.err.println(context + " Failed to create call for SSE: " + e.getMessage());
            return;
        }

        sseCalls.get(context).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (!call.isCanceled()) {
                    System.err.println(context + " SSE Connection failed" + e.getMessage());
                    retryConnection(apiKey, handleDataUpdate, context);
                } else {
                    System.out.println(context + " SSE connection has been closed");
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    if (!call.isCanceled()) {
                        System.err.println(context + " Failed to receive SSE updates" + response.message());
                        retryConnection(apiKey, handleDataUpdate, context);
                    } else {
                        System.out.println(context + " SSE connection has been closed");
                    }
                    return;
                }

                System.out.println(context + " Live connection has established successfully!");

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
                    String line;
                    ObjectMapper objectMapper = new ObjectMapper();
                    while ((line = reader.readLine()) != null) {
                        if (line.equals("event: keep-alive")) printSSEStatus(context);
                        if (!line.startsWith("data: ")) continue;
                        String jsonData = line.substring("data: ".length()).trim();
                        if (jsonData.equals("null")) continue;
                        if (jsonData.contains("\"path\":\"/\"")) {
                            continue;
                        }
                        try {
                            JsonNode jsonNode = objectMapper.readTree(jsonData);
                            JsonNode pathNode = jsonNode.get("path");
                            if (pathNode == null) return;
                            String path = pathNode.asText();
                            if (!path.contains("/")) return;
                            String id = path.split("/")[1];
                            System.out.println(context + " Syncing Data for ID: " + id);
                            handleDataUpdate.accept(id);
                        } catch (Exception e) {
                            System.err.println(context + " Error parsing JSON data:");
                        }
                    }
                } catch (SocketTimeoutException e) {
                    if (!call.isCanceled()) {
                        retryConnection(apiKey, handleDataUpdate, context);
                    } else {
                        System.out.println(context + " SSE connection has been closed");
                    }
                    System.err.println(context + " SocketTimeoutException: " + e.getMessage());
                } catch (IOException e) {
                    if (!call.isCanceled()) {
                        retryConnection(apiKey, handleDataUpdate, context);
                    } else {
                        System.out.println(context + " SSE connection has been closed");
                    }
                    System.err.println(context + " Error reading SSE updates: " + e.getMessage());
                }
            }
        });
    }

    public void stopListeningToUpdates() {
        sseCalls.forEach((context, call) -> {
            if (call != null) {
                call.cancel();
            }
        });
        sseCalls.clear();
    }

    private void retryConnection(String apiKey, Consumer<String> handleDataUpdate, String context) {
        System.out.println(context + " Retrying connection in 5 seconds...");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println(context + " Retry interrupted" + e.getMessage());
        }
        startListeningToUpdates(apiKey, handleDataUpdate, context);
    }

    public void printSSEStatus(String context) {
        Call call = sseCalls.get(context);
        if (call == null) {
            System.out.println(context + " connection has not been initialized");
        } else if (call.isExecuted() && !call.isCanceled()) {
            System.out.println(context + " connection is running for call: " + call.hashCode());
        } else if (call.isCanceled()) {
            System.out.println(context + " connection is canceled for call: " + call.hashCode());
        } else {
            System.out.println(context + " connection is not running for call: " + call.hashCode());
        }
    }
}

