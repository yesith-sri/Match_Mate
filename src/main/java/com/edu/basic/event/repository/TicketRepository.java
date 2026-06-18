package com.edu.basic.event.repository;

import com.edu.basic.event.entity.Ticket;
import com.edu.basic.event.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Find ticket by ticket code
    Optional<Ticket> findByTicketCode(String ticketCode);

    // Find all tickets for a specific event
    Page<Ticket> findByEvent_EventId(Long eventId, Pageable pageable);

    // Find all tickets for a specific user
    Page<Ticket> findByUser_Id(Long userId, Pageable pageable);

    // Find all tickets for a user by status
    Page<Ticket> findByUser_IdAndTicketStatus(Long userId, TicketStatus ticketStatus, Pageable pageable);

    // Find all tickets for an event by status
    Page<Ticket> findByEvent_EventIdAndTicketStatus(Long eventId, TicketStatus ticketStatus, Pageable pageable);

    // Check if user already has ticket for an event
    @Query("SELECT COUNT(t) > 0 FROM Ticket t WHERE t.user.id = :userId AND t.event.eventId = :eventId AND t.ticketStatus = 'ACTIVE'")
    Boolean existsActiveTicketForUserAndEvent(@Param("userId") Long userId, @Param("eventId") Long eventId);

    // Count tickets sold for an event
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.event.eventId = :eventId AND t.ticketStatus != 'CANCELLED'")
    Long countSoldTickets(@Param("eventId") Long eventId);

    // Get all active tickets for an event
    Page<Ticket> findByEvent_EventIdAndTicketStatusOrderByPurchasedAtDesc(Long eventId, TicketStatus ticketStatus, Pageable pageable);

    // Get user's active tickets
    Page<Ticket> findByUser_IdAndTicketStatusOrderByPurchasedAtDesc(Long userId, TicketStatus ticketStatus, Pageable pageable);
}
