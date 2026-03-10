package com.firstclub.membership.modules.tier.domain.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.firstclub.membership.enums.RuleType;
import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.modules.tier.domain.entity.MembershipTier;
import com.firstclub.membership.modules.tier.domain.model.TierEligibilityRules;
import com.firstclub.membership.modules.tier.domain.model.TierRule;
import com.firstclub.membership.modules.tier.domain.repository.MembershipTierRepository;

/**
 * Evaluates all tiers (highest first) and returns the best tier
 * the user qualifies for. Uses registered strategies as plugins.
 */
@Component
public class TierEligibilityEvaluator {

    private static final Logger log = LoggerFactory.getLogger(TierEligibilityEvaluator.class);

    private final MembershipTierRepository tierRepository;
    private final Map<RuleType, TierEligibilityStrategy> strategyMap;

    public TierEligibilityEvaluator(
            MembershipTierRepository tierRepository,
            List<TierEligibilityStrategy> strategies) {
        this.tierRepository = tierRepository;
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        TierEligibilityStrategy::getSupportedType,
                        s -> s));
        log.info("Loaded {} tier eligibility strategies: {}", strategyMap.size(), strategyMap.keySet());
    }

    /**
     * Result of evaluating a user's eligibility for a specific tier.
     * Contains pass/fail status and details about which criteria were not met.
     */
    public static class EligibilityResult {
        private final boolean eligible;
        private final TierLevel tierLevel;
        private final List<String> failedCriteria;

        public EligibilityResult(boolean eligible, TierLevel tierLevel, List<String> failedCriteria) {
            this.eligible = eligible;
            this.tierLevel = tierLevel;
            this.failedCriteria = failedCriteria;
        }

        public boolean isEligible() {
            return eligible;
        }

        public TierLevel getTierLevel() {
            return tierLevel;
        }

        public List<String> getFailedCriteria() {
            return failedCriteria;
        }
    }

    /**
     * Auto-evaluate: find the highest tier the user qualifies for.
     */
    public TierLevel evaluate(UUID userId) {
        List<MembershipTier> tiers = tierRepository.findAll().stream()
                .sorted(Comparator.comparingInt((MembershipTier t) -> t.getTierLevel().getRank()).reversed())
                .toList();

        for (MembershipTier tier : tiers) {

            log.debug("Evaluating tier {} for user {}", tier.getTierLevel(), userId);

            TierEligibilityRules rulesWrapper = tier.getEligibilityRules();

            // No rules → user qualifies automatically
            if (rulesWrapper == null || rulesWrapper.getRules() == null || rulesWrapper.getRules().isEmpty()) {
                log.debug("Tier {} has no rules — user qualifies by default", tier.getTierLevel());
                return tier.getTierLevel();
            }

            try {

                List<TierRule> rules = rulesWrapper.getRules();

                boolean allMet = rules.stream()
                        .allMatch(rule -> evaluateSingleRule(userId, rule));

                if (allMet) {
                    log.info("User {} qualifies for tier {}", userId, tier.getTierLevel());
                    return tier.getTierLevel();
                }

            } catch (Exception e) {

                log.error("Failed to evaluate rules for tier {}", tier.getTierLevel(), e);

            }
        }

        log.info("User {} did not qualify for any tier — defaulting to SILVER", userId);
        return TierLevel.SILVER;
    }

    /**
     * Check if a user is eligible for a specific tier (by tier ID).
     * Returns detailed result including which criteria failed.
     * 
     * Used when the user explicitly selects a tier during subscription.
     */
    public EligibilityResult evaluateForTier(UUID userId, UUID tierId) {
        MembershipTier tier = tierRepository.findById(tierId)
                .orElseThrow(() -> new com.firstclub.membership.common.exception.ResourceNotFoundException(
                        "Tier not found: " + tierId));

        return evaluateForTier(userId, tier);
    }

    public EligibilityResult evaluateForTier(UUID userId, MembershipTier tier) {

        TierEligibilityRules rulesWrapper = tier.getEligibilityRules();

        // No rules = everyone qualifies
        if (rulesWrapper == null || rulesWrapper.getRules() == null || rulesWrapper.getRules().isEmpty()) {
            return new EligibilityResult(true, tier.getTierLevel(), Collections.emptyList());
        }

        List<String> failedCriteria = new ArrayList<>();

        try {

            List<TierRule> rules = rulesWrapper.getRules();

            for (TierRule rule : rules) {

                boolean passed = evaluateSingleRule(userId, rule);

                if (!passed) {
                    failedCriteria.add(buildFailureMessage(rule));
                }
            }

        } catch (Exception e) {
            log.error("Failed to evaluate eligibility rules for tier {}", tier.getTierLevel(), e);
            return new EligibilityResult(false, tier.getTierLevel(),
                    List.of("Invalid tier rule configuration"));
        }

        boolean eligible = failedCriteria.isEmpty();

        log.info("User {} eligibility for {}: {} (failed: {})",
                userId, tier.getTierLevel(), eligible, failedCriteria);

        return new EligibilityResult(eligible, tier.getTierLevel(), failedCriteria);
    }

    private boolean evaluateSingleRule(UUID userId, TierRule rule) {

        TierEligibilityStrategy strategy = strategyMap.get(rule.getType());

        if (strategy == null) {
            log.warn("No strategy found for rule type {}. Skipping.", rule.getType());
            return true;
        }

        boolean result = strategy.evaluate(userId, rule);

        log.debug("Rule {} result={}", rule.getType(), result);

        return result;
    }

    /**
     * Build a human-readable failure message for a criteria that wasn't met.
     */
    private String buildFailureMessage(TierRule rule) {
        String period = rule.getPeriodDays() != null
                ? " in the last " + rule.getPeriodDays() + " days"
                : "";

        return switch (rule.getType()) {
            case MIN_ORDER_COUNT ->
                "Minimum " + rule.getThresholdValue().intValue() + " orders required" + period;
            case MIN_ORDER_VALUE ->
                "Minimum order value of " + rule.getThresholdValue() + " required" + period;
            case USER_COHORT ->
                "Must belong to cohort: " + rule.getCohortName();

            default -> "Unknow rule failed";
        };
    }
}
