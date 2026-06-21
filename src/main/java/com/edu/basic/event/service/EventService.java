package com.edu.basic.event.service;

import com.edu.basic.event.dtos.EventRequestDTO;
import com.edu.basic.event.dtos.EventResponseDTO;
import com.edu.basic.event.dtos.EventUpdateDTO;
import com.edu.basic.event.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {

    // Create a new event
    EventResponseDTO createEvent(EventRequestDTO eventRequestDTO, Long userId);

    // Get event by ID
    EventResponseDTO getEventById(Long eventId);

    // Get all events with pagination
    Page<EventResponseDTO> getAllEvents(Pageable pageable);

    // Get upcoming events
    Page<EventResponseDTO> getUpcomingEvents(Pageable pageable);

    // Get events by status
    Page<EventResponseDTO> getEventsByStatus(EventStatus eventStatus, Pageable pageable);

    // Get events by type
    Page<EventResponseDTO> getEventsByType(String eventType, Pageable pageable);

    // Update event
    EventResponseDTO updateEvent(Long eventId, EventUpdateDTO eventUpdateDTO, Long userId);

    // Delete event
    void deleteEvent(Long eventId, Long userId);

    // Search events
    Page<EventResponseDTO> searchEvents(String keyword, Pageable pageable);

    // Get events by creator
    Page<EventResponseDTO> getEventsByCreator(Long userId, Pageable pageable);

    // Get events by location
    Page<EventResponseDTO> getEventsByLocation(String location, Pageable pageable);

    // Get available events (with available seats)
    Page<EventResponseDTO> getAvailableEvents(Pageable pageable);

    // Update event status
    EventResponseDTO updateEventStatus(Long eventId, EventStatus eventStatus, Long userId);

    // Get events near location
    Page<EventResponseDTO> getEventsNearLocation(Double latitude, Double longitude, Double radius, Pageable pageable);
}