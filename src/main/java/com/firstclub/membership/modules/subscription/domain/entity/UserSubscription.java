package com.firstclub.membership.modules.subscription.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.firstclub.membership.common.entity.BaseEntity;
import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.modules.membershipplan.domain.entity.MembershipPlan;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "user_subscriptions", indexes = {
        @Index(name = "idx_user_status", columnList = "user_id, status"),
        @Index(name = "idx_subscription_expiry", columnList = "end_date")
})
public class UserSubscription extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false)
    private MembershipPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status = SubscriptionStatus.PENDING;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "auto_renew")
    private boolean autoRenew = true;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Version
    private Long version;

    public UserSubscription() {
    }

    private UserSubscription(Builder builder) {
        this.userId = builder.userId;
        this.plan = builder.plan;
        this.status = builder.status;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.autoRenew = builder.autoRenew;
        this.cancelledAt = builder.cancelledAt;
        this.cancellationReason = builder.cancellationReason;
    }

    /**
     * Convenience: is this subscription currently valid?
     */
    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE
                && LocalDateTime.now().isBefore(endDate);
    }

    // ── Getters & Setters ──

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public MembershipPlan getPlan() {
        return plan;
    }

    public void setPlan(MembershipPlan plan) {
        this.plan = plan;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String reason) {
        this.cancellationReason = reason;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    // ── Builder ──

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID userId;
        private MembershipPlan plan;
        private SubscriptionStatus status = SubscriptionStatus.PENDING;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private boolean autoRenew = true;
        private LocalDateTime cancelledAt;
        private String cancellationReason;

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder plan(MembershipPlan plan) {
            this.plan = plan;
            return this;
        }

        public Builder status(SubscriptionStatus status) {
            this.status = status;
            return this;
        }

        public Builder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder autoRenew(boolean autoRenew) {
            this.autoRenew = autoRenew;
            return this;
        }

        public Builder cancelledAt(LocalDateTime cancelledAt) {
            this.cancelledAt = cancelledAt;
            return this;
        }

        public Builder cancellationReason(String reason) {
            this.cancellationReason = reason;
            return this;
        }

        public UserSubscription build() {
            return new UserSubscription(this);
        }
    }
}
