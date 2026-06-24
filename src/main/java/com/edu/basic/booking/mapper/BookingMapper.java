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

        return new BookingResponse(
        );
    }
}