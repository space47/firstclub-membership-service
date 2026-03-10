package com.firstclub.membership.modules.tier.dto.request;

import java.util.List;

import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.modules.tier.domain.entity.TierBenefit;
import com.firstclub.membership.modules.tier.domain.model.TierEligibilityRules;

public class UpdateTierRequest {
    private String name;
    private String description;
    private TierLevel tierLevel;
    private List<TierBenefit> tierBenefits;

    private TierEligibilityRules eligibilityRules;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<TierBenefit> getTierBenefits() {
        return tierBenefits;
    }

    public TierEligibilityRules getEligibilityRules() {
        return eligibilityRules;
    }

    public TierLevel getTierLevel() {
        return tierLevel;
    }

}
