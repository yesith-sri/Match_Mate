package com.edu.basic.booking.mapper;


import com.edu.basic.booking.dto.response.BookingResponse;
import com.edu.basic.booking.entity.Booking;
import lombok.Data;
import org.springframework.stereotype.Component;
@Data
@Component
public class BookingMapper {

    public BookingResponse toResponse(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setUserId(booking.getUser() != null ? booking.getUser().getId() : null);
        response.setUserFullName(booking.getUser() != null
                ? booking.getUser().getFirstName() + " " + booking.getUser().getLastName() : null);
        response.setEventId(booking.getEvent() != null ? booking.getEvent().getEventId() : null);
        response.setEventName(booking.getEvent() != null ? booking.getEvent().getEventName() : null);
        response.setBookingDate(booking.getBookingDate());
        response.setBookingTime(booking.getBookingTime());
        response.setStatus(booking.getStatus());
        response.setCreatedAt(booking.getCreatedAt());
        return response;
    }
}