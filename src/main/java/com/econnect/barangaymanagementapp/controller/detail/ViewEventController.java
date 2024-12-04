package com.econnect.barangaymanagementapp.controller.detail;

import com.econnect.barangaymanagementapp.controller.base.BaseViewController;
import com.econnect.barangaymanagementapp.domain.Event;
import com.econnect.barangaymanagementapp.domain.EventItems;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.database.Firestore;
import com.econnect.barangaymanagementapp.service.EventItemsService;
import com.econnect.barangaymanagementapp.service.EventService;
import com.econnect.barangaymanagementapp.service.ImageService;
import com.econnect.barangaymanagementapp.service.ResidentService;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.resource.ImageUtils;
import com.econnect.barangaymanagementapp.util.ui.LoadingIndicator;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class ViewEventController implements BaseViewController {
    @FXML
    private ImageView closeBtn;
    @FXML
    private ImageView profilePicture;
    @FXML
    private HBox profileContainer;
    @FXML
    private TextField residentIdInput, fullNameInput, emailInput, contactNumberInput, typeInput, eventPlaceInput, eventDateInput, eventTimeInput, dateInput, timeInput;
    @FXML
    private TextArea purposeInput;
    @FXML
    private VBox itemContainer;

    private final ModalUtils modalUtils;
    private final ResidentService residentService;
    private final EventService eventService;
    private final EventItemsService eventItemsService;
    private final ImageService imageService;
    private Image profilePictureImage;
    private Stage currentStage;
    private String eventId;

    public ViewEventController(DependencyInjector dependencyInjector) {
        this.modalUtils = dependencyInjector.getModalUtils();
        this.residentService = dependencyInjector.getResidentService();
        this.eventService = dependencyInjector.getEventService();
        this.imageService = dependencyInjector.getImageService();
        this.eventItemsService = dependencyInjector.getEventItemsService();
        Platform.runLater(() -> currentStage = (Stage) closeBtn.getScene().getWindow());
    }

    public void initialize() {
        closeBtn.setOnMouseClicked(_ -> closeView());
        Platform.runLater(this::fetchData);
        setupViewImage();
    }

    private void fetchData() {
        Task<Optional<Event>> requestTask = new Task<>() {
            @Override
            protected Optional<Event> call() {
                return eventService.findEventById(eventId);
            }

            @Override
            protected void succeeded() {
                Optional<Event> eventValue = getValue();
                if (eventValue.isPresent()) {
                    Event event = eventValue.get();
                    populateRequestData(event);
                    eventItemsService.findAllEventItemsByEventId(event.getId()).forEach(item -> addItemRow(item.getItemName(), item.getQuantity()));
                }
            }

            @Override
            protected void failed() {
                System.out.println("Failed to fetch data: " + getException().getMessage());
            }
        };

        Task<Optional<Resident>> fetchResident = new Task<>() {
            @Override
            protected Optional<Resident> call() {
                return residentService.findResidentById(eventId.substring(0, eventId.length() - 5));
            }

            @Override
            protected void succeeded() {
                Optional<Resident> residentValue = getValue();
                if (residentValue.isPresent()) {
                    Resident resident = residentValue.get();
                    populateResidentData(resident);
                    loadProfileImage(Firestore.PROFILE_PICTURE.getPath(), resident.getProfileUrl());
                }
            }

            @Override
            protected void failed() {
                System.out.println("Failed to fetch data: " + getException().getMessage());
            }
        };

        new Thread(fetchResident).start();
        new Thread(requestTask).start();
    }

    private void populateRequestData(Event event) {
        if (event == null) return;
        residentIdInput.setText(event.getId().substring(0, event.getId().length() - 5));
        purposeInput.setText(event.getPurpose());
        typeInput.setText(event.getEventType());
        eventPlaceInput.setText(event.getEventPlace());
        eventDateInput.setText(DateFormatter.formatToLongDate(event.getEventDate()));
        eventTimeInput.setText(event.getEventTime());
        dateInput.setText(DateFormatter.formatDateToLongStyle(event.getCreatedAt()));
        timeInput.setText(DateFormatter.formatTimeTo12HourStyle(event.getCreatedAt()));
    }

    private void populateResidentData(Resident resident) {
        if (resident == null) return;
        String fullName = resident.getLastName() + ", " + resident.getFirstName() + " " + resident.getMiddleName();
        fullNameInput.setText(fullName);
        emailInput.setText(resident.getEmail());
        contactNumberInput.setText(resident.getMobileNumber());
    }

    private void loadProfileImage(String directory, String link) {
        profilePicture.setOnMouseClicked(_ -> modalUtils.showImageView(profilePicture.getImage(), currentStage));
        profilePicture.setCursor(Cursor.HAND);
        profilePicture.setVisible(false);
        profilePicture.setManaged(false);
        StackPane loadingIndicator = LoadingIndicator.createLoadingIndicator(profileContainer.getWidth(), profileContainer.getHeight());
        Platform.runLater(() -> profileContainer.getChildren().add(loadingIndicator));

        Runnable call = () -> {
            profilePictureImage = imageService.getImage(directory, link);
            Platform.runLater(() -> {
                profileContainer.getChildren().remove(loadingIndicator);
                profilePicture.setImage(profilePictureImage);
                profilePicture.setVisible(true);
                profilePicture.setManaged(true);
            });
        };

        Runnable onFailed = () -> {
            Platform.runLater(() -> profileContainer.getChildren().remove(loadingIndicator));
            profilePicture.setVisible(true);
            profilePicture.setManaged(true);
            System.err.println("Error loading employees");
        };
        LoadingIndicator.executeWithLoadingIndicator(loadingIndicator, call, onFailed);
    }

    private void setupViewImage() {
        Platform.runLater(() -> ImageUtils.setRoundedClip(profilePicture, 25, 25));
    }

    private void addItemRow(String itemName, String quantity) {
        HBox itemRow = new HBox(30);
        itemRow.setAlignment(Pos.CENTER);
        itemRow.setMinHeight(30.0);
        itemRow.getStyleClass().add("border");

        Label itemLabel = new Label(itemName);
        itemLabel.setTextOverrun(OverrunStyle.CLIP);
        itemLabel.setFont(new Font("Arial", 15.0));

        Label quantityLabel = new Label("x" + quantity);
        quantityLabel.setTextOverrun(OverrunStyle.CLIP);
        quantityLabel.setFont(new Font("Arial", 15.0));

        itemRow.getChildren().addAll(itemLabel, quantityLabel);
        itemContainer.getChildren().add(itemRow);
    }

    @FXML
    private void closeView() {
        modalUtils.closeCustomizeModal();
    }

    @Override
    public void setId(String id) {
        this.eventId = id;
    }
}
