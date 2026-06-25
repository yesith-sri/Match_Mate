package com.edu.basic.payment.service.impl;

import com.edu.basic.booking.entity.Booking;
import com.edu.basic.booking.enums.BookingStatus;
import com.edu.basic.booking.repositary.BookingRepository;
import com.edu.basic.booking.service.BookingService;
import com.edu.basic.exception.BusinessException;
import com.edu.basic.exception.ErrorCode;
import com.edu.basic.exception.ResourceNotFoundException;
import com.edu.basic.exception.UnauthorizedException;
import com.edu.basic.payment.dto.request.PaymentRequest;
import com.edu.basic.payment.dto.response.PaymentHashResponse;
import com.edu.basic.payment.dto.response.PaymentResponse;
import com.edu.basic.payment.entity.Payment;
import com.edu.basic.payment.enums.PaymentStatus;
import com.edu.basic.payment.repositary.PaymentRepository;
import com.edu.basic.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    @Value("${payhere.merchant.id}")
    private String merchantId;

    @Value("${payhere.merchant.secret}")
    private String merchantSecret;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              BookingRepository bookingRepository,
                              BookingService bookingService) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
    }

    @Override
    @Transactional
    public PaymentHashResponse initiatePayment(Long userId, PaymentRequest request) {

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.BOOKING_NOT_FOUND, "Booking not found with id: " + request.getBookingId()));

        if (!booking.getUser().getId().equals(userId)) {
            throw new UnauthorizedException(ErrorCode.ACCESS_DENIED,
                    "You are not authorized to pay for this booking");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST,
                    "Booking is not in PENDING state");
        }

        paymentRepository.findByBooking_Id(booking.getId()).ifPresent(p -> {
            if (p.getStatus() == PaymentStatus.COMPLETED) {
                throw new BusinessException(ErrorCode.PAYMENT_ALREADY_PROCESSED,
                        "Payment already completed for this booking");
            }
        });

        String orderId = "MM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String formattedAmount = new BigDecimal(request.getAmount())
                .setScale(2, RoundingMode.HALF_UP).toPlainString();
        String hash = generateHash(merchantId, orderId, formattedAmount, "LKR");

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(request.getAmount());
        payment.setCurrency("LKR");
        payment.setOrderId(orderId);
        payment.setHash(hash);
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        return new PaymentHashResponse(orderId, request.getAmount(), "LKR", hash, merchantId);
    }

    @Override
    @Transactional
    public void handleCallback(String merchantId, String orderId, String payherePaymentId,
                               String payhereAmount, String payhereCurrency,
                               String statusCode, String md5sig) {

        String expectedSig = generateCallbackHash(merchantId, orderId,
                payhereAmount, payhereCurrency, statusCode);

        if (!expectedSig.equals(md5sig)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST,
                    "Invalid MD5 signature — possible fraud attempt");
        }

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.PAYMENT_NOT_FOUND, "Payment not found for order: " + orderId));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            return;
        }

        if ("2".equals(statusCode)) {
            payment.setPayherePaymentId(payherePaymentId);
            payment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);
            bookingService.confirmBooking(payment.getBooking().getId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        }
    }

    @Override
    public PaymentResponse getPaymentByBookingId(Long bookingId) {
        Payment payment = paymentRepository.findByBooking_Id(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.PAYMENT_NOT_FOUND, "Payment not found for booking: " + bookingId));

        return new PaymentResponse(
                payment.getId(),
                payment.getBooking().getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }

    private String generateHash(String merchantId, String orderId, String amount, String currency) {
        String secretHash = md5(merchantSecret).toUpperCase();
        return md5(merchantId + orderId + amount + currency + secretHash).toUpperCase();
    }

    private String generateCallbackHash(String merchantId, String orderId,
                                        String amount, String currency, String statusCode) {
        String secretHash = md5(merchantSecret).toUpperCase();
        return md5(merchantId + orderId + amount + currency + statusCode + secretHash).toUpperCase();
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "MD5 hashing failed");
        }
    }
}