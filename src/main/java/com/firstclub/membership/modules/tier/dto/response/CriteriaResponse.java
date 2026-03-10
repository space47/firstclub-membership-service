package com.firstclub.membership.modules.tier.dto.response;

import java.math.BigDecimal;

import com.firstclub.membership.enums.RuleType;

public class CriteriaResponse {
    private RuleType criteriaType;
    private BigDecimal thresholdValue;
    private String cohortName;
    private Integer periodDays;

    public CriteriaResponse() {
    }

    private CriteriaResponse(Builder b) {
        this.criteriaType = b.criteriaType;
        this.thresholdValue = b.thresholdValue;
        this.cohortName = b.cohortName;
        this.periodDays = b.periodDays;
    }

    public RuleType getCriteriaType() {
        return criteriaType;
    }

    public void setCriteriaType(RuleType criteriaType) {
        this.criteriaType = criteriaType;
    }

    public BigDecimal getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(BigDecimal thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public String getCohortName() {
        return cohortName;
    }

    public void setCohortName(String cohortName) {
        this.cohortName = cohortName;
    }

    public Integer getPeriodDays() {
        return periodDays;
    }

    public void setPeriodDays(Integer periodDays) {
        this.periodDays = periodDays;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private RuleType criteriaType;
        private BigDecimal thresholdValue;
        private String cohortName;
        private Integer periodDays;

        public Builder criteriaType(RuleType criteriaType) {
            this.criteriaType = criteriaType;
            return this;
        }

        public Builder thresholdValue(BigDecimal thresholdValue) {
            this.thresholdValue = thresholdValue;
            return this;
        }

        public Builder cohortName(String cohortName) {
            this.cohortName = cohortName;
            return this;
        }

        public Builder periodDays(Integer periodDays) {
            this.periodDays = periodDays;
            return this;
        }

        public CriteriaResponse build() {
            return new CriteriaResponse(this);
        }
    }
}
