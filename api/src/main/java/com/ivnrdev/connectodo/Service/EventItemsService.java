package com.ivnrdev.connectodo.Service;

import com.ivnrdev.connectodo.Domain.EventItems;
import com.ivnrdev.connectodo.Repository.EventItemsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventItemsService {
    private final EventItemsRepository eventItemsRepository;

    public EventItems saveEventItems(EventItems eventItems) {
        return eventItemsRepository.save(eventItems);
    }

    public List<EventItems> getAllEventItemss() {
        return StreamSupport.stream(eventItemsRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public EventItems getEventItemsById(Long id) {
        return eventItemsRepository.findById(id).orElseThrow(() -> new RuntimeException("EventItems not found"));
    }

    public EventItems deleteEventItemsById(Long id) {
        EventItems existingEventItems = eventItemsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EventItems not found"));

        eventItemsRepository.delete(existingEventItems);
        return existingEventItems;
    }

    public EventItems updateEventItems(EventItems eventItems) {
        EventItems existingEventItems = eventItemsRepository.findById(eventItems.getId())
                .orElseThrow(() -> new RuntimeException("EventItems not found"));

        BeanUtils.copyProperties(eventItems, existingEventItems, getNullPropertyNames(eventItems));
        return eventItemsRepository.save(existingEventItems);
    }

    public EventItems updateEventItemsById(Long id, EventItems updatedEventItems) {
        EventItems existingEventItems = eventItemsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EventItems not found"));

        BeanUtils.copyProperties(updatedEventItems, existingEventItems, getNullPropertyNames(updatedEventItems));
        return eventItemsRepository.save(existingEventItems);
    }

    private String[] getNullPropertyNames(EventItems source) {
        return Arrays.stream(EventItems.class.getDeclaredFields())
                .filter(field -> {
                    try {
                        field.setAccessible(true);
                        return field.get(source) == null;
                    } catch (IllegalAccessException e) {
                        return true;
                    }
                })
                .map(Field::getName)
                .toArray(String[]::new);
    }

}
