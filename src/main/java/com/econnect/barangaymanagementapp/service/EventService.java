package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Event;
import com.econnect.barangaymanagementapp.domain.EventItems;
import com.econnect.barangaymanagementapp.domain.Inventory;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.EventAppointmentStatus;
import com.econnect.barangaymanagementapp.repository.EventRepository;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.econnect.barangaymanagementapp.util.StatusUtils;
import okhttp3.Response;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.EventAppointmentStatus.*;

public class EventService {
    private final EventRepository eventRepository;
    private final EventItemsService eventItemsService;
    private final InventoryService inventoryService;
    private final EmailService emailService;
    private final ResidentService residentService;

    public EventService(DependencyInjector dependencyInjector) {
        this.eventRepository = dependencyInjector.getEventRepository();
        this.eventItemsService = new EventItemsService(dependencyInjector);
        this.inventoryService = new InventoryService(dependencyInjector);
        this.emailService = dependencyInjector.getEmailService();
        this.residentService = dependencyInjector.getResidentService();
    }

    public Response createEvent(Event event, List<EventItems> eventItems) {
        int baseId = 1000;
        String residentId = event.getId();
        int countOfEvents = findCountOfEventsByResidentId(residentId);
        int autoIncrementId = countOfEvents > 0 ? baseId + countOfEvents : baseId;
        String generatedEventId = event.getId() + "-" + autoIncrementId;

        event.setId(generatedEventId);
        event.setCreatedAt(ZonedDateTime.now());
        event.setUpdatedAt(ZonedDateTime.now());
        event.setApplicationType(ApplicationType.WALK_IN);
        event.setStatus(EventAppointmentStatus.PENDING);

        eventItems.forEach(eventItem -> {
            eventItem.setId(residentId);
            eventItem.setEventId(generatedEventId);
            addItems(eventItem);

        });

        return eventRepository.createEvent(event);
    }

    public List<Event> findAllEvents() {
        return eventRepository.findAllEvents();
    }

    public List<Event> findAllPendingEvents() {
        return eventRepository.findEventByFilter(request -> request.getStatus().equals(PENDING));
    }

    public Optional<Event> findEventById(String id) {
        return eventRepository.findEventById(id);
    }

    private int findCountOfEventsByResidentId(String residentId) {
        return (int) eventRepository.findEventByFilter(request -> request.getId().contains(residentId)).stream().count();
    }

    public Optional<Event> findCompletedEvent(String id) {
        return eventRepository.findEventById(id).filter(request -> request.getStatus().equals(COMPLETED));
    }

    public Response updateEventByStatus(String requestId, EventAppointmentStatus status) {
        Event event = findEventById(requestId).get();
        if (status.equals(IN_PROGRESS)) {
            List<EventItems> items = eventItemsService.findAllEventItemsByEventId(requestId);
            items.forEach(item -> {
                Inventory inventory = inventoryService.findInventoryById(item.getItemId()).get();
                int newStock = Integer.parseInt(inventory.getStocks()) - Integer.parseInt(item.getQuantity());
                if (newStock < 0) {
                    throw new IllegalArgumentException("Insufficient stocks for item");
                }
                inventory.setStocks(String.valueOf(newStock));
                inventory.setUpdatedAt(ZonedDateTime.now());
                inventoryService.updateInventory(inventory);
            });
        }

        if (status.equals(WAITING) && event.getApplicationType().equals(ApplicationType.ONLINE)) {
            Resident resident = residentService.findResidentById(event.getId().substring(0, 12)).get();
            sendReleaseEmail(resident, event);
        }

        if (status.equals(COMPLETED) || status.equals(CANCELLED)) {
            List<EventItems> items = eventItemsService.findAllEventItemsByEventId(requestId);
            items.forEach(item -> {
                Inventory inventory = inventoryService.findInventoryById(item.getItemId()).get();
                inventory.setStocks(String.valueOf(Integer.parseInt(inventory.getStocks()) + Integer.parseInt(item.getQuantity())));
                inventory.setUpdatedAt(ZonedDateTime.now());
                inventoryService.updateInventory(inventory);
            });
        }

        return eventRepository.updateEventByStatus(requestId, status);
    }

    private void addItems(EventItems eventItems) {
        eventItemsService.createEventItems(eventItems);
    }

    // Analytics
    public int totalEvents() {
        return eventRepository.findAllEvents().size();
    }

    public int totalPendingEvents() {
        return eventRepository.findEventByFilter(request -> request.getStatus().equals(PENDING)).size();
    }

    public int todayTotalEventRequests() {
        return eventRepository.findEventByFilter(request -> request.getCreatedAt().toLocalDate().equals(ZonedDateTime.now().toLocalDate())).size();
    }

    public int todayProcessingEvents() {
        return eventRepository.findEventByFilter(request -> StatusUtils.PROCESSING_EVENTS.contains(request.getStatus())
                && request.getCreatedAt().toLocalDate().equals(ZonedDateTime.now().toLocalDate())).size();
    }

    public int totalCompletedEvents() {
        return eventRepository.findEventByFilter(request -> request.getStatus().equals(COMPLETED)).size();
    }


    public void listenToUpdates(Consumer<String> handleDataUpdate) {
        eventRepository.enableLiveReload(handleDataUpdate);
    }

    private void sendReleaseEmail(Resident resident, Event event) {
        emailService.sendEmailAsync(resident.getEmail(), "Your Requested Event has been Accepted", String.format("""
                        Dear %s,

                        We are pleased to inform you that your request for an event "%s" scheduled on %s at %s has been accepted and is now preparing .
                                                    
                        Please visit the barangay hall at Old Capitol Site to collect your event items. Our office hours are from 8:00 AM to 5:00 PM, Monday to Friday.
                                                    
                        If you have any questions or need further assistance, feel free to reach out to our office.
                                                        
                        Best regards,
                        Old Capitol Site
                        """,
                resident.getFirstName(),
                event.getEventType(),
                DateFormatter.formatToLongDate(event.getEventDate()),
                event.getEventTime()

        ));
    }
}
