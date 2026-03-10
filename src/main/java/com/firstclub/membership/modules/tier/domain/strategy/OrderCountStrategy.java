package com.firstclub.membership.modules.tier.domain.strategy;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.firstclub.membership.enums.RuleType;
import com.firstclub.membership.external.order.MockOrderService;
import com.firstclub.membership.modules.tier.domain.model.TierRule;

@Component
public class OrderCountStrategy implements TierEligibilityStrategy {

    private final MockOrderService orderService;

    public OrderCountStrategy(MockOrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public RuleType getSupportedType() {
        return RuleType.MIN_ORDER_COUNT;
    }

    @Override
    public boolean evaluate(UUID userId, TierRule rule) {
        int periodDays = rule.getPeriodDays() != null ? rule.getPeriodDays() : 30;
        int actualCount = orderService.getOrderCount(userId, periodDays);
        int threshold = rule.getThresholdValue().intValue();
        return actualCount >= threshold;
    }
}
