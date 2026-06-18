package com.edu.basic.event.service;

import com.edu.basic.event.dtos.TicketRequestDTO;
import com.edu.basic.event.dtos.TicketResponseDTO;
import com.edu.basic.event.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketService {

    // Purchase a ticket for an event
    TicketResponseDTO purchaseTicket(TicketRequestDTO ticketRequestDTO, Long userId);

    // Get ticket by ID
    TicketResponseDTO getTicketById(Long ticketId);

    // Get ticket by code
    TicketResponseDTO getTicketByCode(String ticketCode);

    // Get all tickets for a user
    Page<TicketResponseDTO> getUserTickets(Long userId, Pageable pageable);

    // Get active tickets for a user
    Page<TicketResponseDTO> getUserActiveTickets(Long userId, Pageable pageable);

    // Get all tickets for an event
    Page<TicketResponseDTO> getEventTickets(Long eventId, Pageable pageable);

    // Get tickets for an event by status
    Page<TicketResponseDTO> getEventTicketsByStatus(Long eventId, TicketStatus ticketStatus, Pageable pageable);

    // Cancel ticket
    TicketResponseDTO cancelTicket(Long ticketId, String cancellationReason, Long userId);

    // Mark ticket as used
    TicketResponseDTO markTicketAsUsed(Long ticketId, Long userId);

    // Check if user has already purchased ticket for event
    Boolean hasUserPurchasedTicket(Long userId, Long eventId);

    // Get count of tickets sold for event
    Long getTicketsSoldCount(Long eventId);
}
