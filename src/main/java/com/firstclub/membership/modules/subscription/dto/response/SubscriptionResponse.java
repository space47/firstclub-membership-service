package com.firstclub.membership.modules.subscription.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.modules.membershipplan.dto.response.PlanResponse;

public class SubscriptionResponse {
    private UUID id;
    private UUID userId;
    private PlanResponse plan;
    private SubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean autoRenew;
    private LocalDateTime cancelledAt;
    private String cancellationReason;

    public SubscriptionResponse() {
    }

    private SubscriptionResponse(Builder b) {
        this.id = b.id;
        this.userId = b.userId;
        this.plan = b.plan;
        this.status = b.status;
        this.startDate = b.startDate;
        this.endDate = b.endDate;
        this.autoRenew = b.autoRenew;
        this.cancelledAt = b.cancelledAt;
        this.cancellationReason = b.cancellationReason;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public PlanResponse getPlan() {
        return plan;
    }

    public void setPlan(PlanResponse plan) {
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID userId;
        private PlanResponse plan;
        private SubscriptionStatus status;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private boolean autoRenew;
        private LocalDateTime cancelledAt;
        private String cancellationReason;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder plan(PlanResponse plan) {
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

        public SubscriptionResponse build() {
            return new SubscriptionResponse(this);
        }
    }
}