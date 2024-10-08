package com.econnect.barangaymanagementapp.util;

import okhttp3.OkHttpClient;

public class HTTPClient {
    private final OkHttpClient client;

    public HTTPClient() {
        this.client = new OkHttpClient();
    }

    public OkHttpClient getClient() {
        return client;
    }
}
