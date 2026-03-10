package com.firstclub.membership.modules.tier.dto.request;

import java.math.BigDecimal;

import com.firstclub.membership.enums.BenefitType;

public class UpdateTierBenefitRequest {

    private BenefitType benefitType;

    private BigDecimal value;

    private BigDecimal minOrderValue;

    private BigDecimal maxDiscountCap;

    private Integer maxUsageLimit;

    private String description;

    public BenefitType getBenefitType() {
        return benefitType;
    }

    public BigDecimal getValue() {
        return value;
    }

    public BigDecimal getMinOrderValue() {
        return minOrderValue;
    }

    public BigDecimal getMaxDiscountCap() {
        return maxDiscountCap;
    }

    public Integer getMaxUsageLimit() {
        return maxUsageLimit;
    }

    public String getDescription() {
        return description;
    }
}