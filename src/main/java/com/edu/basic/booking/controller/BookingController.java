
package com.edu.basic.booking.controller;
import com.edu.basic.booking.dto.request.BookingRequest;
import com.edu.basic.booking.dto.response.BookingResponse;
import com.edu.basic.booking.service.BookingService;
import com.edu.basic.event.dtos.EventResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody BookingRequest request) {

        BookingResponse response = bookingService.createBooking(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBookingById(bookingId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(bookingService.getBookingsByEvent(eventId));
    }

    @GetMapping("/event/{eventId}/availability")
    public ResponseEntity<EventResponseDTO> getEventAvailability(
            @PathVariable Long eventId) {
        return ResponseEntity.ok(bookingService.getEventAvailability(eventId));
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId, userId));
    }
}