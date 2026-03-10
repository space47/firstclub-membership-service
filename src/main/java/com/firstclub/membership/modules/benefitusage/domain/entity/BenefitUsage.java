package com.firstclub.membership.modules.benefitusage.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.firstclub.membership.common.entity.BaseEntity;
import com.firstclub.membership.enums.BenefitType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * Tracks each time a user consumes a benefit.
 * 
 * The checkout service calls our API to:
 * 1. Check if the user can use a benefit (GET /benefits/check)
 * 2. Record that they used it (POST /benefits/record-usage)
 * 
 * This table answers: "Has user X used FREE_DELIVERY 3 times this month?"
 */
@Entity
@Table(name = "benefit_usage", indexes = {
        @Index(name = "idx_usage_user_type", columnList = "user_id, benefit_type"),
        @Index(name = "idx_usage_user_period", columnList = "user_id, benefit_type, used_at")
})
public class BenefitUsage extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "benefit_type", nullable = false)
    private BenefitType benefitType;

    /**
     * Reference to the order that consumed this benefit.
     * Links back to the Order service — useful for auditing.
     */
    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt;

    public BenefitUsage() {
    }

    public BenefitUsage(UUID userId, BenefitType benefitType, UUID orderId) {
        this.userId = userId;
        this.benefitType = benefitType;
        this.orderId = orderId;
        this.usedAt = LocalDateTime.now();
    }

    // ── Getters & Setters ──

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public BenefitType getBenefitType() {
        return benefitType;
    }

    public void setBenefitType(BenefitType benefitType) {
        this.benefitType = benefitType;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }
}