package com.firstclub.membership.common.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.firstclub.membership.modules.membershipplan.domain.entity.MembershipPlan;
import com.firstclub.membership.modules.membershipplan.dto.response.PlanResponse;
import com.firstclub.membership.modules.subscription.domain.entity.UserSubscription;
import com.firstclub.membership.modules.subscription.dto.response.SubscriptionResponse;
import com.firstclub.membership.modules.tier.domain.entity.MembershipTier;
import com.firstclub.membership.modules.tier.domain.entity.TierBenefit;
import com.firstclub.membership.modules.tier.domain.entity.UserTier;
import com.firstclub.membership.modules.tier.domain.model.TierRule;
import com.firstclub.membership.modules.tier.dto.response.BenefitResponse;
import com.firstclub.membership.modules.tier.dto.response.CriteriaResponse;
import com.firstclub.membership.modules.tier.dto.response.TierResponse;
import com.firstclub.membership.modules.tier.dto.response.UserTierResponse;

public final class DtoMapper {

        private DtoMapper() {
        }

        public static PlanResponse toPlanResponse(MembershipPlan plan) {
                return PlanResponse.builder()
                                .id(plan.getId())
                                .name(plan.getName())
                                .description(plan.getDescription())
                                .duration(plan.getDuration())
                                .price(plan.getPrice())
                                .autoRenewDefault(plan.isAutoRenewDefault())
                                .build();
        }

        public static TierResponse toTierResponse(MembershipTier tier) {
                List<BenefitResponse> benefits = tier.getBenefits() != null
                                ? tier.getBenefits().stream()
                                                .filter(TierBenefit::isActive)
                                                .map(DtoMapper::toBenefitResponse)
                                                .collect(Collectors.toList())
                                : Collections.emptyList();

                List<CriteriaResponse> criteria = tier.getEligibilityRules() != null &&
                                tier.getEligibilityRules().getRules() != null
                                                ? tier.getEligibilityRules().getRules().stream()
                                                                .map(DtoMapper::toCriteriaResponse)
                                                                .collect(Collectors.toList())
                                                : Collections.emptyList();

                return TierResponse.builder()
                                .id(tier.getId())
                                .name(tier.getName())
                                .tierLevel(tier.getTierLevel())
                                .description(tier.getDescription())
                                .benefits(benefits)
                                .eligibilityCriteria(criteria)
                                .build();
        }

        public static BenefitResponse toBenefitResponse(TierBenefit benefit) {
                return BenefitResponse.builder()
                                .id(benefit.getId())
                                .benefitType(benefit.getBenefitType())
                                .value(benefit.getValue())
                                .minOrderValue(benefit.getMinOrderValue())
                                .maxDiscountCap(benefit.getMaxDiscountCap())
                                .maxUsageLimit(benefit.getMaxUsageLimit())
                                .description(benefit.getDescription())
                                .build();
        }

        public static CriteriaResponse toCriteriaResponse(TierRule rule) {
                return CriteriaResponse.builder()
                                .criteriaType(rule.getType())
                                .thresholdValue(rule.getThresholdValue())
                                .cohortName(rule.getCohortName())
                                .periodDays(rule.getPeriodDays())
                                .build();
        }

        public static SubscriptionResponse toSubscriptionResponse(UserSubscription sub) {
                return SubscriptionResponse.builder()
                                .id(sub.getId())
                                .userId(sub.getUserId())
                                .plan(toPlanResponse(sub.getPlan()))
                                .status(sub.getStatus())
                                .startDate(sub.getStartDate())
                                .endDate(sub.getEndDate())
                                .autoRenew(sub.isAutoRenew())
                                .cancelledAt(sub.getCancelledAt())
                                .cancellationReason(sub.getCancellationReason())
                                .build();
        }

        public static UserTierResponse toUserTierResponse(UserTier userTier) {
                List<BenefitResponse> benefits = userTier.getTier().getBenefits() != null
                                ? userTier.getTier().getBenefits().stream()
                                                .filter(TierBenefit::isActive)
                                                .map(DtoMapper::toBenefitResponse)
                                                .collect(Collectors.toList())
                                : Collections.emptyList();

                return UserTierResponse.builder()
                                .userId(userTier.getUserId())
                                .tierLevel(userTier.getTierLevel())
                                .tierName(userTier.getTier().getName())
                                .assignedAt(userTier.getAssignedAt())
                                .validUntil(userTier.getValidUntil())
                                .previousTierLevel(userTier.getPreviousTierLevel())
                                .assignmentReason(userTier.getAssignmentReason())
                                .activeBenefits(benefits)
                                .build();
        }
}
