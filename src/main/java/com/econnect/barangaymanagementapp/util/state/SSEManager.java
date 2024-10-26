package com.econnect.barangaymanagementapp.util.state;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class SSEManager {

    private final List<Call> activeSSEConnections = new ArrayList<>(); // Stores all active SSE connections
    private final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.SECONDS) // No read timeout for SSE
            .pingInterval(15, TimeUnit.SECONDS)
            .build();

    public void listenToUpdates(String apiKey, Consumer<Boolean> handleDataUpdate) {
        // Create the request for SSE
        Request request = new Request.Builder()
                .url(apiKey + ".json")
                .addHeader("Accept", "text/event-stream")
                .get()
                .build();

        Call sseCall = client.newCall(request);
        activeSSEConnections.add(sseCall); // Add to the list of active connections

        sseCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (!call.isCanceled()) {
                    System.err.println("Failed to connect for SSE: " + e.getMessage());
                } else {
                    System.out.println("SSE connection was closed by the user.");
                }
                activeSSEConnections.remove(call); // Remove from active list on failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.err.println("Failed to receive SSE updates: " + response.message());
                    activeSSEConnections.remove(call); // Remove if response unsuccessful
                    return;
                }

                System.out.println("Successfully connected to SSE updates.");

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("event: ")) {
                            String eventType = line.substring(7).trim();
                            handleDataUpdate.accept(true);
                            System.out.println("Event received: " + eventType);
                        }
                    }
                } catch (SocketTimeoutException e) {
                    System.err.println("SocketTimeoutException: " + e.getMessage());
                } finally {
                    activeSSEConnections.remove(call); // Clean up after closing
                }
            }
        });
    }

    public void listOpenConnections() {
        System.out.println("Active SSE connections: " + activeSSEConnections.size());
        for (Call call : activeSSEConnections) {
            System.out.println("Connection: " + call.request().url());
        }
    }

    public void closeAllConnections() {
        for (Call call : activeSSEConnections) {
            call.cancel(); // Close each SSE connection
        }
        activeSSEConnections.clear(); // Clear the list after closing
        System.out.println("All SSE connections closed.");
    }
}
