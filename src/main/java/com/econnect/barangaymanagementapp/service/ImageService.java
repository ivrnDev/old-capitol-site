package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.config.Config;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.HTTPClient;
import javafx.scene.image.Image;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;

public class ImageService {

    private final HTTPClient client;
    private static final String FIREBASE_STORAGE_URL = Config.getStorageUrl();

    public ImageService(DependencyInjector dependencyInjector) {
        this.client = dependencyInjector.getHttpClient();
    }

    public Image getImage(String directory, String link) {
        Request request = new Request.Builder()
                .url(link)
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

    public void uploadImage(String filePath, Firestore directory) {
        File file = new File(filePath);
        String fileName = file.getName();

        String mimeType = URLConnection.guessContentTypeFromName(fileName);

        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        MediaType mediaType = MediaType.parse(mimeType);
        RequestBody requestBody = RequestBody.create(mediaType, file);

        Request request = new Request.Builder()
                .url(FIREBASE_STORAGE_URL + directory + "%2F" + fileName + "?uploadType=media")
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

    public String uploadImage(Firestore directory, File file, String id) {
        String fileName = file.getName();
        String mimeType = URLConnection.guessContentTypeFromName(fileName);
        String url = FIREBASE_STORAGE_URL + directory.getPath() + "%2F" + id;
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        MediaType mediaType = MediaType.parse(mimeType);
        RequestBody requestBody = RequestBody.create(mediaType, file);

        Request request = new Request.Builder()
                .url(url + "?uploadType=media")
                .post(requestBody)
                .addHeader("Content-Type", mimeType)
                .build();

        try (Response response = client.getClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return url + "?alt=media";
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String uploadImage(Firestore directory, Image image, String id) {
        File file = null;
        try {
            file = saveImageToFile(image);
            return uploadImage(directory, file, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private File saveImageToFile(Image image) throws Exception {
        // Create a temporary file
        File tempFile = File.createTempFile("captured_image", ".png");
        tempFile.deleteOnExit();

        // Extract pixel data from the Image
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        javafx.scene.image.PixelReader pixelReader = image.getPixelReader();

        // Create a BufferedImage for writing
        java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);

        // Transfer pixel data
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                javafx.scene.paint.Color fxColor = pixelReader.getColor(x, y);
                int argb = (int) (fxColor.getOpacity() * 255) << 24 |
                        (int) (fxColor.getRed() * 255) << 16 |
                        (int) (fxColor.getGreen() * 255) << 8 |
                        (int) (fxColor.getBlue() * 255);
                bufferedImage.setRGB(x, y, argb);
            }
        }

        // Write the BufferedImage to a file
        javax.imageio.ImageIO.write(bufferedImage, "png", tempFile);

        return tempFile;
    }

}
