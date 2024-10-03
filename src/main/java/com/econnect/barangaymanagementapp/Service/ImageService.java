package com.econnect.barangaymanagementapp.Service;

import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.HTTPClient;
import javafx.scene.image.Image;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URLConnection;

public class ImageService {

    private final HTTPClient client;
    private static final String FIREBASE_STORAGE_URL = "https://firebasestorage.googleapis.com/v0/b/sia101-d60a1.appspot.com/o/";

    public ImageService(DependencyInjector dependencyInjector) {
        this.client = dependencyInjector.getHttpClient();
    }

    public Image getImage(String directory, String filename) {
        if (!isInternetAvailable()) {
            System.out.println("No internet connection.");
            return null;
        }

        Request request = new Request.Builder()
                .url(FIREBASE_STORAGE_URL + directory + "%2F" + filename + "?alt=media")
                .build();
        try (Response response = client.getClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return new Image(response.body().byteStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void uploadImage(String filePath) {
        if (!isInternetAvailable()) {
            System.out.println("No internet connection.");
            return; // Or handle accordingly
        }

        File file = new File(filePath);
        String fileName = file.getName();

        String mimeType = URLConnection.guessContentTypeFromName(fileName);

        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        MediaType mediaType = MediaType.parse(mimeType);
        RequestBody requestBody = RequestBody.create(mediaType, file);

        Request request = new Request.Builder()
                .url(FIREBASE_STORAGE_URL + fileName + "?uploadType=media")
                .post(requestBody)
                .addHeader("Content-Type", mimeType)
                .build();

        try (Response response = client.getClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            System.out.println("Image uploaded successfully: " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadImage(File file) {
        if (!isInternetAvailable()) {
            System.out.println("No internet connection.");
            return; // Or handle accordingly
        }

        String fileName = file.getName();

        String mimeType = URLConnection.guessContentTypeFromName(fileName);

        // Handle the case where the MIME type cannot be determined
        if (mimeType == null) {
            mimeType = "application/octet-stream"; // Default to binary if the type cannot be determined
        }

        MediaType mediaType = MediaType.parse(mimeType);
        RequestBody requestBody = RequestBody.create(mediaType, file);

        Request request = new Request.Builder()
                .url(FIREBASE_STORAGE_URL + fileName + "?uploadType=media")
                .post(requestBody)
                .addHeader("Content-Type", mimeType)
                .build();

        try (Response response = client.getClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            System.out.println("Image uploaded successfully: " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isInternetAvailable() {
        try {
            // Ping a reliable website (Google)
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (Exception e) {
            return false; // No internet connection
        }
    }
}
