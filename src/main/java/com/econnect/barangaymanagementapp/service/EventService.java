package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Event;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.EventAppointmentStatus;
import com.econnect.barangaymanagementapp.repository.EventRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.EventAppointmentStatus.COMPLETED;
import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.EventAppointmentStatus.PENDING;

public class EventService {
    private final EventRepository eventRepository;

    public EventService(DependencyInjector dependencyInjector) {
        this.eventRepository = dependencyInjector.getEventRepository();
    }

    public Response createEvent(Event event) {
        int baseId = 1000;
        String eventId = event.getId();
        int countOfEvents = findCountOfEventsByResidentId(eventId);
        int autoIncrementId = countOfEvents > 0 ? baseId + countOfEvents : baseId;
        event.setId(event.getId() + "-" + autoIncrementId);
        event.setCreatedAt(ZonedDateTime.now());
        event.setUpdatedAt(ZonedDateTime.now());
        event.setApplicationType(ApplicationType.WALK_IN);
        event.setStatus(EventAppointmentStatus.PENDING);
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
        return eventRepository.updateEventByStatus(requestId, status);
    }

    private String generateReferenceNumber() {
        int OTP_LENGTH = 12;
        SecureRandom random = new SecureRandom();
        long otp = random.nextLong((long) Math.pow(10, OTP_LENGTH));
        return String.format("%012d", otp);
    }

    public void listenToUpdates(Consumer<String> handleDataUpdate) {
        eventRepository.enableLiveReload(handleDataUpdate);
    }
}
