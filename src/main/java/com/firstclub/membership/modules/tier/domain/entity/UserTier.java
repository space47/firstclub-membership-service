package com.firstclub.membership.modules.tier.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.firstclub.membership.common.entity.BaseEntity;
import com.firstclub.membership.enums.TierLevel;

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
@Table(name = "user_tiers", indexes = {
        @Index(name = "idx_user_tier_user", columnList = "user_id"),
        @Index(name = "idx_user_tier_active", columnList = "user_id, valid_until")
})
public class UserTier extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier_level", nullable = false)
    private TierLevel tierLevel;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @Column(name = "previous_tier_level")
    @Enumerated(EnumType.STRING)
    private TierLevel previousTierLevel;

    @Column(name = "assignment_reason")
    private String assignmentReason;

    @Version
    private Long version;

    public UserTier() {
    }

    private UserTier(Builder builder) {
        this.userId = builder.userId;
        this.tier = builder.tier;
        this.tierLevel = builder.tierLevel;
        this.assignedAt = builder.assignedAt;
        this.validUntil = builder.validUntil;
        this.previousTierLevel = builder.previousTierLevel;
        this.assignmentReason = builder.assignmentReason;
    }

    // ── Getters & Setters ──

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public MembershipTier getTier() {
        return tier;
    }

    public void setTier(MembershipTier tier) {
        this.tier = tier;
    }

    public TierLevel getTierLevel() {
        return tierLevel;
    }

    public void setTierLevel(TierLevel tierLevel) {
        this.tierLevel = tierLevel;
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

    public void setPreviousTierLevel(TierLevel previousTierLevel) {
        this.previousTierLevel = previousTierLevel;
    }

    public String getAssignmentReason() {
        return assignmentReason;
    }

    public void setAssignmentReason(String assignmentReason) {
        this.assignmentReason = assignmentReason;
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
        private MembershipTier tier;
        private TierLevel tierLevel;
        private LocalDateTime assignedAt;
        private LocalDateTime validUntil;
        private TierLevel previousTierLevel;
        private String assignmentReason;

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder tier(MembershipTier tier) {
            this.tier = tier;
            return this;
        }

        public Builder tierLevel(TierLevel tierLevel) {
            this.tierLevel = tierLevel;
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

        public UserTier build() {
            return new UserTier(this);
        }
    }
}
