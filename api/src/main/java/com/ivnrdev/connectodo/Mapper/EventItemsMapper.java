package com.ivnrdev.connectodo.Mapper;

import com.ivnrdev.connectodo.DTO.Event.EventItemsRequestDTO;
import com.ivnrdev.connectodo.DTO.Event.EventItemsResponseDTO;
import com.ivnrdev.connectodo.Domain.EventItems;
import org.springframework.stereotype.Component;

@Component
public class EventItemsMapper implements Mapper<EventItems, EventItemsRequestDTO, EventItemsResponseDTO> {

    @Override
    public EventItems toEntity(EventItemsRequestDTO request) {
        return EventItems.builder()
                .eventId(request.getEventId())
                .itemId(request.getItemId())
                .itemName(request.getItemName())
                .quantity(request.getQuantity())
                .build();
    }

    @Override
    public EventItemsResponseDTO toRes(EventItems entity) {
        return EventItemsResponseDTO.builder()
                .id(entity.getId())
                .eventId(entity.getEventId())
                .itemId(entity.getItemId())
                .itemName(entity.getItemName())
                .quantity(entity.getQuantity())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
