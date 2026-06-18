package com.edu.basic.booking.mapper;


import com.edu.basic.booking.dto.response.BookingResponse;
import com.edu.basic.booking.entity.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingResponse toResponse(Booking booking) {
        if (booking == null) {
            return null;
        }

        return new BookingResponse(
                booking.getId(),
                booking.getUser().getId(),
                booking.getUser().getFirstName() + " " + booking.getUser().getLastname(),
                booking.getEvent().getId(),
                booking.getEvent().getName(),
                booking.getBookingDate(),
                booking.getBookingTime(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}