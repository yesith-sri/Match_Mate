package com.edu.basic.event.repository;

import com.edu.basic.event.entity.Event;
import com.edu.basic.event.enums.EventStatus;
import com.edu.basic.event.enums.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Find all events by status
    Page<Event> findByEventStatus(EventStatus eventStatus, Pageable pageable);

    // Find all events by type
    Page<Event> findByEventType(EventType eventType, Pageable pageable);

    // Find all events by location
    Page<Event> findByLocation(String location, Pageable pageable);

    // Find upcoming events
    @Query("SELECT e FROM Event e WHERE e.eventStatus = 'UPCOMING' ORDER BY e.eventDate ASC")
    Page<Event> findUpcomingEvents(Pageable pageable);

    // Find events by date range
    @Query("SELECT e FROM Event e WHERE e.eventDate BETWEEN :startDate AND :endDate ORDER BY e.eventDate ASC")
    Page<Event> findEventsByDateRange(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate,
                                      Pageable pageable);

    // Find events with available seats
    @Query("SELECT e FROM Event e WHERE e.availableSeats > 0 AND e.eventStatus = 'UPCOMING' ORDER BY e.eventDate ASC")
    Page<Event> findEventsWithAvailableSeats(Pageable pageable);

    // Find events by creator
    @Query("SELECT e FROM Event e WHERE e.createdBy.id = :userId ORDER BY e.eventDate DESC")
    Page<Event> findEventsByCreator(@Param("userId") Long userId, Pageable pageable);

    // Search events by name
    @Query("SELECT e FROM Event e WHERE LOWER(e.eventName) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY e.eventDate ASC")
    Page<Event> searchByEventName(@Param("keyword") String keyword, Pageable pageable);

    // Count events by status
    Long countByEventStatus(EventStatus eventStatus);

    // Find events near location (using latitude and longitude)
    @Query(value = "SELECT e FROM Event e WHERE " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(e.latitude)) * " +
            "cos(radians(e.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(e.latitude)))) <= :radius " +
            "ORDER BY e.eventDate ASC",
            nativeQuery = false)
    Page<Event> findEventsNearLocation(@Param("latitude") Double latitude,
                                       @Param("longitude") Double longitude,
                                       @Param("radius") Double radius,
                                       Pageable pageable);
}