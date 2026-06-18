package com.edu.basic.booking.entity;

import com.edu.basic.booking.enums.BookingStatus;
import com.edu.basic.entity.BaseEntity;
import com.edu.basic.user.entity.User;
import jakarta.persistence.*;
import jdk.jfr.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table
@Getter
@Setter
public class Booking extends BaseEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id",nullable = false)
    private Event event;

    @Column(name = "booking_date",nullable = false)
    private Date bookingDate;

    @Column(name ="booking time", nullable = false)
    private Date bookingTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    }



}
