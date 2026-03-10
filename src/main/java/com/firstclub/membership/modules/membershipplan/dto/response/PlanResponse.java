package com.firstclub.membership.modules.membershipplan.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.firstclub.membership.enums.PlanDuration;

public class PlanResponse {
    private UUID id;
    private String name;
    private String description;
    private PlanDuration duration;
    private BigDecimal price;
    private boolean autoRenewDefault;

    public PlanResponse() {
    }

    private PlanResponse(Builder b) {
        this.id = b.id;
        this.name = b.name;
        this.description = b.description;
        this.duration = b.duration;
        this.price = b.price;
        this.autoRenewDefault = b.autoRenewDefault;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PlanDuration getDuration() {
        return duration;
    }

    public void setDuration(PlanDuration duration) {
        this.duration = duration;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isAutoRenewDefault() {
        return autoRenewDefault;
    }

    public void setAutoRenewDefault(boolean v) {
        this.autoRenewDefault = v;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String name;
        private String description;
        private PlanDuration duration;
        private BigDecimal price;
        private boolean autoRenewDefault;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder duration(PlanDuration duration) {
            this.duration = duration;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder autoRenewDefault(boolean v) {
            this.autoRenewDefault = v;
            return this;
        }

        public PlanResponse build() {
            return new PlanResponse(this);
        }
    }
}