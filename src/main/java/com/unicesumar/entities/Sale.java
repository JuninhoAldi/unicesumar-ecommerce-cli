package com.unicesumar.entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class Sale extends Entity {
    private UUID userId;
    private String paymentMethod;
    private LocalDateTime saleDate;

    public Sale(UUID userId, String paymentMethod, LocalDateTime saleDate) {
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.saleDate = saleDate;
    }

    public Sale(UUID uuid, UUID userId, String paymentMethod, LocalDateTime saleDate) {
        super(uuid);
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.saleDate = saleDate;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    @Override
    public String toString() {
        return String.format("Venda ID: %s\nUsuário: %s\nForma de pagamento: %s\nData: %s",
            getUuid(), userId, paymentMethod, saleDate.toString());
    }
}
