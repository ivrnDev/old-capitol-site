package com.econnect.barangaymanagementapp.repository;

import com.econnect.barangaymanagementapp.config.Config;
import com.econnect.barangaymanagementapp.domain.Event;
import com.econnect.barangaymanagementapp.enumeration.database.Firebase;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EventRepository extends BaseRepository<Event> {
    private final String apiKey = Config.getFirebaseUrl() + Firebase.EVENT.getPath();

    public EventRepository(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
    }

    public Response createEvent(Event event) {
        return create(apiKey + "/" + event.getId(), event);
    }

    public Response updateEvent(Event event) {
        return create(apiKey + "/" + event.getId(), event);

    }

    public Boolean deleteEventById(String id) {
        return deleteById(apiKey, id);
    }

    public Optional<Event> findEventById(String requestId) {
        return findById(apiKey, requestId, new TypeReference<>() {
        });

    }

    public List<Event> findAllEvents() {
        return findAll(apiKey, new TypeReference<>() {
        });
    }

    public List<Event> findEventByFilter(Predicate<Event> predicate) {
        return findAllByFilter(apiKey, new TypeReference<>() {
        }, predicate);
    }

    public Response updateEventByStatus(String requestId, StatusType.EventAppointmentStatus status) {
        return updateBy(apiKey, requestId, new TypeReference<>() {
        }, request -> request.setStatus(status));
    }

    public void enableLiveReload(Consumer<String> handleDataUpdates) {
        enableLiveReload(apiKey, handleDataUpdates, "EVENT:");
    }

}
