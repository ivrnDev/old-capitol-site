package com.econnect.barangaymanagementapp.util.resource;

import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class ImageUtils {
    public static void setRoundedClip(ImageView imageView, double arcWidth, double arcHeight) {
        if (imageView.getFitWidth() > 0 && imageView.getFitHeight() > 0) {
            Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
            clip.setArcWidth(arcWidth);
            clip.setArcHeight(arcHeight);
            imageView.setClip(clip);
        } else {
            System.out.println("ImageView dimensions are not valid for clipping.");
        }
    }

    public static void setCircleClip(ImageView imageView) {
        if (imageView.getFitWidth() > 0 && imageView.getFitHeight() > 0) {
            double radius = Math.min(imageView.getFitWidth(), imageView.getFitHeight()) / 2.0;

            Circle clip = new Circle(radius);

            clip.setCenterX(imageView.getFitWidth() / 2.0);
            clip.setCenterY(imageView.getFitHeight() / 2.0);

            imageView.setClip(clip);
        } else {
            System.out.println("ImageView dimensions are not valid for clipping.");
        }
    }
}