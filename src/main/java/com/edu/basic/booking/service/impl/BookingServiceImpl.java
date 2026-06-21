package com.edu.basic.booking.service.impl;

import com.edu.basic.booking.dto.request.BookingRequest;
import com.edu.basic.booking.dto.response.BookingResponse;
import com.edu.basic.booking.entity.Booking;
import com.edu.basic.booking.enums.BookingStatus;
import com.edu.basic.booking.exception.BookingLimitExceededException;
import com.edu.basic.booking.mapper.BookingMapper;
import com.edu.basic.booking.repositary.BookingRepository;
import com.edu.basic.booking.service.BookingService;

import com.edu.basic.event.dtos.EventResponseDTO;
import com.edu.basic.event.entity.Event;
import com.edu.basic.event.repository.EventRepository;
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
                .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        boolean alreadyBooked = bookingRepository.existsByUserIdAndEvent_EventIdAndStatusNot(
                userId, request.getEventId(), BookingStatus.CANCELLED);
        if (alreadyBooked) {
            throw new RuntimeException("You have already booked this event");
        }

        UserGender gender = UserGender.valueOf(user.getGender().toUpperCase());

        long currentCount = bookingRepository.countByEventAndGenderAndStatus(
                request.getEventId(), gender, BookingStatus.CONFIRMED);

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
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            return bookingMapper.toResponse(booking);
        }

        Event event = booking.getEvent();
        UserGender gender = UserGender.valueOf(booking.getUser().getGender().toUpperCase());
        long currentCount = bookingRepository.countByEventAndGenderAndStatus(
                event.getEventId(), gender, BookingStatus.CONFIRMED);

        int limit = (gender == UserGender.MALE) ? event.getMaleLimit() : event.getFemaleLimit();

        // --- THIS IS THE PART YOU CHANGE ---
        if (currentCount >= limit) {
            // 1. Change the status to CANCELLED
            booking.setStatus(BookingStatus.CANCELLED);

            // 2. Save it to the database and IMMEDIATELY return the response
            // (We removed the 'throw new BookingLimitExceededException' from here)
            return bookingMapper.toResponse(bookingRepository.save(booking));
        }
        // -----------------------------------

        booking.setStatus(BookingStatus.CONFIRMED);
        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return bookingMapper.toResponse(booking);
    }

    @Override
    public List<BookingResponse> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
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
                .orElseThrow(() -> new RuntimeException("Booking not found for this user"));

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    public EventResponseDTO getEventAvailability(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));


        int maleCount = (int) bookingRepository.countByEventAndGenderAndStatus(eventId, UserGender.MALE, BookingStatus.CONFIRMED);
        int femaleCount = (int) bookingRepository.countByEventAndGenderAndStatus(eventId, UserGender.FEMALE, BookingStatus.CONFIRMED);


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