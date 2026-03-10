package com.firstclub.membership.modules.subscription.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class CancelRequest {
    @NotNull(message = "userId is required")
    private UUID userId;

    private String reason;

    public CancelRequest() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}