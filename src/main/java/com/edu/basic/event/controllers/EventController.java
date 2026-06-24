package com.edu.basic.event.controllers;

import com.edu.basic.event.dtos.ApiResponse;
import com.edu.basic.event.dtos.EventRequestDTO;
import com.edu.basic.event.dtos.EventResponseDTO;
import com.edu.basic.event.dtos.EventUpdateDTO;
import com.edu.basic.event.enums.EventStatus;
import com.edu.basic.event.service.impl.EventServiceImpl;
import com.edu.basic.exception.UnauthorizedException;
import com.edu.basic.event.service.EventService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/events")
@Slf4j
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private EventServiceImpl eventServiceImpl;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<EventResponseDTO>> createEvent(
            @Valid @RequestBody EventRequestDTO eventRequestDTO,
            Authentication authentication) {

        log.info("Create event request: {}", eventRequestDTO.getEventName());

        try {
            Long userId = Long.parseLong(authentication.getName());
            EventResponseDTO event = eventService.createEvent(eventRequestDTO, userId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Event created successfully", event, HttpStatus.CREATED.value()));
        } catch (Exception e) {
            log.error("Error creating event: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error creating event: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventResponseDTO>> getEventById(@PathVariable Long eventId) {

        log.info("Get event request for ID: {}", eventId);

        try {
            EventResponseDTO event = eventService.getEventById(eventId);
            return ResponseEntity.ok(ApiResponse.success("Event retrieved successfully", event, HttpStatus.OK.value()));
        } catch (Exception e) {
            log.error("Error fetching event: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Event not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<EventResponseDTO>>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "eventDate") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {

        log.info("Get all events request: page={}, size={}", page, size);

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<EventResponseDTO> events = eventService.getAllEvents(pageable);

            return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events, HttpStatus.OK.value()));
        } catch (Exception e) {
            log.error("Error fetching events: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching events", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<Page<EventResponseDTO>>> getUpcomingEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Get upcoming events request");

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "eventDate"));
            Page<EventResponseDTO> events = eventService.getUpcomingEvents(pageable);

            return ResponseEntity.ok(ApiResponse.success("Upcoming events retrieved successfully", events, HttpStatus.OK.value()));
        } catch (Exception e) {
            log.error("Error fetching upcoming events: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching upcoming events", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<EventResponseDTO>>> getEventsByStatus(
            @PathVariable EventStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Get events by status: {}", status);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<EventResponseDTO> events = eventService.getEventsByStatus(status, pageable);

            return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events, HttpStatus.OK.value()));
        } catch (Exception e) {
            log.error("Error fetching events by status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching events", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<Page<EventResponseDTO>>> getEventsByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Get events by type: {}", type);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<EventResponseDTO> events = eventService.getEventsByType(type, pageable);

            return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events, HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            log.error("Error fetching events by type: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching events", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<Page<EventResponseDTO>>> getAvailableEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Get available events request");

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<EventResponseDTO> events = eventService.getAvailableEvents(pageable);

            return ResponseEntity.ok(ApiResponse.success("Available events retrieved successfully", events, HttpStatus.OK.value()));
        } catch (Exception e) {
            log.error("Error fetching available events: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching events", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<EventResponseDTO>>> searchEvents(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Search events with keyword: {}", keyword);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<EventResponseDTO> events = eventService.searchEvents(keyword, pageable);

            return ResponseEntity.ok(ApiResponse.success("Events found", events, HttpStatus.OK.value()));
        } catch (Exception e) {
            log.error("Error searching events: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error searching events", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<ApiResponse<Page<EventResponseDTO>>> getEventsByLocation(
            @PathVariable String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Get events by location: {}", location);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<EventResponseDTO> events = eventService.getEventsByLocation(location, pageable);

            return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events, HttpStatus.OK.value()));
        } catch (Exception e) {
            log.error("Error fetching events by location: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching events", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<Page<EventResponseDTO>>> getEventsNearLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "50") Double radius,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Get events near location: lat={}, lon={}, radius={}", latitude, longitude, radius);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<EventResponseDTO> events = eventService.getEventsNearLocation(latitude, longitude, radius, pageable);

            return ResponseEntity.ok(ApiResponse.success("Events found near location", events, HttpStatus.OK.value()));
        } catch (Exception e) {
            log.error("Error fetching nearby events: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching events", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventResponseDTO>> updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody EventUpdateDTO eventUpdateDTO,
            Authentication authentication) {

        log.info("Update event request for ID: {}", eventId);

        try {
            Long userId = Long.parseLong(authentication.getName());
            EventResponseDTO event = eventService.updateEvent(eventId, eventUpdateDTO, userId);

            return ResponseEntity.ok(ApiResponse.success("Event updated successfully", event, HttpStatus.OK.value()));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage(), HttpStatus.FORBIDDEN.value()));
        } catch (Exception e) {
            log.error("Error updating event: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error updating event: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PutMapping("/{eventId}/status/{status}")
    public ResponseEntity<ApiResponse<EventResponseDTO>> updateEventStatus(
            @PathVariable Long eventId,
            @PathVariable EventStatus status,
            Authentication authentication) {

        log.info("Update event status request for ID: {}", eventId);

        try {
            Long userId = Long.parseLong(authentication.getName());
            EventResponseDTO event = eventService.updateEventStatus(eventId, status, userId);

            return ResponseEntity.ok(ApiResponse.success("Event status updated successfully", event, HttpStatus.OK.value()));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage(), HttpStatus.FORBIDDEN.value()));
        } catch (Exception e) {
            log.error("Error updating event status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error updating event status", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<ApiResponse<String>> deleteEvent(
            @PathVariable Long eventId,
            Authentication authentication) {

        log.info("Delete event request for ID: {}", eventId);

        try {
            Long userId = Long.parseLong(authentication.getName());
            eventService.deleteEvent(eventId, userId);

            return ResponseEntity.ok(ApiResponse.success("Event deleted successfully", "Event removed", HttpStatus.OK.value()));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage(), HttpStatus.FORBIDDEN.value()));
        } catch (Exception e) {
            log.error("Error deleting event: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error deleting event", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/creator/{userId}")
    public ResponseEntity<ApiResponse<Page<EventResponseDTO>>> getEventsByCreator(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Get events created by user: {}", userId);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<EventResponseDTO> events = eventService.getEventsByCreator(userId, pageable);

            return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events, HttpStatus.OK.value()));
        } catch (Exception e) {
            log.error("Error fetching events by creator: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching events", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PutMapping("/{eventId}/limits")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponseDTO> updateLimits(
            @PathVariable Long eventId,
            @RequestParam int maleLimit,
            @RequestParam int femaleLimit) {
        return ResponseEntity.ok(eventServiceImpl.updateGenderLimits(eventId, maleLimit, femaleLimit));
    }
    
    
}
