package com.edu.basic.event.service;

import com.edu.basic.event.dtos.TicketRequestDTO;
import com.edu.basic.event.dtos.TicketResponseDTO;
import com.edu.basic.event.entity.Event;
import com.edu.basic.event.entity.Ticket;
import com.edu.basic.event.enums.TicketStatus;
import com.edu.basic.event.repository.EventRepository;
import com.edu.basic.event.repository.TicketRepository;
import com.edu.basic.exception.BusinessException;
import com.edu.basic.exception.ResourceNotFoundException;
import com.edu.basic.exception.UnauthorizedException;
import com.edu.basic.user.entity.User;
import com.edu.basic.user.repositary.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public TicketResponseDTO purchaseTicket(TicketRequestDTO ticketRequestDTO, Long userId) {
        log.info("Purchasing ticket for event: {} by user: {}", ticketRequestDTO.getEventId(), userId);

        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get event
        Event event = eventRepository.findById(ticketRequestDTO.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        // Check if user already has an active ticket for this event
        if (Boolean.TRUE.equals(ticketRepository.existsActiveTicketForUserAndEvent(userId, event.getEventId()))) {
            throw new BusinessException("You already have an active ticket for this event");
        }

        // Check if event has available seats
        if (event.getAvailableSeats() <= 0) {
            throw new BusinessException("No available seats for this event");
        }

        // Check if event is not cancelled
        if (event.getEventStatus().name().equals("CANCELLED")) {
            throw new BusinessException("This event has been cancelled");
        }

        // Generate unique ticket code
        String ticketCode = generateTicketCode();

        // Create ticket
        Ticket ticket = Ticket.builder()
                .ticketCode(ticketCode)
                .event(event)
                .user(user)
                .ticketStatus(TicketStatus.ACTIVE)
                .purchasePrice(event.getTicketPrice())
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);

        // Update available seats
        event.setAvailableSeats(event.getAvailableSeats() - 1);
        eventRepository.save(event);

        log.info("Ticket purchased successfully. Ticket Code: {}", ticketCode);

        return mapTicketToResponseDTO(savedTicket);
    }

    @Override
    public TicketResponseDTO getTicketById(Long ticketId) {
        log.info("Fetching ticket with ID: {}", ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));

        return mapTicketToResponseDTO(ticket);
    }

    @Override
    public TicketResponseDTO getTicketByCode(String ticketCode) {
        log.info("Fetching ticket with code: {}", ticketCode);

        Ticket ticket = ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with code: " + ticketCode));

        return mapTicketToResponseDTO(ticket);
    }

    @Override
    public Page<TicketResponseDTO> getUserTickets(Long userId, Pageable pageable) {
        log.info("Fetching all tickets for user: {}", userId);

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        return ticketRepository.findByUser_Id(userId, pageable)
                .map(this::mapTicketToResponseDTO);
    }

    @Override
    public Page<TicketResponseDTO> getUserActiveTickets(Long userId, Pageable pageable) {
        log.info("Fetching active tickets for user: {}", userId);

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        return ticketRepository.findByUser_IdAndTicketStatus(userId, TicketStatus.ACTIVE, pageable)
                .map(this::mapTicketToResponseDTO);
    }

    @Override
    public Page<TicketResponseDTO> getEventTickets(Long eventId, Pageable pageable) {
        log.info("Fetching all tickets for event: {}", eventId);

        // Verify event exists
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found");
        }

        return ticketRepository.findByEvent_EventId(eventId, pageable)
                .map(this::mapTicketToResponseDTO);
    }

    @Override
    public Page<TicketResponseDTO> getEventTicketsByStatus(Long eventId, TicketStatus ticketStatus, Pageable pageable) {
        log.info("Fetching tickets for event: {} with status: {}", eventId, ticketStatus);

        // Verify event exists
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found");
        }

        return ticketRepository.findByEvent_EventIdAndTicketStatus(eventId, ticketStatus, pageable)
                .map(this::mapTicketToResponseDTO);
    }

    @Override
    public TicketResponseDTO cancelTicket(Long ticketId, String cancellationReason, Long userId) {
        log.info("Cancelling ticket: {}", ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        // Check authorization
        if (!ticket.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to cancel this ticket");
        }

        // Check if ticket can be cancelled
        if (!ticket.getTicketStatus().equals(TicketStatus.ACTIVE)) {
            throw new BusinessException("Only active tickets can be cancelled");
        }

        // Update ticket
        ticket.setTicketStatus(TicketStatus.CANCELLED);
        ticket.setCancelledAt(LocalDateTime.now());
        ticket.setCancellationReason(cancellationReason);

        Ticket updatedTicket = ticketRepository.save(ticket);

        // Increase available seats for the event
        Event event = ticket.getEvent();
        event.setAvailableSeats(event.getAvailableSeats() + 1);
        eventRepository.save(event);

        log.info("Ticket cancelled successfully");

        return mapTicketToResponseDTO(updatedTicket);
    }

    @Override
    public TicketResponseDTO markTicketAsUsed(Long ticketId, Long userId) {
        log.info("Marking ticket as used: {}", ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        // Check authorization (only admin or event creator)
        Event event = ticket.getEvent();
        if (!event.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to mark this ticket as used");
        }

        // Check if ticket is active
        if (!ticket.getTicketStatus().equals(TicketStatus.ACTIVE)) {
            throw new BusinessException("Only active tickets can be marked as used");
        }

        ticket.setTicketStatus(TicketStatus.USED);
        ticket.setUsedAt(LocalDateTime.now());

        Ticket updatedTicket = ticketRepository.save(ticket);
        log.info("Ticket marked as used successfully");

        return mapTicketToResponseDTO(updatedTicket);
    }

    @Override
    public Boolean hasUserPurchasedTicket(Long userId, Long eventId) {
        log.info("Checking if user: {} has purchased ticket for event: {}", userId, eventId);

        return ticketRepository.existsActiveTicketForUserAndEvent(userId, eventId);
    }

    @Override
    public Long getTicketsSoldCount(Long eventId) {
        log.info("Getting sold tickets count for event: {}", eventId);

        return ticketRepository.countSoldTickets(eventId);
    }

    // Helper method to generate unique ticket code
    private String generateTicketCode() {
        return "TKT-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Helper method to map Ticket to TicketResponseDTO
    private TicketResponseDTO mapTicketToResponseDTO(Ticket ticket) {
        return TicketResponseDTO.builder()
                .ticketId(ticket.getTicketId())
                .ticketCode(ticket.getTicketCode())
                .eventId(ticket.getEvent().getEventId())
                .eventName(ticket.getEvent().getEventName())
                .userId(ticket.getUser().getId())
                .username(ticket.getUser().getEmail())
                .ticketStatus(ticket.getTicketStatus())
                .purchasePrice(ticket.getPurchasePrice())
                .purchasedAt(ticket.getPurchasedAt())
                .usedAt(ticket.getUsedAt())
                .cancelledAt(ticket.getCancelledAt())
                .cancellationReason(ticket.getCancellationReason())
                .build();
    }
}