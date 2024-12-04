package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Event;
import com.econnect.barangaymanagementapp.domain.EventItems;
import com.econnect.barangaymanagementapp.domain.Inventory;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.EventAppointmentStatus;
import com.econnect.barangaymanagementapp.repository.EventItemsRepository;
import com.econnect.barangaymanagementapp.repository.EventRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.EventAppointmentStatus.*;

public class EventService {
    private final EventRepository eventRepository;
    private final EventItemsService eventItemsService;
    private final InventoryService inventoryService;

    public EventService(DependencyInjector dependencyInjector) {
        this.eventRepository = dependencyInjector.getEventRepository();
        this.eventItemsService = new EventItemsService(dependencyInjector);
        this.inventoryService = new InventoryService(dependencyInjector);
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


    public void listenToUpdates(Consumer<String> handleDataUpdate) {
        eventRepository.enableLiveReload(handleDataUpdate);
    }
}
