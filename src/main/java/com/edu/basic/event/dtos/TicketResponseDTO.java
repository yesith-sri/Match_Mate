package com.edu.basic.event.dtos;

import com.edu.basic.event.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponseDTO {

    private Long ticketId;

    private String ticketCode;

    private Long eventId;

    private String eventName;

    private Long userId;

    private String username;

    private TicketStatus ticketStatus;

    private BigDecimal purchasePrice;

    private LocalDateTime purchasedAt;

    private LocalDateTime usedAt;

    private LocalDateTime cancelledAt;

    private String cancellationReason;
}
