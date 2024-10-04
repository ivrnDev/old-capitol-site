package com.econnect.barangaymanagementapp.Utils;

import javafx.scene.image.ImageView;
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
}
