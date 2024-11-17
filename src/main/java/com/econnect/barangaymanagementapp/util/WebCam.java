package com.econnect.barangaymanagementapp.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.IplImage;

import java.nio.ByteBuffer;

public class WebCam {
    private FrameGrabber grabber;
    private OpenCVFrameConverter.ToIplImage converter;
    private OnImageCapturedListener captureListener;
    private Image image;

    public WebCam() {
        grabber = new OpenCVFrameGrabber(0); // Default webcam
        converter = new OpenCVFrameConverter.ToIplImage();
    }

    // Method to set the callback listener
    public void setOnCaptureImage(OnImageCapturedListener listener) {
        this.captureListener = listener;
    }

    public void startWithCapture(Stage parentStage) {
        Stage stage = new Stage();
        Platform.runLater(() -> {
            StackPane root = new StackPane();
            Scene scene = new Scene(root);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.initOwner(parentStage);
            stage.setScene(scene);

            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());


            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
            imageView.setFitWidth(screenBounds.getWidth());
            imageView.setFitHeight(screenBounds.getHeight());
            imageView.setPreserveRatio(false);
            imageView.setCursor(javafx.scene.Cursor.CROSSHAIR);
            root.getChildren().add(imageView);

            handleEvent(stage, imageView);
            stage.show();
        });
        new Thread(() -> {
            try {
                grabber.start();
                while (stage.isShowing()) {
                    Frame frame = grabber.grab();
                    if (frame != null) {
                        WritableImage fxImage = convertToWritableImage(converter.convert(frame));
                        if (fxImage != null) {
                            Platform.runLater(() -> {
                                StackPane root = (StackPane) stage.getScene().getRoot();
                                javafx.scene.image.ImageView imageView = (javafx.scene.image.ImageView) root.getChildren().get(0);
                                imageView.setImage(fxImage);
                            });
                        }
                    }
                }

                grabber.stop();
            } catch (FrameGrabber.Exception e) {
                System.err.println("Error accessing webcam: " + e.getMessage());
            }
        }).start();
    }

    private WritableImage captureCurrentFrame() {
        try {
            Frame frame = grabber.grab();
            if (frame == null) {
                System.err.println("Failed to grab a frame. Check webcam status.");
                return null;
            }
            IplImage img = converter.convert(frame);
            return convertToWritableImage(img);
        } catch (FrameGrabber.Exception e) {
            System.err.println("Error capturing current frame: " + e.getMessage());
            return null;
        }
    }

    private WritableImage convertToWritableImage(IplImage img) {
        if (img == null) return null;

        int width = img.width();
        int height = img.height();
        int channels = img.nChannels();
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        ByteBuffer buffer = img.getByteBuffer();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * img.widthStep() + x * channels;

                // Get BGR values (convert to RGB)
                int blue = buffer.get(index) & 0xFF;
                int green = buffer.get(index + 1) & 0xFF;
                int red = buffer.get(index + 2) & 0xFF;

                // Set RGB pixel to JavaFX image
                pixelWriter.setColor(x, y, Color.rgb(red, green, blue));
            }
        }

        return writableImage;
    }

    private void handleEvent(Stage stage, ImageView imageView) {
        stage.addEventHandler(javafx.scene.input.KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                WritableImage capturedImage = captureCurrentFrame();
                if (capturedImage != null && captureListener != null) {
                    Platform.runLater(() -> captureListener.onImageCaptured(capturedImage));
                } else {
                    System.err.println("Failed to capture image.");
                }
                Platform.runLater(() -> {
                    stage.close();
                });
            }
        });

        stage.addEventHandler(javafx.scene.input.KeyEvent.KEY_RELEASED, e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                Platform.runLater(() -> {
                    stage.close();
                });
            }
        });

        stage.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> {
            WritableImage capturedImage = captureCurrentFrame();
            if (capturedImage != null && captureListener != null) {
                Platform.runLater(() -> captureListener.onImageCaptured(capturedImage));
            } else {
                System.err.println("Failed to capture image.");
            }
            Platform.runLater(() -> {
                stage.close();
            });
        });
    }

    @FunctionalInterface
    public interface OnImageCapturedListener {
        void onImageCaptured(Image image);
    }
}
