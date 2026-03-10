package com.firstclub.membership.modules.membershipplan.domain.entity;

import java.math.BigDecimal;

import com.firstclub.membership.common.entity.BaseEntity;
import com.firstclub.membership.enums.PlanDuration;
import com.firstclub.membership.modules.membershipplan.dto.request.UpdatePlanRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "membership_plans", indexes = {
        @Index(name = "idx_plan_duration", columnList = "duration"),
        @Index(name = "idx_plan_active", columnList = "active")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_plan_name", columnNames = "name")
})
public class MembershipPlan extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanDuration duration;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "auto_renew_default")
    private boolean autoRenewDefault = true;

    public MembershipPlan() {
    }

    private MembershipPlan(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.duration = builder.duration;
        this.price = builder.price;
        this.active = builder.active;
        this.autoRenewDefault = builder.autoRenewDefault;
    }

    // ── Getters & Setters ──

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAutoRenewDefault() {
        return autoRenewDefault;
    }

    public void setAutoRenewDefault(boolean autoRenewDefault) {
        this.autoRenewDefault = autoRenewDefault;
    }

    // ── Builder ──

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String description;
        private PlanDuration duration;
        private BigDecimal price;
        private boolean active = true;
        private boolean autoRenewDefault = true;

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

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Builder autoRenewDefault(boolean autoRenewDefault) {
            this.autoRenewDefault = autoRenewDefault;
            return this;
        }

        public MembershipPlan build() {
            return new MembershipPlan(this);
        }
    }

    public void updateFrom(UpdatePlanRequest request) {
        if (request.getName() != null)
            this.name = request.getName();
        if (request.getDescription() != null)
            this.description = request.getDescription();
        if (request.getDuration() != null)
            this.duration = request.getDuration();
        if (request.getPrice() != null)
            this.price = request.getPrice();
        if (request.getActive() != null)
            this.active = request.getActive();
        if (request.getAutoRenewDefault() != null)
            this.autoRenewDefault = request.getAutoRenewDefault();
    }
}
