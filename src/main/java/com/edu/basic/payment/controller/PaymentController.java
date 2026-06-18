package com.edu.basic.payment.controller;

import com.edu.basic.payment.dto.request.PaymentRequest;
import com.edu.basic.payment.dto.response.PaymentHashResponse;
import com.edu.basic.payment.dto.response.PaymentResponse;
import com.edu.basic.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Authenticated — user calls this first to get hash for PayHere popup
    @PostMapping("/initiate")
    public ResponseEntity<PaymentHashResponse> initiatePayment(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.initiatePayment(userId, request));
    }

    // PUBLIC — PayHere calls this from their server, no JWT
    @PostMapping("/callback")
    public ResponseEntity<Void> handleCallback(
            @RequestParam String merchant_id,
            @RequestParam String order_id,
            @RequestParam String payment_id,
            @RequestParam String payhere_amount,
            @RequestParam String payhere_currency,
            @RequestParam String status_code,
            @RequestParam String md5sig) {

        paymentService.handleCallback(merchant_id, order_id, payment_id,
                payhere_amount, payhere_currency, status_code, md5sig);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse> getPaymentByBooking(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentByBookingId(bookingId));
    }
}