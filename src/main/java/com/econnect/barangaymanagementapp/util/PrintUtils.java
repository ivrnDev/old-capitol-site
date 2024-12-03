package com.econnect.barangaymanagementapp.util;

import com.econnect.barangaymanagementapp.MainApplication;
import com.econnect.barangaymanagementapp.domain.Certificate;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.type.CertificateType;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;

import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.function.Consumer;

public class PrintUtils {
    private static final double ID_CARD_WIDTH_MM = 85.60;
    private static final double ID_CARD_HEIGHT_MM = 53.98 * 2;

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

        ImageView imageView = renderNodeAsImage(node, idCardWidthPoints, idCardHeightPoints);

        boolean userProceeded = printerJob.showPrintDialog(ownerStage);
        boolean success = userProceeded && printerJob.printPage(pageLayout, imageView);

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

    private ImageView renderNodeAsImage(Node node, double width, double height) {
        SnapshotParameters params = new SnapshotParameters();
        params.setTransform(new Scale(2, 2)); // Increase the scale for higher resolution
        WritableImage snapshot = node.snapshot(params, null);

        ImageView imageView = new ImageView(snapshot);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);

        return imageView;
    }

    public static void printDocumentFile(File pdfFile, Stage currentStage, Consumer<Boolean> callback) {
        printPdf(pdfFile, callback, currentStage);
    }

    public static File generateCertificate(String controlNumber, Resident resident, Certificate certificate, Consumer<Image> callback) {
        CertificateType certificateType = CertificateType.fromName(certificate.getRequest());
        try {
            File generatedPDF = null;
            switch (certificateType) {
                case BARANGAY_CLEARANCE -> generatedPDF = createBarangayClearance(controlNumber, resident, certificate);
                case CERTIFICATE_OF_RESIDENCY ->
                        generatedPDF = createCertificateOfResidency(controlNumber, resident, certificate);
                case CERTIFICATE_OF_INDIGENCY ->
                        generatedPDF = createCertificateOfIndigency(controlNumber, resident, certificate);
            }
            callback.accept(convertPdfToImage(generatedPDF, 0));
            return generatedPDF;
        } catch (IOException e) {
            e.printStackTrace();
            callback.accept(null);
        }
        return null;
    }

    public static void printPdf(File pdfFile, Consumer<Boolean> callback, Window owner) {
        try (PDDocument pdfDocument = Loader.loadPDF(pdfFile)) {
            PrinterJob fxPrinterJob = PrinterJob.createPrinterJob();

            if (fxPrinterJob != null && fxPrinterJob.showPrintDialog(owner)) {
                java.awt.print.PrinterJob awtPrinterJob = java.awt.print.PrinterJob.getPrinterJob();
                PageFormat pageFormat = awtPrinterJob.defaultPage();
                java.awt.print.Paper paper = pageFormat.getPaper();

                paper.setSize(595, 842);
                paper.setImageableArea(0, 0, 595, 842);

                pageFormat.setPaper(paper);

                awtPrinterJob.setPrintable(new PDFPrintable(pdfDocument, Scaling.SHRINK_TO_FIT), pageFormat);

                try {
                    awtPrinterJob.print();
                    callback.accept(true);
                    System.out.println("Print job completed successfully.");
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.accept(false);
                    System.out.println("Print job failed.");
                }
            } else {
                callback.accept(false);
                System.out.println("Print job was cancelled.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.accept(false); // Notify an error occurred
            System.out.println("An error occurred while printing.");
        }
    }

    public static Image convertPdfToImage(File pdfFile, int pageIndex) throws IOException {
        try (PDDocument pdfDocument = Loader.loadPDF(pdfFile)) {
            PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageIndex, 300);
            WritableImage fxImage = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
            PixelWriter pixelWriter = fxImage.getPixelWriter();

            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                for (int x = 0; x < bufferedImage.getWidth(); x++) {
                    pixelWriter.setArgb(x, y, bufferedImage.getRGB(x, y));
                }
            }
            return fxImage;
        }
    }

    public static File createPdfFromDocument(XWPFDocument document, String controlNumber) throws IOException {
        File pdfFile = File.createTempFile("certificate" + controlNumber, ".pdf");
        pdfFile.deleteOnExit();
        try (PDDocument pdfDocument = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            pdfDocument.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page)) {
                for (XWPFPictureData pictureData : document.getAllPictures()) {
                    PDImageXObject pdImage = PDImageXObject.createFromByteArray(pdfDocument, pictureData.getData(), pictureData.getFileName());
                    PDRectangle mediaBox = page.getMediaBox();
                    contentStream.drawImage(pdImage, 0, 0, mediaBox.getWidth(), mediaBox.getHeight());
                }

                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);

                PDRectangle mediaBox = page.getMediaBox();
                float centerX = (float) (mediaBox.getWidth() / 3.3);
                float rightY = mediaBox.getHeight() - 145; // Adjust the value as needed

                contentStream.newLineAtOffset(centerX, rightY);

                float maxWidth = (float) (mediaBox.getWidth() / 1.5); // Set the maximum width for the lines

                for (XWPFParagraph paragraph : document.getParagraphs()) {
                    for (XWPFRun run : paragraph.getRuns()) {
                        // Set the font size
                        int fontSize = run.getFontSize() != -1 ? run.getFontSize() : 12;
                        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                        // Check for bold and italic formatting
                        if (run.isBold() && run.isItalic()) {
                            font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD_OBLIQUE);
                        } else if (run.isBold()) {
                            font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                        } else if (run.isItalic()) {
                            font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);
                        }

                        contentStream.setFont(font, fontSize);

                        // Split the text into lines and show each line
                        String[] lines = run.text().split("\n");
                        for (String line : lines) {
                            float textWidth = font.getStringWidth(line) / 1000 * fontSize;
                            if (textWidth > maxWidth) {
                                // Split the line into multiple lines
                                String[] words = line.split(" ");
                                StringBuilder currentLine = new StringBuilder();
                                for (String word : words) {
                                    if (font.getStringWidth(currentLine + word) / 1000 * fontSize > maxWidth) {
                                        contentStream.showText(currentLine.toString());
                                        contentStream.newLineAtOffset(0, -15);
                                        currentLine = new StringBuilder(word + " ");
                                    } else {
                                        currentLine.append(word).append(" ");
                                    }
                                }
                                contentStream.showText(currentLine.toString().trim());
                            } else {
                                contentStream.showText(line);
                            }
                            contentStream.newLineAtOffset(0, -15);
                        }
                    }
                    contentStream.newLineAtOffset(0, -15);
                }
                contentStream.endText();
            }
            pdfDocument.save(pdfFile);
            return pdfFile;
        }
    }

    // DOCUMENTS
    public static File convertWordDocumentToImage(String documentUrl, Consumer<Image> callback) throws Exception {
        InputStream inputStream = new URL(documentUrl).openStream();
        XWPFDocument document = new XWPFDocument(inputStream);

        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        try (PDDocument pdfDocument = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            pdfDocument.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page)) {
                for (XWPFParagraph paragraph : document.getParagraphs()) {
                    contentStream.beginText();
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                    contentStream.newLineAtOffset(50, 750);
                    contentStream.showText(paragraph.getText());
                    contentStream.endText();
                }
            }

            pdfDocument.save(pdfOutputStream);
        }

        File pdfFile = File.createTempFile("document", ".pdf");
        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            fos.write(pdfOutputStream.toByteArray());
        }

        Image image = PrintUtils.convertPdfToImage(pdfFile, 0);
        callback.accept(image);

        return pdfFile;
    }

    public static File createCertificateOfIndigency(String controlNumber, Resident resident, Certificate certificate) throws IOException {
        InputStream imageStream = MainApplication.class.getResourceAsStream("images/indigency.jpg");
        if (imageStream == null) {
            throw new FileNotFoundException("Image not found: /images/indigency.jpg");
        }
        XWPFDocument document = new XWPFDocument();

        XWPFParagraph imageParagraph = document.createParagraph();
        imageParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun imageRun = imageParagraph.createRun();
        try {
            imageRun.addPicture(
                    imageStream,
                    XWPFDocument.PICTURE_TYPE_JPEG,
                    "indigency.jpg",
                    Units.toEMU(500), // Image width
                    Units.toEMU(200)  // Image height
            );
        } catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
            throw new RuntimeException("Invalid image format", e);
        } catch (IOException e) {
            throw new RuntimeException("Error loading image", e);
        }

        XWPFParagraph controlNumberParagraph = document.createParagraph();
        controlNumberParagraph.setAlignment(ParagraphAlignment.RIGHT);
        controlNumberParagraph.setVerticalAlignment(TextAlignment.CENTER);
        XWPFRun controlNumberRun = controlNumberParagraph.createRun();
        controlNumberRun.setText("Control Number: " + controlNumber);
        controlNumberRun.setFontSize(12);
        controlNumberRun.addBreak();
        controlNumberRun.setText("Date Issued: " + LocalDate.now());

        XWPFParagraph body = document.createParagraph();
        body.setAlignment(ParagraphAlignment.RIGHT);
        body.setVerticalAlignment(TextAlignment.CENTER);
        XWPFRun bodyRun = body.createRun();

        String text = "TO WHOM IT MAY CONCERN:\n\n" +
                "       This is to certify that " + resident.getLastName() + ", " + resident.getFirstName() + " " + resident.getMiddleName() +
                ", of legal age, " + resident.getCivilStatus() + ", and a resident of " + resident.getAddress() +
                ", is a bonafide resident of Barangay Old Capitol Site, Municipality of Quezon City, Province of Metro Manila.\n\n" +
                "       This certification is issued upon the request of the above mentioned person for the purpose of " + certificate.getPurpose() + ".\n\n" +
                "       This further certifies that the individual has no derogatory record or any pending case in this barangay as of the date of issuance.\n\n" +
                "       Issued this " + LocalDate.now().getDayOfMonth() + " day of " + LocalDate.now().getMonth() +
                ", " + LocalDate.now().getYear() + ", at Barangay Old Capitol Site, Municipality of Quezon City, Province of Metro Manila.";
        bodyRun.setText(text);

        File pdfFile = createPdfFromDocument(document, controlNumber);

        return pdfFile;
    }

    public static File createBarangayClearance(String controlNumber, Resident resident, Certificate certificate) throws IOException {

        InputStream imageStream = MainApplication.class.getResourceAsStream("images/clearance.jpg");
        if (imageStream == null) {
            throw new FileNotFoundException("Image not found: /images/clearance.jpg");
        }
        XWPFDocument document = new XWPFDocument();

        XWPFParagraph imageParagraph = document.createParagraph();
        imageParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun imageRun = imageParagraph.createRun();
        try {
            imageRun.addPicture(
                    imageStream,
                    XWPFDocument.PICTURE_TYPE_JPEG,
                    "clearance.jpg",
                    Units.toEMU(500), // Image width
                    Units.toEMU(200)  // Image height
            );
        } catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
            throw new RuntimeException("Invalid image format", e);
        } catch (IOException e) {
            throw new RuntimeException("Error loading image", e);
        }

        XWPFParagraph title = document.createParagraph();
        XWPFRun titleRun = title.createRun();
        titleRun.setText("Barangay Clearance");
        titleRun.setBold(true);
        titleRun.setFontSize(20);
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFParagraph controlNumberParagraph = document.createParagraph();
        controlNumberParagraph.setAlignment(ParagraphAlignment.RIGHT);
        controlNumberParagraph.setVerticalAlignment(TextAlignment.CENTER);
        XWPFRun controlNumberRun = controlNumberParagraph.createRun();
        controlNumberRun.setText("Control Number: " + controlNumber);
        controlNumberRun.setFontSize(12);
        controlNumberRun.addBreak();
        controlNumberRun.setText("Date Issued: " + LocalDate.now());

        // Body Content
        XWPFParagraph body = document.createParagraph();
        body.setAlignment(ParagraphAlignment.RIGHT);
        body.setVerticalAlignment(TextAlignment.CENTER);
        XWPFRun bodyRun = body.createRun();

        String text = "TO WHOM IT MAY CONCERN:\n\n" +
                "       This is to certify that " + resident.getLastName() + ", " + resident.getFirstName() + " " + resident.getMiddleName() +
                ", of legal age, " + resident.getCivilStatus() + ", and a resident of " + resident.getAddress() +
                ", is a bonafide resident of Barangay Old Capitol Site, Municipality of Quezon City, Province of Metro Manila.\n\n" +
                "       This certification is issued upon the request of the above mentioned person for the purpose of " + certificate.getPurpose() + ".\n\n" +
                "       This further certifies that the individual has no derogatory record or any pending case in this barangay as of the date of issuance.\n\n" +
                "       Issued this " + LocalDate.now().getDayOfMonth() + " day of " + LocalDate.now().getMonth() +
                ", " + LocalDate.now().getYear() + ", at Barangay Old Capitol Site, Municipality of Quezon City, Province of Metro Manila.";
        bodyRun.setText(text);

        XWPFParagraph signature = document.createParagraph();
        XWPFRun signatureRun = signature.createRun();
        signatureRun.addBreak();
        signatureRun.addBreak();
        signatureRun.setText("_________________________");
        signatureRun.addBreak();
        signatureRun.setText("[Name of Barangay Captain]");
        signatureRun.addBreak();
        signatureRun.setText("Barangay Captain");
        signature.setAlignment(ParagraphAlignment.CENTER);

        File pdfFile = createPdfFromDocument(document, controlNumber);

        return pdfFile;
    }

    public static File createCertificateOfResidency(String controlNumber, Resident resident, Certificate certificate) throws IOException {
        InputStream imageStream = MainApplication.class.getResourceAsStream("images/residency.jpg");
        if (imageStream == null) {
            throw new FileNotFoundException("Image not found: /images/residency.jpg");
        }
        XWPFDocument document = new XWPFDocument();

        XWPFParagraph imageParagraph = document.createParagraph();
        imageParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun imageRun = imageParagraph.createRun();
        try {
            imageRun.addPicture(
                    imageStream,
                    XWPFDocument.PICTURE_TYPE_JPEG,
                    "residency.jpg",
                    Units.toEMU(500),
                    Units.toEMU(200)
            );
        } catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
            throw new RuntimeException("Invalid image format", e);
        } catch (IOException e) {
            throw new RuntimeException("Error loading image", e);
        }

        XWPFParagraph title = document.createParagraph();
        XWPFRun titleRun = title.createRun();
        titleRun.setText("Certificate of Residency");
        titleRun.setBold(true);
        titleRun.setFontSize(20);
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFParagraph controlNumberParagraph = document.createParagraph();
        controlNumberParagraph.setAlignment(ParagraphAlignment.RIGHT);
        controlNumberParagraph.setVerticalAlignment(TextAlignment.CENTER);
        XWPFRun controlNumberRun = controlNumberParagraph.createRun();
        controlNumberRun.setText("Control Number: " + controlNumber);
        controlNumberRun.setFontSize(12);
        controlNumberRun.addBreak();
        controlNumberRun.setText("Date Issued: " + LocalDate.now());

        XWPFParagraph body = document.createParagraph();
        body.setAlignment(ParagraphAlignment.RIGHT);
        body.setVerticalAlignment(TextAlignment.CENTER);
        XWPFRun bodyRun = body.createRun();

        String text = "TO WHOM IT MAY CONCERN:\n\n" +
                "       This is to certify that " + resident.getLastName() + ", " + resident.getFirstName() + " " + resident.getMiddleName() +
                ", of legal age, " + resident.getCivilStatus() + ", and a resident of " + resident.getAddress() +
                ", is a bonafide resident of Barangay Old Capitol Site, Municipality of Quezon City, Province of Metro Manila.\n\n" +
                "       This certification is issued upon the request of the above mentioned person for the purpose of " + certificate.getPurpose() + ".\n\n" +
                "       This further certifies that the individual has no derogatory record or any pending case in this barangay as of the date of issuance.\n\n" +
                "       Issued this " + LocalDate.now().getDayOfMonth() + " day of " + LocalDate.now().getMonth() +
                ", " + LocalDate.now().getYear() + ", at Barangay Old Capitol Site, Municipality of Quezon City, Province of Metro Manila.";
        bodyRun.setText(text);

        File pdfFile = createPdfFromDocument(document, controlNumber);

        return pdfFile;
    }
}
