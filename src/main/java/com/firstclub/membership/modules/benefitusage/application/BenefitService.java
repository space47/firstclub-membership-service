package com.firstclub.membership.modules.benefitusage.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firstclub.membership.common.exception.InvalidOperationException;
import com.firstclub.membership.common.exception.ResourceNotFoundException;
import com.firstclub.membership.enums.BenefitType;
import com.firstclub.membership.modules.benefitusage.domain.entity.BenefitUsage;
import com.firstclub.membership.modules.benefitusage.domain.repository.BenefitUsageRepository;
import com.firstclub.membership.modules.tier.domain.entity.TierBenefit;
import com.firstclub.membership.modules.tier.domain.entity.UserTier;
import com.firstclub.membership.modules.tier.domain.repository.UserTierRepository;

/**
 * Handles benefit eligibility checks and usage tracking.
 * 
 * This is the API that the Checkout Service calls during order placement:
 * 
 * 1. User adds items to cart (Order Service)
 * 2. At checkout, Order Service calls: GET /benefits/check
 * → "Can this user get free delivery on a ₹450 order?"
 * → Response: { eligible: false, reason: "Minimum order ₹500 required" }
 * 
 * 3. User increases cart to ₹600, checks again:
 * → { eligible: true, remainingUsage: 2 }
 * 
 * 4. Order is placed, Order Service calls: POST /benefits/record-usage
 * → Records that one free delivery was consumed
 */
@Service
public class BenefitService {

    private static final Logger log = LoggerFactory.getLogger(BenefitService.class);

    private final UserTierRepository userTierRepository;
    private final BenefitUsageRepository benefitUsageRepository;

    public BenefitService(UserTierRepository userTierRepository,
            BenefitUsageRepository benefitUsageRepository) {
        this.userTierRepository = userTierRepository;
        this.benefitUsageRepository = benefitUsageRepository;
    }

    /**
     * Result of checking whether a user can use a specific benefit.
     */
    public static class BenefitCheckResult {
        private final boolean eligible;
        private final String reason;
        private final BigDecimal benefitValue;
        private final BigDecimal maxDiscountCap;
        private final int usedThisPeriod;
        private final Integer maxUsageLimit;

        public BenefitCheckResult(boolean eligible, String reason, BigDecimal benefitValue,
                BigDecimal maxDiscountCap, int usedThisPeriod, Integer maxUsageLimit) {
            this.eligible = eligible;
            this.reason = reason;
            this.benefitValue = benefitValue;
            this.maxDiscountCap = maxDiscountCap;
            this.usedThisPeriod = usedThisPeriod;
            this.maxUsageLimit = maxUsageLimit;
        }

        public boolean isEligible() {
            return eligible;
        }

        public String getReason() {
            return reason;
        }

        public BigDecimal getBenefitValue() {
            return benefitValue;
        }

        public BigDecimal getMaxDiscountCap() {
            return maxDiscountCap;
        }

        public int getUsedThisPeriod() {
            return usedThisPeriod;
        }

        public Integer getMaxUsageLimit() {
            return maxUsageLimit;
        }

        public Integer getRemainingUsage() {
            return maxUsageLimit != null ? maxUsageLimit - usedThisPeriod : null;
        }
    }

    /**
     * Check if a user can use a specific benefit for a given order value.
     * Called by the Checkout Service before applying a benefit.
     * 
     * @param userId      the user
     * @param benefitType which benefit to check (FREE_DELIVERY,
     *                    DISCOUNT_PERCENTAGE, etc.)
     * @param orderValue  the current order total
     * @return eligibility result with details
     */
    @Transactional(readOnly = true)
    public BenefitCheckResult checkBenefitEligibility(UUID userId, BenefitType benefitType,
            BigDecimal orderValue) {
        // 1. Get user's current tier
        UserTier userTier = userTierRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User has no active tier: " + userId));

        // 2. Find the matching benefit for their tier
        TierBenefit benefit = userTier.getTier().getBenefits().stream()
                .filter(b -> b.getBenefitType() == benefitType && b.isActive())
                .findFirst()
                .orElse(null);

        if (benefit == null) {
            log.info("from here1");
            return new BenefitCheckResult(false,
                    userTier.getTierLevel() + " tier does not include " + benefitType,
                    null, null, 0, null);
        }

        // 3. Check minimum order value
        if (benefit.getMinOrderValue() != null
                && orderValue.compareTo(benefit.getMinOrderValue()) < 0) {
            log.info("from here2");

            return new BenefitCheckResult(false,
                    "Minimum order value of " + benefit.getMinOrderValue() + " required. Current: " + orderValue,
                    benefit.getValue(), benefit.getMaxDiscountCap(), 0, benefit.getMaxUsageLimit());
        }

        // 4. Check usage limit
        int usedThisPeriod = 0;
        if (benefit.getMaxUsageLimit() != null) {
            LocalDateTime periodStart = getCurrentPeriodStart();
            usedThisPeriod = benefitUsageRepository.countUsage(userId, benefitType, periodStart);

            log.info("max", usedThisPeriod);
            if (usedThisPeriod >= benefit.getMaxUsageLimit()) {
                log.info("from here3");

                return new BenefitCheckResult(false,
                        "Usage limit reached: " + usedThisPeriod + "/" + benefit.getMaxUsageLimit() + " this month",
                        benefit.getValue(), benefit.getMaxDiscountCap(),
                        usedThisPeriod, benefit.getMaxUsageLimit());
            }
        }

        log.info("from here4");

        // 5. All checks passed
        return new BenefitCheckResult(true, "Eligible",
                benefit.getValue(), benefit.getMaxDiscountCap(),
                usedThisPeriod, benefit.getMaxUsageLimit());
    }

    /**
     * Record that a user consumed a benefit for a specific order.
     * Called by the Checkout Service AFTER order is successfully placed.
     * 
     * @param userId      the user
     * @param benefitType which benefit was used
     * @param orderId     the order that consumed this benefit
     */
    @Transactional
    public void recordUsage(UUID userId, BenefitType benefitType, UUID orderId) {
        // Validate the user actually has this benefit and hasn't exceeded limit
        BenefitCheckResult check = checkBenefitEligibility(userId, benefitType, BigDecimal.ZERO);

        // For usage recording, we skip minOrderValue check (order already placed).
        // But we still enforce usage limits.
        if (check.getMaxUsageLimit() != null && check.getUsedThisPeriod() >= check.getMaxUsageLimit()) {
            throw new InvalidOperationException(
                    "Cannot record usage: limit already reached for " + benefitType);
        }

        BenefitUsage usage = new BenefitUsage(userId, benefitType, orderId);
        benefitUsageRepository.save(usage);

        log.info("Recorded {} usage for user {} on order {} ({}/{})",
                benefitType, userId, orderId,
                check.getUsedThisPeriod() + 1,
                check.getMaxUsageLimit() != null ? check.getMaxUsageLimit() : "unlimited");
    }

    /**
     * Get the start of the current billing period.
     * Using first day of current month at midnight.
     * 
     * In production, this might align with the subscription start date
     * instead of calendar month.
     */
    private LocalDateTime getCurrentPeriodStart() {
        return LocalDateTime.now()
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
    }
}
