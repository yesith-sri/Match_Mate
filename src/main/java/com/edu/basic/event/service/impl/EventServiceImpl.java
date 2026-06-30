package com.edu.basic.event.service.impl;

import com.edu.basic.booking.enums.BookingStatus;
import com.edu.basic.booking.repositary.BookingRepository;
import com.edu.basic.event.dtos.EventRequestDTO;
import com.edu.basic.event.dtos.EventResponseDTO;
import com.edu.basic.event.dtos.EventUpdateDTO;
import com.edu.basic.event.entity.Event;
import com.edu.basic.event.enums.EventStatus;
import com.edu.basic.event.enums.EventType;
import com.edu.basic.event.mapeer.EventMapper;
import com.edu.basic.event.repository.EventRepository;
import com.edu.basic.event.service.EventService;
import com.edu.basic.exception.BusinessException;
import com.edu.basic.exception.ErrorCode;
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

@Service
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {

    @Autowired private EventRepository eventRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private EventMapper eventMapper;

    @Override
    public EventResponseDTO createEvent(EventRequestDTO dto, Long userId) {
        log.info("Creating event: {}", dto.getEventName());

        if (dto.getEventEndDate().isBefore(dto.getEventDate())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST,
                    "Event end date must be after event start date");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "User not found with id: " + userId));

        Event event = Event.builder()
                .eventName(dto.getEventName())
                .description(dto.getDescription())
                .eventType(dto.getEventType())
                .eventStatus(EventStatus.UPCOMING)
                .eventDate(dto.getEventDate())
                .eventEndDate(dto.getEventEndDate())
                .location(dto.getLocation())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .ticketPrice(dto.getTicketPrice())
                .totalSeats(dto.getTotalSeats())
                .availableSeats(dto.getTotalSeats())
                .imageUrl(dto.getImageUrl())
                .specialInstructions(dto.getSpecialInstructions())
                .createdBy(user)
                .build();

        Event saved = eventRepository.save(event);
        log.info("Event created with ID: {}", saved.getEventId());
        return eventMapper.mapToResponse(saved);
    }

    @Override
    public EventResponseDTO getEventById(Long eventId) {
        log.info("Fetching event: {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.EVENT_NOT_FOUND, "Event not found with id: " + eventId));
        return eventMapper.mapToResponse(event);
    }

    @Override
    public Page<EventResponseDTO> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable).map(eventMapper::mapToResponse);
    }

    @Override
    public Page<EventResponseDTO> getUpcomingEvents(Pageable pageable) {
        return eventRepository.findUpcomingEvents(pageable).map(eventMapper::mapToResponse);
    }

    @Override
    public Page<EventResponseDTO> getEventsByStatus(EventStatus status, Pageable pageable) {
        return eventRepository.findByEventStatus(status, pageable).map(eventMapper::mapToResponse);
    }

    @Override
    public Page<EventResponseDTO> getEventsByType(String eventType, Pageable pageable) {
        try {
            EventType type = EventType.valueOf(eventType.toUpperCase());
            return eventRepository.findByEventType(type, pageable).map(eventMapper::mapToResponse);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Invalid event type: " + eventType);
        }
    }

    @Override
    public EventResponseDTO updateEvent(Long eventId, EventUpdateDTO dto, Long userId) {
        log.info("Updating event: {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.EVENT_NOT_FOUND, "Event not found with id: " + eventId));

        if (!event.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException(ErrorCode.ACCESS_DENIED,
                    "You are not authorized to update this event");
        }

        if (dto.getEventDate() != null && dto.getEventEndDate() != null
                && dto.getEventEndDate().isBefore(dto.getEventDate())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST,
                    "Event end date must be after event start date");
        }

        if (dto.getEventName() != null)          event.setEventName(dto.getEventName());
        if (dto.getDescription() != null)         event.setDescription(dto.getDescription());
        if (dto.getEventType() != null)           event.setEventType(dto.getEventType());
        if (dto.getEventDate() != null)           event.setEventDate(dto.getEventDate());
        if (dto.getEventEndDate() != null)        event.setEventEndDate(dto.getEventEndDate());
        if (dto.getLocation() != null)            event.setLocation(dto.getLocation());
        if (dto.getLatitude() != null)            event.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null)           event.setLongitude(dto.getLongitude());
        if (dto.getTicketPrice() != null)         event.setTicketPrice(dto.getTicketPrice());
        if (dto.getEventStatus() != null)         event.setEventStatus(dto.getEventStatus());
        if (dto.getImageUrl() != null)            event.setImageUrl(dto.getImageUrl());
        if (dto.getSpecialInstructions() != null) event.setSpecialInstructions(dto.getSpecialInstructions());

        return eventMapper.mapToResponse(eventRepository.save(event));
    }

    @Override
    public void deleteEvent(Long eventId, Long userId) {
        log.info("Deleting event: {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.EVENT_NOT_FOUND, "Event not found with id: " + eventId));

        if (!event.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException(ErrorCode.ACCESS_DENIED,
                    "You are not authorized to delete this event");
        }

        eventRepository.delete(event);
    }

    @Override
    public Page<EventResponseDTO> searchEvents(String keyword, Pageable pageable) {
        return eventRepository.searchByEventName(keyword, pageable).map(eventMapper::mapToResponse);
    }

    @Override
    public Page<EventResponseDTO> getEventsByCreator(Long userId, Pageable pageable) {
        return eventRepository.findEventsByCreator(userId, pageable).map(eventMapper::mapToResponse);
    }

    @Override
    public Page<EventResponseDTO> getEventsByLocation(String location, Pageable pageable) {
        return eventRepository.findByLocation(location, pageable).map(eventMapper::mapToResponse);
    }

    @Override
    public Page<EventResponseDTO> getAvailableEvents(Pageable pageable) {
        return eventRepository.findEventsWithAvailableSeats(pageable).map(eventMapper::mapToResponse);
    }

    @Override
    public EventResponseDTO updateEventStatus(Long eventId, EventStatus status, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.EVENT_NOT_FOUND, "Event not found with id: " + eventId));

        if (!event.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException(ErrorCode.ACCESS_DENIED,
                    "You are not authorized to update this event status");
        }

        event.setEventStatus(status);
        return eventMapper.mapToResponse(eventRepository.save(event));
    }

    @Override
    public Page<EventResponseDTO> getEventsNearLocation(Double latitude, Double longitude,
                                                        Double radius, Pageable pageable) {
        return eventRepository.findEventsNearLocation(latitude, longitude, radius, pageable)
                .map(eventMapper::mapToResponse);
    }

    @Transactional
    public EventResponseDTO updateGenderLimits(Long eventId, int maleLimit, int femaleLimit) {

        if (maleLimit < 0 || femaleLimit < 0) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST,
                    "Capacity limits cannot be negative");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.EVENT_NOT_FOUND, "Event not found with id: " + eventId));

        long confirmedMales = bookingRepository.countByEventAndGenderAndStatus(
                eventId, UserGender.MALE.name(), BookingStatus.CONFIRMED);
        long confirmedFemales = bookingRepository.countByEventAndGenderAndStatus(
                eventId, UserGender.FEMALE.name(), BookingStatus.CONFIRMED);

        if (maleLimit < confirmedMales) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST,
                    "Cannot set male limit to " + maleLimit +
                            " — there are already " + confirmedMales + " confirmed male bookings");
        }

        if (femaleLimit < confirmedFemales) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST,
                    "Cannot set female limit to " + femaleLimit +
                            " — there are already " + confirmedFemales + " confirmed female bookings");
        }

        event.setMaleLimit(maleLimit);
        event.setFemaleLimit(femaleLimit);
        event.setTotalSeats(maleLimit + femaleLimit);

        return eventMapper.mapToResponse(eventRepository.save(event));
    }
}