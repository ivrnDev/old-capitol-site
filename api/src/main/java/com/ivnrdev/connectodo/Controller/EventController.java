package com.ivnrdev.connectodo.Controller;

import com.ivnrdev.connectodo.DTO.Event.EventRequestDTO;
import com.ivnrdev.connectodo.DTO.Event.EventResponseDTO;
import com.ivnrdev.connectodo.Domain.Event;
import com.ivnrdev.connectodo.Mapper.EventMapper;
import com.ivnrdev.connectodo.Response.BaseResponse;
import com.ivnrdev.connectodo.Service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @PostMapping
    public ResponseEntity<BaseResponse<EventResponseDTO>> saveEvent(@RequestBody EventRequestDTO event) {
        Event savedEvent = eventService.saveEvent(eventMapper.toEntity(event));
        EventResponseDTO eventResponseDTO = eventMapper.toRes(savedEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.success(eventResponseDTO, "Event saved successfully")
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<EventResponseDTO>>> getAllEvents() {
        List<EventResponseDTO> events = eventService.getAllEvents().stream().map(data -> eventMapper.toRes(data)).collect(Collectors.toList());
        return ResponseEntity.ok(
                BaseResponse.success(events, "Events fetched successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<EventResponseDTO>> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        EventResponseDTO eventResponseDTO = eventMapper.toRes(event);
        return ResponseEntity.ok(
                BaseResponse.success(eventResponseDTO, "Event fetched successfully")
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<EventResponseDTO>> updateEvent(@PathVariable Long id, @RequestBody EventRequestDTO event) {
        Event updatedEvent = eventService.updateEventById(id, eventMapper.toEntity(event));
        EventResponseDTO eventResponseDTO = eventMapper.toRes(updatedEvent);
        return ResponseEntity.ok(
                BaseResponse.success(eventResponseDTO, "Event updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<EventResponseDTO>> deleteEvent(@PathVariable Long id) {
        Event deletedEvent = eventService.deleteEventById(id);
        EventResponseDTO eventResponseDTO = eventMapper.toRes(deletedEvent);
        return ResponseEntity.ok(
                BaseResponse.success(eventResponseDTO, "Event deleted successfully")
        );
    }
}
