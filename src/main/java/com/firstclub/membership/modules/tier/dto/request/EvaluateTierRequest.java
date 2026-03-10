package com.firstclub.membership.modules.tier.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class EvaluateTierRequest {
    @NotNull(message = "userId is required")
    private UUID userId;

    public EvaluateTierRequest() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
