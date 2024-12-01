package com.econnect.barangaymanagementapp.repository;

import com.econnect.barangaymanagementapp.config.Config;
import com.econnect.barangaymanagementapp.domain.EventItems;
import com.econnect.barangaymanagementapp.enumeration.database.Firebase;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EventItemsRepository extends BaseRepository<EventItems> {
    private final String apiKey = Config.getFirebaseUrl() + Firebase.EVENT_ITEMS.getPath();

    public EventItemsRepository(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
    }

    public Response createEventItems(EventItems event) {
        return create(apiKey + "/" + event.getId(), event);
    }

    public Response updateEventItems(EventItems event) {
        return create(apiKey + "/" + event.getId(), event);

    }

    public Boolean deleteEventItemsById(String id) {
        return deleteById(apiKey, id);
    }

    public Optional<EventItems> findEventItemsById(String requestId) {
        return findById(apiKey, requestId, new TypeReference<>() {
        });

    }

    public List<EventItems> findAllEventItems() {
        return findAll(apiKey, new TypeReference<>() {
        });
    }

    public List<EventItems> findEventItemsByFilter(Predicate<EventItems> predicate) {
        return findAllByFilter(apiKey, new TypeReference<>() {
        }, predicate);
    }

    public void enableLiveReload(Consumer<String> handleDataUpdates) {
        enableLiveReload(apiKey, handleDataUpdates, "EVENT ITEMS:");
    }

}
