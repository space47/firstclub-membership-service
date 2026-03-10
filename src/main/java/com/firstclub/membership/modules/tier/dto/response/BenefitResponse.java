package com.firstclub.membership.modules.tier.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.firstclub.membership.enums.BenefitType;

public class BenefitResponse {
    private UUID id;
    private BenefitType benefitType;
    private BigDecimal value;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscountCap;
    private Integer maxUsageLimit;
    private String description;

    public BenefitResponse() {
    }

    private BenefitResponse(Builder b) {
        this.id = b.id;
        this.benefitType = b.benefitType;
        this.value = b.value;
        this.minOrderValue = b.minOrderValue;
        this.maxDiscountCap = b.maxDiscountCap;
        this.maxUsageLimit = b.maxUsageLimit;
        this.description = b.description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public void setMinOrderValue(BigDecimal v) {
        this.minOrderValue = v;
    }

    public BigDecimal getMaxDiscountCap() {
        return maxDiscountCap;
    }

    public void setMaxDiscountCap(BigDecimal v) {
        this.maxDiscountCap = v;
    }

    public Integer getMaxUsageLimit() {
        return maxUsageLimit;
    }

    public void setMaxUsageLimit(Integer v) {
        this.maxUsageLimit = v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private BenefitType benefitType;
        private BigDecimal value;
        private BigDecimal minOrderValue;
        private BigDecimal maxDiscountCap;
        private Integer maxUsageLimit;
        private String description;

        public Builder id(UUID id) {
            this.id = id;
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

        public BenefitResponse build() {
            return new BenefitResponse(this);
        }
    }
}
