package com.edu.basic.event.controllers;

import com.edu.basic.event.dtos.ApiResponse;
import com.edu.basic.event.dtos.TicketRequestDTO;
import com.edu.basic.event.dtos.TicketResponseDTO;
import com.edu.basic.event.enums.TicketStatus;
import com.edu.basic.exception.UnauthorizedException;
import com.edu.basic.event.service.TicketService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
@Slf4j
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<TicketResponseDTO>> purchaseTicket(
            @Valid @RequestBody TicketRequestDTO ticketRequestDTO,
            Authentication authentication) {

        log.info("Purchase ticket request for event: {}", ticketRequestDTO.getEventId());

        try {
            Long userId = Long.parseLong(authentication.getName());
            TicketResponseDTO ticket = ticketService.purchaseTicket(ticketRequestDTO, userId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Ticket purchased successfully", ticket, HttpStatus.CREATED.value()));
        } catch (Exception e) {
            log.error("Error purchasing ticket: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error purchasing ticket: " + e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<TicketResponseDTO>> getTicketById(@PathVariable Long ticketId) {

        log.info("Get ticket request for ID: {}", ticketId);

        try {
            TicketResponseDTO ticket = ticketService.getTicketById(ticketId);
            return ResponseEntity.ok(ApiResponse.success("Ticket retrieved successfully", ticket, HttpStatus.OK.value()));
        } catch (Exception e) {
            log.error("Error fetching ticket: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Ticket not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    @GetMapping("/code/{ticketCode}")
    public ResponseEntity<ApiResponse<TicketResponseDTO>> getTicketByCode(@PathVariable String ticketCode) {

        log.info("Get ticket request for code: {}", ticketCode);

        try {
            TicketResponseDTO ticket = ticketService.getTicketByCode(ticketCode);
            return ResponseEntity.ok(ApiResponse.success("Ticket retrieved successfully", ticket, HttpStatus.OK.value()));
        } catch (Exception e) {
            log.error("Error fetching ticket: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Ticket not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    @GetMapping("/user/my-tickets")
    public ResponseEntity<ApiResponse<Page<TicketResponseDTO>>> getUserTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        log.info("Get user tickets request");

        try {
            Long userId = Long.parseLong(authentication.getName());
            Pageable pageable = PageRequest.of(page, size);
            Page<TicketResponseDTO> tickets = ticketService.getUserTickets(userId, pageable);

            return ResponseEntity.ok(ApiResponse.success("Tickets retrieved successfully", tickets, HttpStatus.OK.value()));
        } catch (Exception e) {
            log.error("Error fetching user tickets: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching tickets", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/user/active")
    public ResponseEntity<ApiResponse<Page<TicketResponseDTO>>> getUserActiveTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        log.info("Get user active tickets request");

        try {
            Long userId = Long.parseLong(authentication.getName());
            Pageable pageable = PageRequest.of(page, size);
            Page<TicketResponseDTO> tickets = ticketService.getUserActiveTickets(userId, pageable);

            return ResponseEntity.ok(ApiResponse.success("Active tickets retrieved successfully", tickets, HttpStatus.OK.value()));
        } catch (Exception e) {
            log.error("Error fetching active tickets: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching tickets", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<Page<TicketResponseDTO>>> getEventTickets(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Get event tickets request for event: {}", eventId);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<TicketResponseDTO> tickets = ticketService.getEventTickets(eventId, pageable);

            return ResponseEntity.ok(ApiResponse.success("Tickets retrieved successfully", tickets, HttpStatus.OK.value()));
        } catch (Exception e) {
            log.error("Error fetching event tickets: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching tickets", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/event/{eventId}/status/{status}")
    public ResponseEntity<ApiResponse<Page<TicketResponseDTO>>> getEventTicketsByStatus(
            @PathVariable Long eventId,
            @PathVariable TicketStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Get event tickets by status request for event: {}", eventId);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<TicketResponseDTO> tickets = ticketService.getEventTicketsByStatus(eventId, status, pageable);

            return ResponseEntity.ok(ApiResponse.success("Tickets retrieved successfully", tickets, HttpStatus.OK.value()));
        } catch (Exception e) {
            log.error("Error fetching tickets: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching tickets", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PutMapping("/{ticketId}/cancel")
    public ResponseEntity<ApiResponse<TicketResponseDTO>> cancelTicket(
            @PathVariable Long ticketId,
            @RequestParam String reason,
            Authentication authentication) {

        log.info("Cancel ticket request for ticket: {}", ticketId);

        try {
            Long userId = Long.parseLong(authentication.getName());
            TicketResponseDTO ticket = ticketService.cancelTicket(ticketId, reason, userId);

            return ResponseEntity.ok(ApiResponse.success("Ticket cancelled successfully", ticket, HttpStatus.OK.value()));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage(), HttpStatus.FORBIDDEN.value()));
        } catch (Exception e) {
            log.error("Error cancelling ticket: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error cancelling ticket: " + e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @PutMapping("/{ticketId}/use")
    public ResponseEntity<ApiResponse<TicketResponseDTO>> markTicketAsUsed(
            @PathVariable Long ticketId,
            Authentication authentication) {

        log.info("Mark ticket as used request for ticket: {}", ticketId);

        try {
            Long userId = Long.parseLong(authentication.getName());
            TicketResponseDTO ticket = ticketService.markTicketAsUsed(ticketId, userId);

            return ResponseEntity.ok(ApiResponse.success("Ticket marked as used successfully", ticket, HttpStatus.OK.value()));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage(), HttpStatus.FORBIDDEN.value()));
        } catch (Exception e) {
            log.error("Error marking ticket as used: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error marking ticket as used: " + e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @GetMapping("/check-purchase/{eventId}")
    public ResponseEntity<ApiResponse<Boolean>> hasUserPurchasedTicket(
            @PathVariable Long eventId,
            Authentication authentication) {

        log.info("Check ticket purchase request for event: {}", eventId);

        try {
            Long userId = Long.parseLong(authentication.getName());
            Boolean hasPurchased = ticketService.hasUserPurchasedTicket(userId, eventId);

            return ResponseEntity.ok(ApiResponse.success("", hasPurchased, HttpStatus.OK.value()));
        } catch (Exception e) {
            log.error("Error checking ticket purchase: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error checking ticket", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/event/{eventId}/sold-count")
    public ResponseEntity<ApiResponse<Long>> getTicketsSoldCount(@PathVariable Long eventId) {

        log.info("Get sold tickets count for event: {}", eventId);

        try {
            Long count = ticketService.getTicketsSoldCount(eventId);
            return ResponseEntity.ok(ApiResponse.success("Sold tickets count retrieved", count, HttpStatus.OK.value()));
        } catch (Exception e) {
            log.error("Error fetching sold tickets count: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching count", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}