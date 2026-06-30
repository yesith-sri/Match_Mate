package com.edu.basic.booking.service.impl;

import com.edu.basic.booking.dto.request.BookingRequest;
import com.edu.basic.booking.dto.response.BookingResponse;
import com.edu.basic.booking.entity.Booking;
import com.edu.basic.booking.enums.BookingStatus;
import com.edu.basic.booking.mapper.BookingMapper;
import com.edu.basic.booking.repositary.BookingRepository;
import com.edu.basic.booking.service.BookingService;
import com.edu.basic.event.dtos.EventResponseDTO;
import com.edu.basic.event.entity.Event;
import com.edu.basic.event.repository.EventRepository;
import com.edu.basic.exception.BookingLimitExceededException;
import com.edu.basic.exception.BusinessException;
import com.edu.basic.exception.ErrorCode;
import com.edu.basic.exception.ResourceNotFoundException;
import com.edu.basic.user.entity.User;
import com.edu.basic.user.enums.UserGender;
import com.edu.basic.user.repositary.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final BookingMapper bookingMapper;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              EventRepository eventRepository,
                              BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    @Transactional
    public BookingResponse createBooking(Long userId, BookingRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "User not found with id: " + userId));

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.EVENT_NOT_FOUND, "Event not found with id: " + request.getEventId()));

        // Block only on CONFIRMED bookings — a stale PENDING from a failed payment
        // attempt should not permanently lock the user out. Cancel any lingering
        // PENDING bookings so a fresh attempt can proceed.
        boolean alreadyConfirmed = bookingRepository.existsByUserIdAndEvent_EventIdAndStatus(
                userId, request.getEventId(), BookingStatus.CONFIRMED);
        if (alreadyConfirmed) {
            throw new BusinessException(ErrorCode.DUPLICATE_BOOKING,
                    "You have already booked this event");
        }
        bookingRepository.findByUserIdAndEvent_EventIdAndStatus(userId, request.getEventId(), BookingStatus.PENDING)
                .forEach(b -> {
                    b.setStatus(BookingStatus.CANCELLED);
                    bookingRepository.save(b);
                });

        UserGender gender = UserGender.valueOf(user.getGender().toUpperCase());
        long currentCount = bookingRepository.countByEventAndGenderAndStatus(
                request.getEventId(), gender.name(), BookingStatus.CONFIRMED);
        int limit = (gender == UserGender.MALE) ? event.getMaleLimit() : event.getFemaleLimit();

        if (currentCount >= limit) {
            throw new BookingLimitExceededException(
                    "Booking limit reached for " + gender + " participants in this event");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setBookingDate(request.getBookingDate());
        booking.setBookingTime(request.getBookingTime());
        booking.setStatus(BookingStatus.PENDING);

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.BOOKING_NOT_FOUND, "Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            return bookingMapper.toResponse(booking);
        }

        Event event = booking.getEvent();
        UserGender gender = UserGender.valueOf(booking.getUser().getGender().toUpperCase());
        long currentCount = bookingRepository.countByEventAndGenderAndStatus(
                event.getEventId(), gender.name(), BookingStatus.CONFIRMED);
        int limit = (gender == UserGender.MALE) ? event.getMaleLimit() : event.getFemaleLimit();

        if (currentCount >= limit) {
            booking.setStatus(BookingStatus.CANCELLED);
            return bookingMapper.toResponse(bookingRepository.save(booking));
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.BOOKING_NOT_FOUND, "Booking not found with id: " + bookingId));

        return bookingMapper.toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByEvent(Long eventId) {
        return bookingRepository.findByEvent_EventId(eventId)
                .stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.BOOKING_NOT_FOUND, "Booking not found for this user"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.BOOKING_ALREADY_CANCELLED,
                    "Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    public EventResponseDTO getEventAvailability(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.EVENT_NOT_FOUND, "Event not found with id: " + eventId));

        int maleCount = (int) bookingRepository.countByEventAndGenderAndStatus(
                eventId, UserGender.MALE.name(), BookingStatus.CONFIRMED);
        int femaleCount = (int) bookingRepository.countByEventAndGenderAndStatus(
                eventId, UserGender.FEMALE.name(), BookingStatus.CONFIRMED);

        return EventResponseDTO.builder()
                .eventId(event.getEventId())
                .eventName(event.getEventName())
                .confirmedMaleCount(maleCount)
                .confirmedFemaleCount(femaleCount)
                .maleLimit(event.getMaleLimit())
                .femaleLimit(event.getFemaleLimit())
                .build();
    }
}