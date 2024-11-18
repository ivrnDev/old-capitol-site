package com.econnect.barangaymanagementapp.util;

import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class PrintUtils {
    private static final double ID_CARD_WIDTH_MM = 85.60;
    private static final double ID_CARD_HEIGHT_MM = 53.98 * 2;

    public void printNode(Node node, Stage stage) {
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        if (printerJob != null && printerJob.showPrintDialog(stage)) {
            boolean success = printerJob.printPage(node);

            if (success) {
                printerJob.endJob();
                System.out.println("Printing completed successfully.");
            } else {
                System.out.println("Printing failed.");
            }
        } else {
            System.out.println("Printing canceled or no printers available.");
        }
    }

    public boolean printNodeFitToPage(Node node, Stage ownerStage) {
        Printer printer = Printer.getDefaultPrinter();
        if (printer == null) {
            System.out.println("No default printer found!");
            return false;
        }

        PrinterJob printerJob = PrinterJob.createPrinterJob(printer);
        if (printerJob == null) {
            System.out.println("Failed to create a printer job!");
            return false;
        }

        // Get the printer's page layout
        PageLayout pageLayout = printer.getDefaultPageLayout();
        double paperWidth = pageLayout.getPrintableWidth();
        double paperHeight = pageLayout.getPrintableHeight();

        // Get node's original size
        double originalWidth = node.getBoundsInParent().getWidth();
        double originalHeight = node.getBoundsInParent().getHeight();

        // Calculate scaling factors to fit the page
        double scaleX = paperWidth / originalWidth;
        double scaleY = paperHeight / originalHeight;

        // Apply scaling to the node (ignore aspect ratio)
        node.getTransforms().add(new Scale(scaleX, scaleY));

        // Show print dialog and print the page
        boolean userProceeded = printerJob.showPrintDialog(ownerStage);
        boolean success = userProceeded && printerJob.printPage(node);

        // Reset scaling after printing
        node.getTransforms().clear();

        // End the job if successful
        if (success) {
            printerJob.endJob();
            System.out.println("Printing successful!");
        } else if (userProceeded) {
            System.out.println("Printing failed!");
        } else {
            System.out.println("User canceled the print job!");
        }

        return success; // Return true if print is successful, otherwise false
    }

    public boolean printNodeAsIdCard(Node node, Stage ownerStage) {
        Printer printer = Printer.getDefaultPrinter();
        if (printer == null) {
            System.out.println("No default printer found!");
            return false;
        }

        PrinterJob printerJob = PrinterJob.createPrinterJob(printer);
        if (printerJob == null) {
            System.out.println("Failed to create a printer job!");
            return false;
        }

        double mmToPoints = 2.83465;
        double idCardWidthPoints = ID_CARD_WIDTH_MM * mmToPoints;
        double idCardHeightPoints = ID_CARD_HEIGHT_MM * mmToPoints;

        // Create a custom PageLayout for the ID card
        PageLayout pageLayout = printer.createPageLayout(
                Paper.A4,
                PageOrientation.PORTRAIT,
                Printer.MarginType.HARDWARE_MINIMUM
        );

        // Take a high-resolution snapshot of the node
        SnapshotParameters params = new SnapshotParameters();
        params.setTransform(new Scale(2, 2)); // Increase the scale for higher resolution
        WritableImage snapshot = node.snapshot(params, null);

        // Create an ImageView to hold the snapshot
        ImageView imageView = new ImageView(snapshot);
        imageView.setFitWidth(idCardWidthPoints);
        imageView.setFitHeight(idCardHeightPoints);

        // Show the print dialog and print
        boolean userProceeded = printerJob.showPrintDialog(ownerStage);
        boolean success = userProceeded && printerJob.printPage(pageLayout, imageView);

        // End the job if successful
        if (success) {
            printerJob.endJob();
            System.out.println("Printing successful!");
        } else if (userProceeded) {
            System.out.println("Printing failed!");
        } else {
            System.out.println("User canceled the print job!");
        }

        return success;
    }


}
