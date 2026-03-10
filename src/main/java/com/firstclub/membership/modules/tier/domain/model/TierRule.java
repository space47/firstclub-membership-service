package com.firstclub.membership.modules.tier.domain.model;

import java.math.BigDecimal;

import com.firstclub.membership.enums.RuleType;

public class TierRule {

    private RuleType type;
    private BigDecimal thresholdValue;
    private Integer periodDays;
    private String cohortName;

    public TierRule() {
    }

    public TierRule(RuleType type, BigDecimal thresholdValue, Integer periodDays, String cohortName) {
        this.type = type;
        this.thresholdValue = thresholdValue;
        this.periodDays = periodDays;
        this.cohortName = cohortName;
    }

    public RuleType getType() {
        return type;
    }

    public void setType(RuleType type) {
        this.type = type;
    }

    public BigDecimal getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(BigDecimal thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public Integer getPeriodDays() {
        return periodDays;
    }

    public void setPeriodDays(Integer periodDays) {
        this.periodDays = periodDays;
    }

    public String getCohortName() {
        return cohortName;
    }

    public void setCohortName(String cohortName) {
        this.cohortName = cohortName;
    }

    // -------- Builder --------

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private RuleType type;
        private BigDecimal thresholdValue;
        private Integer periodDays;
        private String cohortName;

        public Builder type(RuleType type) {
            this.type = type;
            return this;
        }

        public Builder thresholdValue(BigDecimal thresholdValue) {
            this.thresholdValue = thresholdValue;
            return this;
        }

        public Builder periodDays(Integer periodDays) {
            this.periodDays = periodDays;
            return this;
        }

        public Builder cohortName(String cohortName) {
            this.cohortName = cohortName;
            return this;
        }

        public TierRule build() {
            return new TierRule(type, thresholdValue, periodDays, cohortName);
        }
    }
}