package com.edu.basic.booking.dto.response;

import com.edu.basic.booking.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor // This generates the constructor your mapper is trying to use
@NoArgsConstructor
public class BookingResponse {

    private Long id;
    private Long userId;
    private String userFullName;
    private Long eventId;
    private String eventName;
    private LocalDate bookingDate;
    private LocalTime bookingTime;
    private BookingStatus status;
    private LocalDateTime createdAt;


}
