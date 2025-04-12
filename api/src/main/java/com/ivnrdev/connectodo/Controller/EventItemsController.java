package com.ivnrdev.connectodo.Controller;

import com.ivnrdev.connectodo.DTO.Event.EventItemsRequestDTO;
import com.ivnrdev.connectodo.DTO.Event.EventItemsResponseDTO;
import com.ivnrdev.connectodo.Domain.EventItems;
import com.ivnrdev.connectodo.Mapper.EventItemsMapper;
import com.ivnrdev.connectodo.Response.BaseResponse;
import com.ivnrdev.connectodo.Service.EventItemsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/eventItem")
@RequiredArgsConstructor
public class EventItemsController {
    private final EventItemsService eventItemsService;
    private final EventItemsMapper eventItemsMapper;

    @PostMapping
    public ResponseEntity<BaseResponse<EventItemsResponseDTO>> saveEventItems(@RequestBody EventItemsRequestDTO eventItem) {
        EventItems savedEventItems = eventItemsService.saveEventItems(eventItemsMapper.toEntity(eventItem));
        EventItemsResponseDTO eventItemResponseDTO = eventItemsMapper.toRes(savedEventItems);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.success(eventItemResponseDTO, "EventItems saved successfully")
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<EventItemsResponseDTO>>> getAllEventItemss() {
        List<EventItemsResponseDTO> eventItems = eventItemsService.getAllEventItemss().stream().map(data -> eventItemsMapper.toRes(data)).collect(Collectors.toList());
        return ResponseEntity.ok(
                BaseResponse.success(eventItems, "EventItemss fetched successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<EventItemsResponseDTO>> getEventItemsById(@PathVariable Long id) {
        EventItems eventItem = eventItemsService.getEventItemsById(id);
        EventItemsResponseDTO eventItemResponseDTO = eventItemsMapper.toRes(eventItem);
        return ResponseEntity.ok(
                BaseResponse.success(eventItemResponseDTO, "EventItems fetched successfully")
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<EventItemsResponseDTO>> updateEventItems(@PathVariable Long id, @RequestBody EventItemsRequestDTO eventItem) {
        EventItems updatedEventItems = eventItemsService.updateEventItemsById(id, eventItemsMapper.toEntity(eventItem));
        EventItemsResponseDTO eventItemResponseDTO = eventItemsMapper.toRes(updatedEventItems);
        return ResponseEntity.ok(
                BaseResponse.success(eventItemResponseDTO, "EventItems updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<EventItemsResponseDTO>> deleteEventItems(@PathVariable Long id) {
        EventItems deletedEventItems = eventItemsService.deleteEventItemsById(id);
        EventItemsResponseDTO eventItemResponseDTO = eventItemsMapper.toRes(deletedEventItems);
        return ResponseEntity.ok(
                BaseResponse.success(eventItemResponseDTO, "EventItems deleted successfully")
        );
    }
}
