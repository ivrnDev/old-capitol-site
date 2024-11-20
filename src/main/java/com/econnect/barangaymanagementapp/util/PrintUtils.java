package com.econnect.barangaymanagementapp.util;

import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.type.CertificateType;
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
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
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

    public static void printCertificate(File pdfFile, Stage currentStage, Consumer<Boolean> callback) {
        printPdf(pdfFile, callback, currentStage);
    }

    public static File generateCertificate(String controlNumber, Resident resident, CertificateType certificateType, Consumer<Image> callback) {
        try {
            File generatedPDF = null;
            switch (certificateType) {
                case BARANGAY_CLEARANCE -> generatedPDF = createBarangayClearance(controlNumber, resident);
                case CERTIFICATE_OF_RESIDENCY -> generatedPDF = createCertificateOfResidency(controlNumber, resident);
                case CEDULA -> generatedPDF = createCedulaDocument(controlNumber, resident);
                case CERTIFICATE_OF_INDIGENCY -> generatedPDF = createCertificateOfIndigency(controlNumber, resident);
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
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(50, 750);

                for (XWPFParagraph paragraph : document.getParagraphs()) {
                    for (XWPFRun run : paragraph.getRuns()) {
                        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), run.getFontSize() != -1 ? run.getFontSize() : 12);
                        if (run.isBold()) {
                            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), run.getFontSize() != -1 ? run.getFontSize() : 12);
                        }
                        if (run.isItalic()) {
                            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), run.getFontSize() != -1 ? run.getFontSize() : 12);
                        }
                        String[] lines = run.text().split("\n");
                        for (String line : lines) {
                            contentStream.showText(line);
                            contentStream.newLineAtOffset(0, -15);
                        }
                    }
                    contentStream.newLineAtOffset(0, -15);
                }
                contentStream.endText();
            }
            pdfDocument.save(pdfFile);
        }
        return pdfFile;
    }

    // DOCUMENTS
    public static File createCertificateOfIndigency(String controlNumber, Resident resident) throws IOException {
        XWPFDocument document = new XWPFDocument();

        // Title
        XWPFParagraph title = document.createParagraph();
        XWPFRun titleRun = title.createRun();
        titleRun.setText("Certificate of Indigency");
        titleRun.setBold(true);
        titleRun.setFontSize(20);
        title.setAlignment(ParagraphAlignment.CENTER);

        // Control Number and Date
        XWPFParagraph controlNumberParagraph = document.createParagraph();
        XWPFRun controlNumberRun = controlNumberParagraph.createRun();
        controlNumberRun.setText(String.format("Control Number: %s", controlNumber));
        controlNumberRun.setFontSize(12);
        controlNumberRun.addBreak();
        controlNumberRun.setText(String.format("Date Issued: %s", LocalDate.now()));
        controlNumberParagraph.setAlignment(ParagraphAlignment.LEFT);

        // Body Content
        XWPFParagraph body = document.createParagraph();
        XWPFRun bodyRun = body.createRun();
        bodyRun.setText("TO WHOM IT MAY CONCERN:");
        bodyRun.setFontSize(12);
        bodyRun.addBreak();

        // Footer (Signature)
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

        // Generate PDF from the Word Document
        File pdfFile = createPdfFromDocument(document, controlNumber);

        return pdfFile;
    }

    public static File createBarangayClearance(String controlNumber, Resident resident) throws IOException {
        XWPFDocument document = new XWPFDocument();

        // Title
        XWPFParagraph title = document.createParagraph();
        XWPFRun titleRun = title.createRun();
        titleRun.setText("Barangay Clearance");
        titleRun.setBold(true);
        titleRun.setFontSize(20);
        title.setAlignment(ParagraphAlignment.CENTER);

        // Control Number and Date
        XWPFParagraph controlNumberParagraph = document.createParagraph();
        XWPFRun controlNumberRun = controlNumberParagraph.createRun();
        controlNumberRun.setText(String.format("Control Number: %s", controlNumber));
        controlNumberRun.setFontSize(12);
        controlNumberRun.addBreak();
        controlNumberRun.setText(String.format("Date Issued: %s", LocalDate.now()));
        controlNumberParagraph.setAlignment(ParagraphAlignment.LEFT);

        // Body Content
        XWPFParagraph body = document.createParagraph();
        XWPFRun bodyRun = body.createRun();
        bodyRun.setText("TO WHOM IT MAY CONCERN:");
        bodyRun.setFontSize(12);
        bodyRun.addBreak();

        // Footer (Signature)
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

        // Generate PDF from the Word Document
        File pdfFile = createPdfFromDocument(document, controlNumber);

        return pdfFile;
    }

    public static File createCertificateOfResidency(String controlNumber, Resident resident) throws IOException {
        XWPFDocument document = new XWPFDocument();

        // Title
        XWPFParagraph title = document.createParagraph();
        XWPFRun titleRun = title.createRun();
        titleRun.setText("Certificate of Residency");
        titleRun.setBold(true);
        titleRun.setFontSize(20);
        title.setAlignment(ParagraphAlignment.CENTER);

        // Control Number and Date
        XWPFParagraph controlNumberParagraph = document.createParagraph();
        XWPFRun controlNumberRun = controlNumberParagraph.createRun();
        controlNumberRun.setText(String.format("Control Number: %s", controlNumber));
        controlNumberRun.setFontSize(12);
        controlNumberRun.addBreak();
        controlNumberRun.setText(String.format("Date Issued: %s", LocalDate.now()));
        controlNumberParagraph.setAlignment(ParagraphAlignment.LEFT);

        // Body Content
        XWPFParagraph body = document.createParagraph();
        XWPFRun bodyRun = body.createRun();
        bodyRun.setText("TO WHOM IT MAY CONCERN:");
        bodyRun.setFontSize(12);
        bodyRun.addBreak();

        // Footer (Signature)
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

        // Generate PDF from the Word Document
        File pdfFile = createPdfFromDocument(document, controlNumber);

        return pdfFile;
    }

    public static File createCedulaDocument(String controlNumber, Resident resident) throws IOException {
        XWPFDocument document = new XWPFDocument();

        // Title
        XWPFParagraph title = document.createParagraph();
        XWPFRun titleRun = title.createRun();
        titleRun.setText("Cedula");
        titleRun.setBold(true);
        titleRun.setFontSize(20);
        title.setAlignment(ParagraphAlignment.CENTER);

        // Control Number and Date
        XWPFParagraph controlNumberParagraph = document.createParagraph();
        XWPFRun controlNumberRun = controlNumberParagraph.createRun();
        controlNumberRun.setText(String.format("Control Number: %s", controlNumber));
        controlNumberRun.setFontSize(12);
        controlNumberRun.addBreak();
        controlNumberRun.setText(String.format("Date Issued: %s", LocalDate.now()));
        controlNumberParagraph.setAlignment(ParagraphAlignment.LEFT);

        // Body Content
        XWPFParagraph body = document.createParagraph();
        XWPFRun bodyRun = body.createRun();
        bodyRun.setText("TO WHOM IT MAY CONCERN:");
        bodyRun.setFontSize(12);
        bodyRun.addBreak();

        // Footer (Signature)
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

        // Generate PDF from the Word Document
        File pdfFile = createPdfFromDocument(document, controlNumber);

        return pdfFile;
    }


}
