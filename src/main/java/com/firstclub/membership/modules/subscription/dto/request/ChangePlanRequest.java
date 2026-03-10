package com.firstclub.membership.modules.subscription.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class ChangePlanRequest {
    @NotNull(message = "userId is required")
    private UUID userId;

    @NotNull(message = "newPlanId is required")
    private UUID newPlanId;

    public ChangePlanRequest() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getNewPlanId() {
        return newPlanId;
    }

    public void setNewPlanId(UUID newPlanId) {
        this.newPlanId = newPlanId;
    }
}
