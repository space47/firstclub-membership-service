package com.firstclub.membership.modules.tier.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.firstclub.membership.enums.TierLevel;

public class UserTierResponse {
    private UUID userId;
    private TierLevel tierLevel;
    private String tierName;
    private LocalDateTime assignedAt;
    private LocalDateTime validUntil;
    private TierLevel previousTierLevel;
    private String assignmentReason;
    private List<BenefitResponse> activeBenefits;

    public UserTierResponse() {
    }

    private UserTierResponse(Builder b) {
        this.userId = b.userId;
        this.tierLevel = b.tierLevel;
        this.tierName = b.tierName;
        this.assignedAt = b.assignedAt;
        this.validUntil = b.validUntil;
        this.previousTierLevel = b.previousTierLevel;
        this.assignmentReason = b.assignmentReason;
        this.activeBenefits = b.activeBenefits;
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

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public TierLevel getPreviousTierLevel() {
        return previousTierLevel;
    }

    public void setPreviousTierLevel(TierLevel prev) {
        this.previousTierLevel = prev;
    }

    public String getAssignmentReason() {
        return assignmentReason;
    }

    public void setAssignmentReason(String reason) {
        this.assignmentReason = reason;
    }

    public List<BenefitResponse> getActiveBenefits() {
        return activeBenefits;
    }

    public void setActiveBenefits(List<BenefitResponse> benefits) {
        this.activeBenefits = benefits;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID userId;
        private TierLevel tierLevel;
        private String tierName;
        private LocalDateTime assignedAt;
        private LocalDateTime validUntil;
        private TierLevel previousTierLevel;
        private String assignmentReason;
        private List<BenefitResponse> activeBenefits;

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder tierLevel(TierLevel tierLevel) {
            this.tierLevel = tierLevel;
            return this;
        }

        public Builder tierName(String tierName) {
            this.tierName = tierName;
            return this;
        }

        public Builder assignedAt(LocalDateTime assignedAt) {
            this.assignedAt = assignedAt;
            return this;
        }

        public Builder validUntil(LocalDateTime validUntil) {
            this.validUntil = validUntil;
            return this;
        }

        public Builder previousTierLevel(TierLevel prev) {
            this.previousTierLevel = prev;
            return this;
        }

        public Builder assignmentReason(String reason) {
            this.assignmentReason = reason;
            return this;
        }

        public Builder activeBenefits(List<BenefitResponse> benefits) {
            this.activeBenefits = benefits;
            return this;
        }

        public UserTierResponse build() {
            return new UserTierResponse(this);
        }
    }
}
