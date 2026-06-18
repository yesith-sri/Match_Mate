package com.edu.basic.booking.service;




import com.edu.basic.booking.dto.request.BookingRequest;
import com.edu.basic.booking.dto.response.BookingResponse;
import com.edu.basic.booking.dto.response.EventAvailabilityResponse;

import java.util.List;

public interface BookingService {

    BookingResponse createBooking(Long userId, BookingRequest request);

    BookingResponse getBookingById(Long bookingId);

    List<BookingResponse> getBookingsByUser(Long userId);

    List<BookingResponse> getBookingsByEvent(Long eventId);

    BookingResponse cancelBooking(Long bookingId, Long userId);

    EventAvailabilityResponse getEventAvailability(Long eventId);

    BookingResponse confirmBooking(Long bookingId);
}