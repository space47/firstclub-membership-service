package com.firstclub.membership.modules.tier.domain.model;

import java.util.List;

public class TierEligibilityRules {

    private List<TierRule> rules;

    public TierEligibilityRules() {
    }

    public List<TierRule> getRules() {
        return rules;
    }

    public void setRules(List<TierRule> rules) {
        this.rules = rules;
    }
}