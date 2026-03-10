package com.firstclub.membership.modules.tier.domain.entity;

import java.math.BigDecimal;

import com.firstclub.membership.common.entity.BaseEntity;
import com.firstclub.membership.enums.BenefitType;
import com.firstclub.membership.modules.tier.dto.request.UpdateTierBenefitRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * A single configurable benefit attached to a tier.
 * 
 * Field meanings vary by BenefitType:
 * 
 * FREE_DELIVERY:
 * value = 1 (enabled)
 * minOrderValue = 500 (free delivery only on orders above ₹500)
 * maxUsageLimit = null (unlimited) or 5 (5 free deliveries/month)
 * 
 * DISCOUNT_PERCENTAGE:
 * value = 10 (10% discount)
 * minOrderValue = 200 (discount applies on orders above ₹200)
 * maxDiscountCap = 500 (max ₹500 discount per order)
 * 
 * CASHBACK_PERCENTAGE:
 * value = 3 (3% cashback)
 * minOrderValue = 100
 * maxDiscountCap = 200 (max ₹200 cashback per order)
 * 
 * PRIORITY_SUPPORT / EXCLUSIVE_DEALS / EARLY_ACCESS:
 * value = 1 (enabled, boolean-as-number)
 * minOrderValue = null (not applicable)
 * 
 * This makes benefits machine-readable — the checkout service queries
 * these fields directly instead of parsing description strings.
 */
@Entity
@Table(name = "tier_benefits")
public class TierBenefit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Enumerated(EnumType.STRING)
    @Column(name = "benefit_type", nullable = false)
    private BenefitType benefitType;

    /**
     * Primary value of the benefit.
     * Meaning depends on type: percentage, boolean (1/0), count, etc.
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal value = BigDecimal.ZERO;

    /**
     * Minimum order value required to activate this benefit.
     * e.g., FREE_DELIVERY only on orders above ₹500.
     * Null = no minimum (benefit always applies).
     */
    @Column(name = "min_order_value", precision = 10, scale = 2)
    private BigDecimal minOrderValue;

    /**
     * Maximum discount/cashback amount per order.
     * e.g., 10% discount but capped at ₹500 max.
     * Null = no cap.
     */
    @Column(name = "max_discount_cap", precision = 10, scale = 2)
    private BigDecimal maxDiscountCap;

    /**
     * Maximum number of times this benefit can be used per billing cycle.
     * e.g., 5 free deliveries per month.
     * Null = unlimited usage.
     */
    @Column(name = "max_usage_limit")
    private Integer maxUsageLimit;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    public TierBenefit() {
    }

    private TierBenefit(Builder builder) {
        this.tier = builder.tier;
        this.benefitType = builder.benefitType;
        this.value = builder.value;
        this.minOrderValue = builder.minOrderValue;
        this.maxDiscountCap = builder.maxDiscountCap;
        this.maxUsageLimit = builder.maxUsageLimit;
        this.description = builder.description;
        this.active = builder.active;
    }

    // ── Getters & Setters ──

    public MembershipTier getTier() {
        return tier;
    }

    public void setTier(MembershipTier tier) {
        this.tier = tier;
    }

    public BenefitType getBenefitType() {
        return benefitType;
    }

    public void setBenefitType(BenefitType benefitType) {
        this.benefitType = benefitType;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getMinOrderValue() {
        return minOrderValue;
    }

    public void setMinOrderValue(BigDecimal minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public BigDecimal getMaxDiscountCap() {
        return maxDiscountCap;
    }

    public void setMaxDiscountCap(BigDecimal maxDiscountCap) {
        this.maxDiscountCap = maxDiscountCap;
    }

    public Integer getMaxUsageLimit() {
        return maxUsageLimit;
    }

    public void setMaxUsageLimit(Integer maxUsageLimit) {
        this.maxUsageLimit = maxUsageLimit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // ── Builder ──

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private MembershipTier tier;
        private BenefitType benefitType;
        private BigDecimal value = BigDecimal.ZERO;
        private BigDecimal minOrderValue;
        private BigDecimal maxDiscountCap;
        private Integer maxUsageLimit;
        private String description;
        private boolean active = true;

        public Builder tier(MembershipTier tier) {
            this.tier = tier;
            return this;
        }

        public Builder benefitType(BenefitType benefitType) {
            this.benefitType = benefitType;
            return this;
        }

        public Builder value(BigDecimal value) {
            this.value = value;
            return this;
        }

        public Builder minOrderValue(BigDecimal v) {
            this.minOrderValue = v;
            return this;
        }

        public Builder maxDiscountCap(BigDecimal v) {
            this.maxDiscountCap = v;
            return this;
        }

        public Builder maxUsageLimit(Integer v) {
            this.maxUsageLimit = v;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public TierBenefit build() {
            return new TierBenefit(this);
        }
    }

    public void updateFrom(UpdateTierBenefitRequest request) {
        if (request.getBenefitType() != null)
            this.benefitType = request.getBenefitType();
        if (request.getValue() != null)
            this.value = request.getValue();
        if (request.getMinOrderValue() != null)
            this.minOrderValue = request.getMinOrderValue();
        if (request.getMaxDiscountCap() != null)
            this.maxDiscountCap = request.getMaxDiscountCap();
        if (request.getMaxUsageLimit() != null)
            this.maxUsageLimit = request.getMaxUsageLimit();
        if (request.getDescription() != null)
            this.description = request.getDescription();
    }
}