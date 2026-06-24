package com.edu.basic.event.service.impl;

import com.edu.basic.booking.enums.BookingStatus;
import com.edu.basic.booking.repositary.BookingRepository;
import com.edu.basic.event.dtos.EventRequestDTO;
import com.edu.basic.event.dtos.EventResponseDTO;
import com.edu.basic.event.dtos.EventUpdateDTO;
import com.edu.basic.event.entity.Event;
import com.edu.basic.event.enums.EventStatus;
import com.edu.basic.event.enums.EventType;
import com.edu.basic.event.repository.EventRepository;
import com.edu.basic.event.service.EventService;
import com.edu.basic.exception.ResourceNotFoundException;
import com.edu.basic.exception.UnauthorizedException;
import com.edu.basic.user.entity.User;
import com.edu.basic.user.enums.UserGender;
import com.edu.basic.user.repositary.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public EventResponseDTO createEvent(EventRequestDTO eventRequestDTO, Long userId) {
        log.info("Creating event: {}", eventRequestDTO.getEventName());

        // Validate event dates
        if (eventRequestDTO.getEventEndDate().isBefore(eventRequestDTO.getEventDate())) {
            throw new IllegalArgumentException("Event end date must be after event start date");
        }

        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Create event entity
        Event event = Event.builder()
                .eventName(eventRequestDTO.getEventName())
                .description(eventRequestDTO.getDescription())
                .eventType(eventRequestDTO.getEventType())
                .eventStatus(EventStatus.UPCOMING)
                .eventDate(eventRequestDTO.getEventDate())
                .eventEndDate(eventRequestDTO.getEventEndDate())
                .location(eventRequestDTO.getLocation())
                .latitude(eventRequestDTO.getLatitude())
                .longitude(eventRequestDTO.getLongitude())
                .ticketPrice(eventRequestDTO.getTicketPrice())
                .totalSeats(eventRequestDTO.getTotalSeats())
                .availableSeats(eventRequestDTO.getTotalSeats())
                .imageUrl(eventRequestDTO.getImageUrl())
                .specialInstructions(eventRequestDTO.getSpecialInstructions())
                .createdBy(user)
                .build();

        Event savedEvent = eventRepository.save(event);
        log.info("Event created successfully with ID: {}", savedEvent.getEventId());

        return mapEventToResponseDTO(savedEvent);
    }

    @Override
    public EventResponseDTO getEventById(Long eventId) {
        log.info("Fetching event with ID: {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        return mapEventToResponseDTO(event);
    }

    @Override
    public Page<EventResponseDTO> getAllEvents(Pageable pageable) {
        log.info("Fetching all events with pagination");

        return eventRepository.findAll(pageable)
                .map(this::mapEventToResponseDTO);
    }

    @Override
    public Page<EventResponseDTO> getUpcomingEvents(Pageable pageable) {
        log.info("Fetching upcoming events");

        return eventRepository.findUpcomingEvents(pageable)
                .map(this::mapEventToResponseDTO);
    }

    @Override
    public Page<EventResponseDTO> getEventsByStatus(EventStatus eventStatus, Pageable pageable) {
        log.info("Fetching events by status: {}", eventStatus);

        return eventRepository.findByEventStatus(eventStatus, pageable)
                .map(this::mapEventToResponseDTO);
    }

    @Override
    public Page<EventResponseDTO> getEventsByType(String eventType, Pageable pageable) {
        log.info("Fetching events by type: {}", eventType);

        try {
            EventType type = EventType.valueOf(eventType.toUpperCase());
            return eventRepository.findByEventType(type, pageable)
                    .map(this::mapEventToResponseDTO);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid event type: " + eventType);
        }
    }

    @Override
    public EventResponseDTO updateEvent(Long eventId, EventUpdateDTO eventUpdateDTO, Long userId) {
        log.info("Updating event with ID: {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        // Check authorization
        if (!event.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this event");
        }

        // Validate event dates if both are provided
        if (eventUpdateDTO.getEventDate() != null && eventUpdateDTO.getEventEndDate() != null) {
            if (eventUpdateDTO.getEventEndDate().isBefore(eventUpdateDTO.getEventDate())) {
                throw new IllegalArgumentException("Event end date must be after event start date");
            }
        }

        // Update fields
        if (eventUpdateDTO.getEventName() != null) {
            event.setEventName(eventUpdateDTO.getEventName());
        }
        if (eventUpdateDTO.getDescription() != null) {
            event.setDescription(eventUpdateDTO.getDescription());
        }
        if (eventUpdateDTO.getEventType() != null) {
            event.setEventType(eventUpdateDTO.getEventType());
        }
        if (eventUpdateDTO.getEventDate() != null) {
            event.setEventDate(eventUpdateDTO.getEventDate());
        }
        if (eventUpdateDTO.getEventEndDate() != null) {
            event.setEventEndDate(eventUpdateDTO.getEventEndDate());
        }
        if (eventUpdateDTO.getLocation() != null) {
            event.setLocation(eventUpdateDTO.getLocation());
        }
        if (eventUpdateDTO.getLatitude() != null) {
            event.setLatitude(eventUpdateDTO.getLatitude());
        }
        if (eventUpdateDTO.getLongitude() != null) {
            event.setLongitude(eventUpdateDTO.getLongitude());
        }
        if (eventUpdateDTO.getTicketPrice() != null) {
            event.setTicketPrice(eventUpdateDTO.getTicketPrice());
        }
        if (eventUpdateDTO.getEventStatus() != null) {
            event.setEventStatus(eventUpdateDTO.getEventStatus());
        }
        if (eventUpdateDTO.getImageUrl() != null) {
            event.setImageUrl(eventUpdateDTO.getImageUrl());
        }
        if (eventUpdateDTO.getSpecialInstructions() != null) {
            event.setSpecialInstructions(eventUpdateDTO.getSpecialInstructions());
        }

        Event updatedEvent = eventRepository.save(event);
        log.info("Event updated successfully");

        return mapEventToResponseDTO(updatedEvent);
    }

    @Override
    public void deleteEvent(Long eventId, Long userId) {
        log.info("Deleting event with ID: {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        // Check authorization
        if (!event.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this event");
        }

        eventRepository.delete(event);
        log.info("Event deleted successfully");
    }

    @Override
    public Page<EventResponseDTO> searchEvents(String keyword, Pageable pageable) {
        log.info("Searching events with keyword: {}", keyword);

        return eventRepository.searchByEventName(keyword, pageable)
                .map(this::mapEventToResponseDTO);
    }

    @Override
    public Page<EventResponseDTO> getEventsByCreator(Long userId, Pageable pageable) {
        log.info("Fetching events created by user: {}", userId);

        return eventRepository.findEventsByCreator(userId, pageable)
                .map(this::mapEventToResponseDTO);
    }

    @Override
    public Page<EventResponseDTO> getEventsByLocation(String location, Pageable pageable) {
        log.info("Fetching events by location: {}", location);

        return eventRepository.findByLocation(location, pageable)
                .map(this::mapEventToResponseDTO);
    }

    @Override
    public Page<EventResponseDTO> getAvailableEvents(Pageable pageable) {
        log.info("Fetching available events");

        return eventRepository.findEventsWithAvailableSeats(pageable)
                .map(this::mapEventToResponseDTO);
    }

    @Override
    public EventResponseDTO updateEventStatus(Long eventId, EventStatus eventStatus, Long userId) {
        log.info("Updating event status: {}, Event ID: {}", eventStatus, eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        // Check authorization
        if (!event.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this event status");
        }

        event.setEventStatus(eventStatus);
        Event updatedEvent = eventRepository.save(event);

        return mapEventToResponseDTO(updatedEvent);
    }

    @Override
    public Page<EventResponseDTO> getEventsNearLocation(Double latitude, Double longitude, Double radius, Pageable pageable) {
        log.info("Fetching events near location: latitude={}, longitude={}, radius={}", latitude, longitude, radius);

        return eventRepository.findEventsNearLocation(latitude, longitude, radius, pageable)
                .map(this::mapEventToResponseDTO);
    }

    // Helper method to map Event to EventResponseDTO
    private EventResponseDTO mapEventToResponseDTO(Event event) {
        LocalDateTime now = LocalDateTime.now();
        boolean isEventStarted = now.isAfter(event.getEventDate());
        boolean isEventEnded = now.isAfter(event.getEventEndDate());

        int bookingPercentage = ((event.getTotalSeats() - event.getAvailableSeats()) * 100) / event.getTotalSeats();

        return EventResponseDTO.builder()
                .eventId(event.getEventId())
                .eventName(event.getEventName())
                .description(event.getDescription())
                .eventType(event.getEventType())
                .eventStatus(event.getEventStatus())
                .eventDate(event.getEventDate())
                .eventEndDate(event.getEventEndDate())
                .location(event.getLocation())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .ticketPrice(event.getTicketPrice())
                .totalSeats(event.getTotalSeats())
                .availableSeats(event.getAvailableSeats())
                .imageUrl(event.getImageUrl())
                .specialInstructions(event.getSpecialInstructions())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .createdByUsername(event.getCreatedBy().getEmail())
                .createdByUserId(event.getCreatedBy().getId())
                .bookingPercentage(bookingPercentage)
                .isEventStarted(isEventStarted)
                .isEventEnded(isEventEnded)
                .build();
    }


    @Transactional
    public EventResponseDTO updateGenderLimits(Long eventId, int maleLimit, int femaleLimit) {

        // 1. Prevent negative numbers
        if (maleLimit < 0 || femaleLimit < 0) {
            throw new IllegalArgumentException("Capacity limits cannot be negative.");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // 2. Count currently confirmed bookings
        long confirmedMales = bookingRepository.countByEventAndGenderAndStatus(
                eventId, UserGender.MALE, BookingStatus.CONFIRMED);

        long confirmedFemales = bookingRepository.countByEventAndGenderAndStatus(
                eventId, UserGender.FEMALE, BookingStatus.CONFIRMED);

        // 3. Prevent lowering limits below confirmed bookings
        if (maleLimit < confirmedMales) {
            throw new IllegalArgumentException(
                    "Cannot set male limit to " + maleLimit + " because there are already " + confirmedMales + " confirmed male bookings.");
        }

        if (femaleLimit < confirmedFemales) {
            throw new IllegalArgumentException(
                    "Cannot set female limit to " + femaleLimit + " because there are already " + confirmedFemales + " confirmed female bookings.");
        }

        // 4. Safe to update the limits
        event.setMaleLimit(maleLimit);
        event.setFemaleLimit(femaleLimit);

        // (Optional) If your totalSeats is just male + female, update it here!
        event.setTotalSeats(maleLimit + femaleLimit);

        Event updatedEvent = eventRepository.save(event);

        return mapEventToResponseDTO(updatedEvent);
    }
}
