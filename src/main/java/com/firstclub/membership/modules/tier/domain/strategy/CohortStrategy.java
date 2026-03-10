package com.firstclub.membership.modules.tier.domain.strategy;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.firstclub.membership.enums.RuleType;
import com.firstclub.membership.external.order.MockOrderService;
import com.firstclub.membership.modules.tier.domain.model.TierRule;

@Component
public class CohortStrategy implements TierEligibilityStrategy {

    private final MockOrderService orderService;

    public CohortStrategy(MockOrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public RuleType getSupportedType() {
        return RuleType.USER_COHORT;
    }

    @Override
    public boolean evaluate(UUID userId, TierRule criteria) {
        if (criteria.getCohortName() == null || criteria.getCohortName().isBlank()) {
            return true;
        }
        return orderService.isUserInCohort(userId, criteria.getCohortName());
    }
}
