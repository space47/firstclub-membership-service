package com.firstclub.membership.modules.tier.domain.strategy;

import java.util.UUID;

import com.firstclub.membership.enums.RuleType;
import com.firstclub.membership.modules.tier.domain.model.TierRule;

/**
 * Strategy Pattern for tier eligibility rules.
 * Each implementation handles one CriteriaType.
 */
public interface TierEligibilityStrategy {
    RuleType getSupportedType();

    boolean evaluate(UUID userId, TierRule rule);
}
