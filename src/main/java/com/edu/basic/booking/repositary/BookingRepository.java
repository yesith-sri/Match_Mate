package com.edu.basic.booking.repositary;



import com.edu.basic.booking.entity.Booking;
import com.edu.basic.booking.enums.BookingStatus;
import com.edu.basic.user.enums.UserGender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT COUNT(b) FROM Booking b " +
            "WHERE b.event.eventId = :eventId " +
            "AND b.user.gender = :gender " +
            "AND b.status = :status")
    long countByEventAndGenderAndStatus(@Param("eventId") Long eventId,
                                        @Param("gender") UserGender gender,
                                        @Param("status") BookingStatus status);

    List<Booking> findByUserId(Long userId);

    List<Booking> findByEvent_EventId(Long eventId);

    Optional<Booking> findByIdAndUserId(Long eventId, Long userId);

    boolean existsByUserIdAndEvent_EventIdAndStatusNot(Long userId, Long eventId, BookingStatus status);
}
