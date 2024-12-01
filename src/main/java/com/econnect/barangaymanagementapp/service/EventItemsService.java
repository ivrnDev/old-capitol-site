package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.EventItems;
import com.econnect.barangaymanagementapp.repository.EventItemsRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class EventItemsService {
    private final EventItemsRepository eventItemsRepository;

    public EventItemsService(DependencyInjector dependencyInjector) {
        this.eventItemsRepository = dependencyInjector.getEventItemsRepository();
    }

    public Response createEventItems(EventItems eventItems) {
        Integer generatedId = generateId(eventItems.getId());
        eventItems.setId(eventItems.getId() + "-" + generatedId);
        System.out.println(eventItems.getId() + "-" + generatedId);
        eventItems.setCreatedAt(ZonedDateTime.now());
        eventItems.setUpdatedAt(ZonedDateTime.now());
        return eventItemsRepository.createEventItems(eventItems);
    }

    public List<EventItems> findAllEventItems() {
        return eventItemsRepository.findAllEventItems();
    }

    public Optional<EventItems> findEventItemsById(String id) {
        return eventItemsRepository.findEventItemsById(id);
    }

    private int findCountOfEventItemsByResidentId(String residentId) {
        return (int) eventItemsRepository.findEventItemsByFilter(request -> request.getId().contains(residentId)).stream().count();
    }

    public int generateId(String residentId) {
        int baseId = 1000;
        int countOfItems = findCountOfEventItemsByResidentId(residentId);
        return countOfItems > 0 ? baseId + countOfItems : baseId;
    }

    private String generateReferenceNumber() {
        int OTP_LENGTH = 12;
        SecureRandom random = new SecureRandom();
        long otp = random.nextLong((long) Math.pow(10, OTP_LENGTH));
        return String.format("%012d", otp);
    }

    public void listenToUpdates(Consumer<String> handleDataUpdate) {
        eventItemsRepository.enableLiveReload(handleDataUpdate);
    }
}
