package com.firstclub.membership.modules.tier.dto.response;

import java.util.List;
import java.util.UUID;

import com.firstclub.membership.enums.TierLevel;

public class TierResponse {
    private UUID id;
    private String name;
    private TierLevel tierLevel;
    private String description;
    private List<BenefitResponse> benefits;
    private List<CriteriaResponse> eligibilityCriteria;

    public TierResponse() {
    }

    private TierResponse(Builder b) {
        this.id = b.id;
        this.name = b.name;
        this.tierLevel = b.tierLevel;
        this.description = b.description;
        this.benefits = b.benefits;
        this.eligibilityCriteria = b.eligibilityCriteria;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public List<BenefitResponse> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<BenefitResponse> benefits) {
        this.benefits = benefits;
    }

    public List<CriteriaResponse> getEligibilityCriteria() {
        return eligibilityCriteria;
    }

    public void setEligibilityCriteria(List<CriteriaResponse> c) {
        this.eligibilityCriteria = c;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String name;
        private TierLevel tierLevel;
        private String description;
        private List<BenefitResponse> benefits;
        private List<CriteriaResponse> eligibilityCriteria;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

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

        public Builder benefits(List<BenefitResponse> benefits) {
            this.benefits = benefits;
            return this;
        }

        public Builder eligibilityCriteria(List<CriteriaResponse> c) {
            this.eligibilityCriteria = c;
            return this;
        }

        public TierResponse build() {
            return new TierResponse(this);
        }
    }
}
