package com.econnect.barangaymanagementapp.Service;

import okhttp3.*;

import java.io.File;
import java.io.IOException;

public class ImageService {

    private static final String FIREBASE_STORAGE_URL = "https://firebasestorage.googleapis.com/v0/b/sia101-d60a1.appspot.com/o/";

    public static void uploadImage(String filePath) {
        OkHttpClient client = new OkHttpClient();
        File file = new File(filePath);
        String fileName = file.getName();

        MediaType mediaType = MediaType.parse("image/png");
        RequestBody requestBody = RequestBody.create(mediaType, file);

        Request request = new Request.Builder()
                .url(FIREBASE_STORAGE_URL + fileName + "?uploadType=media")
                .post(requestBody)
                .addHeader("Content-Type", "image/jpeg")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            System.out.println("Image uploaded successfully: " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
