package com.econnect.barangaymanagementapp.controller.detail;

import com.econnect.barangaymanagementapp.controller.base.BaseViewController;
import com.econnect.barangaymanagementapp.domain.DepartmentRequest;
import com.econnect.barangaymanagementapp.service.DepartmentRequestService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.function.Consumer;

public class ViewDepartmentRequestController implements BaseViewController {
    @FXML
    private ImageView closeBtn, documentPreview;
    @FXML
    private HBox documentPreviewContainer;
    @FXML
    private TextField requestIdInput, nameInput, emailInput, departmentInput, typeInput, statusInput, dateInput, timeInput;
    @FXML
    private TextArea commentInput;
    @FXML
    private Button printBtn;

    private final ModalUtils modalUtils;
    private final DepartmentRequestService departmentRequestService;
    private Image documentImage;
    private Stage currentStage;
    private String requestId;

    public ViewDepartmentRequestController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.departmentRequestService = dependencyInjector.getDepartmentRequestService();
        Platform.runLater(() -> currentStage = (Stage) closeBtn.getScene().getWindow());
    }

    public void initialize() {
        closeBtn.setOnMouseClicked(_ -> closeView());
//        printBtn.setOnMouseClicked(_ -> handlePrint());
        Platform.runLater(this::fetchData);
    }

//    private void handlePrint() {
//        certificateService.printCertificate(generatedPdfFile, currentStage, success -> {
//            if (success) {
//                callback.accept(true);
//                closeView();
//            }
//        });
//    }

    private void displayAttachment(String fileUrl) {
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(documentPreviewContainer.getWidth(), documentPreviewContainer.getHeight());
        addLoadingIndicator(loadingIndicator);
        Task<Image> requestTask = new Task<>() {
            @Override
            protected Image call() throws Exception {
                return departmentRequestService.convertWordDocumentToImage(fileUrl);
            }

            @Override
            protected void succeeded() {
                removeLoadingIndicator(loadingIndicator);
                Image image = getValue();
                documentPreview.setImage(image);
                documentPreview.setCursor(javafx.scene.Cursor.HAND);
                documentPreview.setOnMouseClicked(_ -> modalUtils.showImageView(image, currentStage));
                printBtn.setDisable(false);
            }

            @Override
            protected void failed() {
                System.out.println("Failed to generate certificate: " + getException().getMessage());
                removeLoadingIndicator(loadingIndicator);
            }
        };
        new Thread(requestTask).start();
    }

    private void fetchData() {
        Task<Optional<DepartmentRequest>> requestTask = new Task<>() {
            @Override
            protected Optional<DepartmentRequest> call() {
                return departmentRequestService.findDepartmentRequestById(requestId);
            }

            @Override
            protected void succeeded() {
                Optional<DepartmentRequest> fetchedRequest = getValue();
                if (fetchedRequest.isPresent()) {
                    DepartmentRequest request = fetchedRequest.get();
                    populateRequestData(request);
                    displayAttachment(request.getFileURL());
                }
            }

            @Override
            protected void failed() {
                System.out.println("Failed to fetch data: " + getException().getMessage());
            }
        };

        new Thread(requestTask).start();
    }

    private void populateRequestData(DepartmentRequest request) {
        if (request == null) return;
        requestIdInput.setText(request.getId());
        nameInput.setText(request.getName());
        emailInput.setText(request.getEmail());
        departmentInput.setText("Health Department");
        typeInput.setText(request.getType());
        statusInput.setText(request.getStatus());
        commentInput.setText(request.getComment());
        ZonedDateTime createdAt = DateFormatter.convertTimestampToZonedDateTime(request.getTimestamp());
        dateInput.setText(DateFormatter.formatDateToLongStyle(createdAt));
        timeInput.setText(DateFormatter.formatTimeTo12HourStyle(createdAt));
    }

    private void addLoadingIndicator(StackPane loadingIndicator) {
        documentPreview.setManaged(false);
        documentPreview.setVisible(false);
        documentPreviewContainer.getChildren().add(loadingIndicator);
    }

    private void removeLoadingIndicator(StackPane loadingIndicator) {
        documentPreview.setManaged(true);
        documentPreview.setVisible(true);
        documentPreviewContainer.getChildren().remove(loadingIndicator);
    }

    private void closeView() {
        modalUtils.closeCustomizeModal();
    }

    @Override
    public void setId(String id) {
        this.requestId = id;
    }
}
