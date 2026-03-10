package com.firstclub.membership.modules.subscription.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class SubscribeRequest {
    @NotNull(message = "userId is required")
    private UUID userId;

    @NotNull(message = "planId is required")
    private UUID planId;

    private UUID tierId;

    private Boolean autoRenew;

    public SubscribeRequest() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getPlanId() {
        return planId;
    }

    public UUID getTierId() {
        return tierId;
    }

    public void setPlanId(UUID planId) {
        this.planId = planId;
    }

    public Boolean getAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(Boolean autoRenew) {
        this.autoRenew = autoRenew;
    }
}
