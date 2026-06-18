package com.edu.basic.payment.service;

import com.edu.basic.payment.dto.request.PaymentRequest;
import com.edu.basic.payment.dto.response.PaymentHashResponse;
import com.edu.basic.payment.dto.response.PaymentResponse;

public interface PaymentService {

    // Step 1: frontend calls this → gets hash + orderId → launches PayHere popup
    PaymentHashResponse initiatePayment(Long userId, PaymentRequest request);

    // Step 2: PayHere calls this automatically after payment
    void handleCallback(String merchantId, String orderId, String payherePaymentId,
                        String payhereAmount, String payhereCurrency,
                        String statusCode, String md5sig);

    PaymentResponse getPaymentByBookingId(Long bookingId);
}