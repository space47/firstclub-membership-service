package com.firstclub.membership.config;

import java.math.BigDecimal;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.firstclub.membership.enums.BenefitType;
import com.firstclub.membership.enums.PlanDuration;
import com.firstclub.membership.enums.RuleType;
import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.modules.membershipplan.domain.entity.MembershipPlan;
import com.firstclub.membership.modules.membershipplan.domain.repository.MembershipPlanRepository;
import com.firstclub.membership.modules.tier.domain.entity.MembershipTier;
import com.firstclub.membership.modules.tier.domain.entity.TierBenefit;
import com.firstclub.membership.modules.tier.domain.model.TierEligibilityRules;
import com.firstclub.membership.modules.tier.domain.model.TierRule;
import com.firstclub.membership.modules.tier.domain.repository.MembershipTierRepository;

@Configuration
public class DataSeeder {

        private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

        @Bean
        @Transactional
        CommandLineRunner seedData(MembershipPlanRepository planRepo,
                        MembershipTierRepository tierRepo) {
                return args -> {
                        if (planRepo.count() > 0) {
                                log.info("Data already seeded, skipping.");
                                return;
                        }

                        log.info("Seeding membership plans...");
                        seedPlans(planRepo);

                        log.info("Seeding membership tiers with benefits and criteria...");
                        seedTiers(tierRepo);

                        log.info("Data seeding complete!");
                };
        }

        private void seedPlans(MembershipPlanRepository repo) {
                repo.saveAll(Arrays.asList(
                                MembershipPlan.builder()
                                                .name("FirstClub Monthly")
                                                .description("Monthly membership with basic benefits. Cancel anytime.")
                                                .duration(PlanDuration.MONTHLY)
                                                .price(new BigDecimal("99.00"))
                                                .active(true)
                                                .build(),

                                MembershipPlan.builder()
                                                .name("FirstClub Quarterly")
                                                .description("3-month membership. Save 15% vs monthly.")
                                                .duration(PlanDuration.QUARTERLY)
                                                .price(new BigDecimal("249.00"))
                                                .active(true)
                                                .build(),

                                MembershipPlan.builder()
                                                .name("FirstClub Yearly")
                                                .description("Annual membership. Best value - save 30% vs monthly.")
                                                .duration(PlanDuration.YEARLY)
                                                .price(new BigDecimal("799.00"))
                                                .active(true)
                                                .build()));
        }

        private void seedTiers(MembershipTierRepository repo) {
                // ── SILVER TIER ───────────────────────────────────────────
                MembershipTier silver = MembershipTier.builder()
                                .name("Silver")
                                .tierLevel(TierLevel.SILVER)
                                .description("Entry-level membership with basic benefits.")
                                .build();

                silver.getBenefits().addAll(Arrays.asList(
                                TierBenefit.builder()
                                                .tier(silver)
                                                .benefitType(BenefitType.FREE_DELIVERY)
                                                .value(BigDecimal.ONE)
                                                .minOrderValue(new BigDecimal("500"))
                                                .maxUsageLimit(3)
                                                .description("Free delivery on orders above ₹500 (3 per month)")
                                                .build(),
                                TierBenefit.builder()
                                                .tier(silver)
                                                .benefitType(BenefitType.DISCOUNT_PERCENTAGE)
                                                .value(new BigDecimal("5"))
                                                .minOrderValue(new BigDecimal("200"))
                                                .maxDiscountCap(new BigDecimal("200"))
                                                .description("5% discount on orders above ₹200, max ₹200 off")
                                                .build()));

                // ── GOLD TIER ─────────────────────────────────────────────
                MembershipTier gold = MembershipTier.builder()
                                .name("Gold")
                                .tierLevel(TierLevel.GOLD)
                                .description("Enhanced benefits for regular shoppers.")
                                .build();

                gold.getBenefits().addAll(Arrays.asList(
                                TierBenefit.builder()
                                                .tier(gold)
                                                .benefitType(BenefitType.FREE_DELIVERY)
                                                .value(BigDecimal.ONE)
                                                .minOrderValue(BigDecimal.ZERO)
                                                .description("Free delivery on all orders, no minimum")
                                                .build(),
                                TierBenefit.builder()
                                                .tier(gold)
                                                .benefitType(BenefitType.DISCOUNT_PERCENTAGE)
                                                .value(new BigDecimal("10"))
                                                .minOrderValue(new BigDecimal("100"))
                                                .maxDiscountCap(new BigDecimal("500"))
                                                .description("10% discount on orders above ₹100, max ₹500 off")
                                                .build(),
                                TierBenefit.builder()
                                                .tier(gold)
                                                .benefitType(BenefitType.EXCLUSIVE_DEALS)
                                                .value(BigDecimal.ONE)
                                                .description("Access to exclusive Gold member deals")
                                                .build(),
                                TierBenefit.builder()
                                                .tier(gold)
                                                .benefitType(BenefitType.EARLY_ACCESS)
                                                .value(BigDecimal.ONE)
                                                .description("Early access to sales (12 hours before)")
                                                .build()));

                TierEligibilityRules wrapper = new TierEligibilityRules();
                wrapper.setRules(Arrays.asList(
                                TierRule.builder()
                                                .type(RuleType.MIN_ORDER_COUNT)
                                                .thresholdValue(new BigDecimal("10"))
                                                .periodDays(30)
                                                .build(),
                                TierRule.builder()
                                                .type(RuleType.MIN_ORDER_VALUE)
                                                .thresholdValue(new BigDecimal("3000"))
                                                .periodDays(30)
                                                .build()));
                gold.setEligibilityRules(wrapper);

                // ── PLATINUM TIER ─────────────────────────────────────────
                MembershipTier platinum = MembershipTier.builder()
                                .name("Platinum")
                                .tierLevel(TierLevel.PLATINUM)
                                .description("Premium tier for power users. Maximum benefits.")
                                .build();

                platinum.getBenefits().addAll(Arrays.asList(
                                TierBenefit.builder()
                                                .tier(platinum)
                                                .benefitType(BenefitType.FREE_DELIVERY)
                                                .value(BigDecimal.ONE)
                                                .minOrderValue(BigDecimal.ZERO)
                                                .description("Free express delivery on all orders")
                                                .build(),
                                TierBenefit.builder()
                                                .tier(platinum)
                                                .benefitType(BenefitType.DISCOUNT_PERCENTAGE)
                                                .value(new BigDecimal("15"))
                                                .minOrderValue(BigDecimal.ZERO)
                                                .maxDiscountCap(new BigDecimal("1000"))
                                                .description("15% discount on all items, max ₹1000 off per order")
                                                .build(),
                                TierBenefit.builder()
                                                .tier(platinum)
                                                .benefitType(BenefitType.EXCLUSIVE_DEALS)
                                                .value(BigDecimal.ONE)
                                                .description("Platinum-only exclusive deals and bundles")
                                                .build(),
                                TierBenefit.builder()
                                                .tier(platinum)
                                                .benefitType(BenefitType.EARLY_ACCESS)
                                                .value(BigDecimal.ONE)
                                                .description("Early access to sales (24 hours before)")
                                                .build(),
                                TierBenefit.builder()
                                                .tier(platinum)
                                                .benefitType(BenefitType.PRIORITY_SUPPORT)
                                                .value(BigDecimal.ONE)
                                                .description("Priority customer support with dedicated queue")
                                                .build(),
                                TierBenefit.builder()
                                                .tier(platinum)
                                                .benefitType(BenefitType.CASHBACK_PERCENTAGE)
                                                .value(new BigDecimal("3"))
                                                .minOrderValue(new BigDecimal("100"))
                                                .maxDiscountCap(new BigDecimal("200"))
                                                .description("3% cashback on orders above ₹100, max ₹200 per order")
                                                .build(),
                                TierBenefit.builder()
                                                .tier(platinum)
                                                .benefitType(BenefitType.EXPRESS_DELIVERY)
                                                .value(BigDecimal.ONE)
                                                .minOrderValue(BigDecimal.ZERO)
                                                .description("Free express 10-minute delivery on all orders")
                                                .build()));

                wrapper = new TierEligibilityRules();
                wrapper.setRules(Arrays.asList(
                                TierRule.builder()
                                                .type(RuleType.MIN_ORDER_COUNT)
                                                .thresholdValue(new BigDecimal("25"))
                                                .periodDays(30)
                                                .build(),
                                TierRule.builder()
                                                .type(RuleType.MIN_ORDER_VALUE)
                                                .thresholdValue(new BigDecimal("10000"))
                                                .periodDays(30)
                                                .build()));
                platinum.setEligibilityRules(wrapper);

                repo.saveAll(Arrays.asList(silver, gold, platinum));
        }
}
