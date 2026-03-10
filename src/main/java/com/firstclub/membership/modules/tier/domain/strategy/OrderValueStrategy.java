package com.firstclub.membership.modules.tier.domain.strategy;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.firstclub.membership.enums.RuleType;
import com.firstclub.membership.external.order.MockOrderService;
import com.firstclub.membership.modules.tier.domain.model.TierRule;

@Component
public class OrderValueStrategy implements TierEligibilityStrategy {

    private final MockOrderService orderService;

    public OrderValueStrategy(MockOrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public RuleType getSupportedType() {
        return RuleType.MIN_ORDER_VALUE;
    }

    @Override
    public boolean evaluate(UUID userId, TierRule rule) {
        int periodDays = rule.getPeriodDays() != null ? rule.getPeriodDays() : 30;
        BigDecimal actualValue = orderService.getTotalOrderValue(userId, periodDays);
        BigDecimal threshold = rule.getThresholdValue();
        return actualValue.compareTo(threshold) >= 0;
    }
}
