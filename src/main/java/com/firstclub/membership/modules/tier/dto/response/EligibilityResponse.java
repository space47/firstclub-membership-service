package com.firstclub.membership.modules.tier.dto.response;

import java.util.List;
import java.util.UUID;

import com.firstclub.membership.enums.TierLevel;

public class EligibilityResponse {
    private UUID userId;
    private TierLevel tierLevel;
    private boolean eligible;
    private List<String> failedCriteria;

    public EligibilityResponse() {
    }

    public EligibilityResponse(UUID userId, TierLevel tierLevel,
            boolean eligible, List<String> failedCriteria) {
        this.userId = userId;
        this.tierLevel = tierLevel;
        this.eligible = eligible;
        this.failedCriteria = failedCriteria;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public TierLevel getTierLevel() {
        return tierLevel;
    }

    public void setTierLevel(TierLevel tierLevel) {
        this.tierLevel = tierLevel;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public List<String> getFailedCriteria() {
        return failedCriteria;
    }

    public void setFailedCriteria(List<String> failedCriteria) {
        this.failedCriteria = failedCriteria;
    }
}
