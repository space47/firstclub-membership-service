package com.firstclub.membership.modules.tier.domain.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.firstclub.membership.common.entity.BaseEntity;
import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.modules.tier.domain.model.TierEligibilityRules;
import com.firstclub.membership.modules.tier.dto.request.UpdateTierRequest;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "membership_tiers", indexes = {
        @Index(name = "idx_tier_level", columnList = "tier_level", unique = true)
})
public class MembershipTier extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier_level", nullable = false, unique = true)
    private TierLevel tierLevel;

    @Column(length = 500)
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private TierEligibilityRules eligibilityRules;

    @OneToMany(mappedBy = "tier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TierBenefit> benefits = new ArrayList<>();

    public MembershipTier() {
    }

    private MembershipTier(Builder builder) {
        this.name = builder.name;
        this.tierLevel = builder.tierLevel;
        this.description = builder.description;
        this.benefits = builder.benefits;
        this.eligibilityRules = builder.eligibilityRules;
    }

    // ── Getters & Setters ──

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TierLevel getTierLevel() {
        return tierLevel;
    }

    public void setTierLevel(TierLevel tierLevel) {
        this.tierLevel = tierLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TierEligibilityRules getEligibilityRules() {
        return eligibilityRules;
    }

    public void setEligibilityRules(TierEligibilityRules eligibilityRules) {
        this.eligibilityRules = eligibilityRules;
    }

    public List<TierBenefit> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<TierBenefit> benefits) {
        this.benefits = benefits;
    }

    // ── Builder ──

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private TierLevel tierLevel;
        private String description;
        private List<TierBenefit> benefits = new ArrayList<>();
        private TierEligibilityRules eligibilityRules;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder tierLevel(TierLevel tierLevel) {
            this.tierLevel = tierLevel;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder eligibilityRules(TierEligibilityRules eligibilityRules) {
            this.eligibilityRules = eligibilityRules;
            return this;
        }

        public Builder benefits(List<TierBenefit> benefits) {
            this.benefits = benefits;
            return this;
        }

        public MembershipTier build() {
            return new MembershipTier(this);
        }
    }

    public void addBenefit(TierBenefit benefit) {
        benefits.add(benefit);
        benefit.setTier(this);
    }

    public void clearBenefits() {
        for (TierBenefit benefit : benefits) {
            benefit.setTier(null);
        }
        benefits.clear();
    }

    public void updateFrom(UpdateTierRequest request) {
        if (request.getName() != null)
            this.name = request.getName();
        if (request.getDescription() != null)
            this.description = request.getDescription();
        if (request.getTierLevel() != null)
            this.tierLevel = request.getTierLevel();
        if (request.getTierBenefits() != null) {
            this.clearBenefits();
            request.getTierBenefits().forEach(this::addBenefit);
        }
        if (request.getEligibilityRules() != null)
            this.eligibilityRules = request.getEligibilityRules();
    }
}
