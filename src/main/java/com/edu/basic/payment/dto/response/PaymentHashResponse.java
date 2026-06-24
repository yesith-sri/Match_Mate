package com.edu.basic.payment.dto.response;

public class PaymentHashResponse {

    private String orderId;
    private Double amount;
    private String currency;
    private String hash;
    private String merchantId;

    public PaymentHashResponse(String orderId, Double amount,
                               String currency, String hash, String merchantId) {
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.hash = hash;
        this.merchantId = merchantId;
    }

    // getters and setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
}