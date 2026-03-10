package com.firstclub.membership.modules.subscription.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firstclub.membership.common.exception.DuplicateSubscriptionException;
import com.firstclub.membership.common.exception.InvalidOperationException;
import com.firstclub.membership.common.exception.ResourceNotFoundException;
import com.firstclub.membership.common.mapper.DtoMapper;
import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.modules.membershipplan.domain.entity.MembershipPlan;
import com.firstclub.membership.modules.membershipplan.domain.repository.MembershipPlanRepository;
import com.firstclub.membership.modules.subscription.domain.entity.UserSubscription;
import com.firstclub.membership.modules.subscription.domain.repository.UserSubscriptionRepository;
import com.firstclub.membership.modules.subscription.dto.request.CancelRequest;
import com.firstclub.membership.modules.subscription.dto.request.ChangePlanRequest;
import com.firstclub.membership.modules.subscription.dto.request.SubscribeRequest;
import com.firstclub.membership.modules.subscription.dto.response.SubscriptionResponse;
import com.firstclub.membership.modules.subscription.dto.response.UserMembershipResponse;
import com.firstclub.membership.modules.tier.application.TierEvaluationService;
import com.firstclub.membership.modules.tier.domain.repository.UserTierRepository;
import com.firstclub.membership.modules.tier.dto.response.UserTierResponse;

@Service
public class SubscriptionService {

        private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);

        private final UserSubscriptionRepository subscriptionRepository;
        private final MembershipPlanRepository planRepository;
        private final UserTierRepository userTierRepository;
        private final TierEvaluationService tierEvaluationService;

        public SubscriptionService(UserSubscriptionRepository subscriptionRepository,
                        MembershipPlanRepository planRepository,
                        UserTierRepository userTierRepository,
                        TierEvaluationService tierEvaluationService) {
                this.subscriptionRepository = subscriptionRepository;
                this.planRepository = planRepository;
                this.userTierRepository = userTierRepository;
                this.tierEvaluationService = tierEvaluationService;
        }

        @Transactional
        public UserMembershipResponse subscribe(SubscribeRequest request) {
                UUID userId = request.getUserId();

                if (subscriptionRepository.existsByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)) {
                        throw new DuplicateSubscriptionException(
                                        "User already has an active subscription. Cancel or upgrade instead.");
                }

                MembershipPlan plan = planRepository.findById(request.getPlanId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Plan not found: " + request.getPlanId()));

                if (!plan.isActive()) {
                        throw new DuplicateSubscriptionException("Plan is no longer available: " + plan.getName());
                }

                LocalDateTime startDate = LocalDateTime.now();
                LocalDateTime endDate = startDate.plusDays(plan.getDuration().getDays());

                UserSubscription subscription = UserSubscription.builder()
                                .userId(userId)
                                .plan(plan)
                                .status(SubscriptionStatus.ACTIVE)
                                .startDate(startDate)
                                .endDate(endDate)
                                .autoRenew(request.getAutoRenew() != null ? request.getAutoRenew()
                                                : plan.isAutoRenewDefault())
                                .build();

                subscription = subscriptionRepository.save(subscription);
                log.info("User {} subscribed to plan {} (expires: {})", userId, plan.getName(), endDate);

                UserTierResponse tierResponse;

                if (request.getTierId() != null) {
                        // User explicitly chose a tier — validate eligibility before assigning
                        tierResponse = tierEvaluationService
                                        .validateAndAssignRequestedTier(userId, request.getTierId(),
                                                        "USER_SELECTED_AT_SUBSCRIPTION");
                } else {
                        // No tier specified — auto-calculate from rules engine
                        tierResponse = tierEvaluationService
                                        .evaluateAndAssignTier(userId, "AUTO_EVALUATED_AT_SUBSCRIPTION");
                }

                return UserMembershipResponse.builder()
                                .userId(userId)
                                .subscription(DtoMapper.toSubscriptionResponse(subscription))
                                .tier(tierResponse)
                                .isMember(true)
                                .build();
        }

        @Transactional
        public SubscriptionResponse cancel(CancelRequest request) {
                UserSubscription subscription = getActiveSubscription(request.getUserId());

                subscription.setStatus(SubscriptionStatus.CANCELLED);
                subscription.setCancelledAt(LocalDateTime.now());
                subscription.setCancellationReason(request.getReason());
                subscription.setAutoRenew(false);

                subscription = subscriptionRepository.save(subscription);
                log.info("User {} cancelled subscription (reason: {})",
                                request.getUserId(), request.getReason());

                return DtoMapper.toSubscriptionResponse(subscription);
        }

        @Transactional
        public UserMembershipResponse changePlan(ChangePlanRequest request) {
                UUID userId = request.getUserId();
                UserSubscription current = getActiveSubscription(userId);

                log.info("current_subscription", current);

                MembershipPlan newPlan = planRepository.findById(request.getNewPlanId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Plan not found: " + request.getNewPlanId()));

                if (!newPlan.isActive()) {
                        throw new InvalidOperationException("Plan is no longer available: " + newPlan.getName());
                }

                if (current.getPlan().getId().equals(newPlan.getId())) {
                        throw new InvalidOperationException("User is already on this plan");
                }

                boolean isUpgrade = newPlan.getPrice().compareTo(current.getPlan().getPrice()) > 0;

                current.setStatus(isUpgrade ? SubscriptionStatus.UPGRADED : SubscriptionStatus.DOWNGRADED);
                current.setCancelledAt(LocalDateTime.now());
                current.setCancellationReason("Changed to plan: " + newPlan.getName());
                subscriptionRepository.save(current);

                LocalDateTime startDate = LocalDateTime.now();
                LocalDateTime endDate = startDate.plusDays(newPlan.getDuration().getDays());

                UserSubscription newSubscription = UserSubscription.builder()
                                .userId(userId)
                                .plan(newPlan)
                                .status(SubscriptionStatus.ACTIVE)
                                .startDate(startDate)
                                .endDate(endDate)
                                .autoRenew(current.isAutoRenew())
                                .build();

                newSubscription = subscriptionRepository.save(newSubscription);
                log.info("User {} {} from {} to {}", userId,
                                isUpgrade ? "upgraded" : "downgraded",
                                current.getPlan().getName(), newPlan.getName());

                UserTierResponse tierResponse = tierEvaluationService
                                .evaluateAndAssignTier(userId, isUpgrade ? "PLAN_UPGRADED" : "PLAN_DOWNGRADED");

                return UserMembershipResponse.builder()
                                .userId(userId)
                                .subscription(DtoMapper.toSubscriptionResponse(newSubscription))
                                .tier(tierResponse)
                                .isMember(true)
                                .build();
        }

        @Transactional(readOnly = true)
        public UserMembershipResponse getUserMembership(UUID userId) {
                var subscriptionOpt = subscriptionRepository
                                .findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE);

                var tierOpt = tierEvaluationService.findUserTier(userId);

                // If user has never subscribed and has no tier, they don't exist in our system
                if (subscriptionOpt.isEmpty() && tierOpt.isEmpty()) {
                        throw new ResourceNotFoundException(
                                        "No active membership found for user: " + userId);
                }

                SubscriptionResponse subResponse = subscriptionOpt
                                .map(DtoMapper::toSubscriptionResponse)
                                .orElse(null);

                boolean isMember = subscriptionOpt.isPresent() && subscriptionOpt.get().isActive();

                return UserMembershipResponse.builder()
                                .userId(userId)
                                .subscription(subResponse)
                                .tier(tierOpt.orElse(null))
                                .isMember(isMember)
                                .build();
        }

        @Transactional(readOnly = true)
        public List<SubscriptionResponse> getSubscriptionHistory(UUID userId) {
                List<UserSubscription> history = subscriptionRepository
                                .findByUserIdOrderByCreatedAtDesc(userId);

                if (history.isEmpty()) {
                        throw new ResourceNotFoundException(
                                        "No subscription history found for user: " + userId);
                }

                return history.stream()
                                .map(DtoMapper::toSubscriptionResponse)
                                .collect(Collectors.toList());
        }

        private UserSubscription getActiveSubscription(UUID userId) {
                return subscriptionRepository
                                .findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "No active subscription found for user: " + userId));
        }
}
