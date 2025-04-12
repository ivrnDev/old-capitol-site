package com.ivnrdev.connectodo.Mapper;

import com.ivnrdev.connectodo.DTO.Event.EventRequestDTO;
import com.ivnrdev.connectodo.DTO.Event.EventResponseDTO;
import com.ivnrdev.connectodo.Domain.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper implements Mapper<Event, EventRequestDTO, EventResponseDTO> {
    @Override
    public Event toEntity(EventRequestDTO request) {
        return Event.builder()
                .id(request.getId())
                .eventDate(request.getEventDate())
                .eventTime(request.getEventTime())
                .eventType(request.getEventType())
                .status(request.getStatus())
                .applicationType(request.getApplicationType())
                .build();
    }

    @Override
    public EventResponseDTO toRes(Event entity) {
        return EventResponseDTO.builder()
                .id(entity.getId())
                .eventDate(entity.getEventDate())
                .eventTime(entity.getEventTime())
                .eventType(entity.getEventType())
                .status(entity.getStatus())
                .applicationType(entity.getApplicationType())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
