package com.edu.basic.payment.entity;

import com.edu.basic.booking.entity.Booking;
import com.edu.basic.entity.BaseEntity;
import com.edu.basic.payment.enums.PaymentStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "payments")
public class Payment extends BaseEntity<Long> {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String currency = "LKR";

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @Column(name = "payhere_payment_id")
    private String payherePaymentId;

    @Column(name = "hash")
    private String hash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        if (this.status == null) {
            this.status = PaymentStatus.PENDING;
        }
    }

    // getters and setters
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getPayherePaymentId() { return payherePaymentId; }
    public void setPayherePaymentId(String payherePaymentId) { this.payherePaymentId = payherePaymentId; }
    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
}