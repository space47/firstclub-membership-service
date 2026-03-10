package com.firstclub.membership.modules.tier.application;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firstclub.membership.common.exception.InvalidOperationException;
import com.firstclub.membership.common.exception.ResourceNotFoundException;
import com.firstclub.membership.common.mapper.DtoMapper;
import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.modules.tier.domain.entity.MembershipTier;
import com.firstclub.membership.modules.tier.domain.entity.UserTier;
import com.firstclub.membership.modules.tier.domain.repository.MembershipTierRepository;
import com.firstclub.membership.modules.tier.domain.repository.UserTierRepository;
import com.firstclub.membership.modules.tier.domain.strategy.TierEligibilityEvaluator;
import com.firstclub.membership.modules.tier.dto.response.UserTierResponse;

@Service
public class TierEvaluationService {

    private static final Logger log = LoggerFactory.getLogger(TierEvaluationService.class);

    private final TierEligibilityEvaluator evaluator;
    private final MembershipTierRepository tierRepository;
    private final UserTierRepository userTierRepository;

    public TierEvaluationService(TierEligibilityEvaluator evaluator,
            MembershipTierRepository tierRepository,
            UserTierRepository userTierRepository) {
        this.evaluator = evaluator;
        this.tierRepository = tierRepository;
        this.userTierRepository = userTierRepository;
    }

    @Transactional
    public UserTierResponse evaluateAndAssignTier(UUID userId, String reason) {
        TierLevel qualifiedLevel = evaluator.evaluate(userId);
        return assignTier(userId, qualifiedLevel, reason);
    }

    /**
     * Validate that a user is eligible for a specific user-requested tier.
     * If eligible, assign it. If not, throw with details about what's missing.
     * 
     * This is the flow when the user explicitly picks a tier during subscription.
     */
    @Transactional
    public UserTierResponse validateAndAssignRequestedTier(UUID userId, UUID tierId, String reason) {
        TierEligibilityEvaluator.EligibilityResult result = evaluator.evaluateForTier(userId, tierId);

        if (!result.isEligible()) {
            String failureDetails = String.join("; ", result.getFailedCriteria());
            throw new InvalidOperationException(
                    "User is not eligible for " + result.getTierLevel() + " tier. "
                            + "Unmet criteria: " + failureDetails);
        }

        log.info("User {} passed eligibility check for tier {}", userId, result.getTierLevel());
        return assignTier(userId, result.getTierLevel(), reason);
    }

    /**
     * Check eligibility without assigning. Useful for UI to show
     * which tiers the user can/cannot select.
     */
    @Transactional(readOnly = true)
    public TierEligibilityEvaluator.EligibilityResult checkEligibility(UUID userId, UUID tierId) {
        return evaluator.evaluateForTier(userId, tierId);
    }

    @Async("tierEvaluationExecutor")
    @Transactional
    public CompletableFuture<UserTierResponse> evaluateAndAssignTierAsync(UUID userId) {
        log.info("Async tier evaluation started for user {}", userId);
        UserTierResponse result = evaluateAndAssignTier(userId, "AUTO_EVALUATION_ASYNC");
        return CompletableFuture.completedFuture(result);
    }

    @Transactional
    public UserTierResponse assignTier(UUID userId, TierLevel tierLevel, String reason) {
        MembershipTier tier = tierRepository.findByTierLevel(tierLevel)
                .orElseThrow(() -> new ResourceNotFoundException("Tier not found: " + tierLevel));

        UserTier userTier = userTierRepository.findByUserId(userId)
                .map(existing -> {
                    existing.setPreviousTierLevel(existing.getTierLevel());
                    existing.setTierLevel(tierLevel);
                    existing.setTier(tier);
                    existing.setAssignedAt(LocalDateTime.now());
                    existing.setAssignmentReason(reason);
                    return existing;
                })
                .orElseGet(() -> UserTier.builder()
                        .userId(userId)
                        .tier(tier)
                        .tierLevel(tierLevel)
                        .assignedAt(LocalDateTime.now())
                        .assignmentReason(reason)
                        .build());

        userTier = userTierRepository.save(userTier);
        log.info("Assigned tier {} to user {} (reason: {})", tierLevel, userId, reason);

        return DtoMapper.toUserTierResponse(userTier);
    }

    /**
     * Get user's tier — throws if not found.
     * Use for endpoints where tier MUST exist.
     */
    @Transactional(readOnly = true)
    public UserTierResponse getUserTier(UUID userId) {
        UserTier userTier = userTierRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No tier assignment found for user: " + userId));
        return DtoMapper.toUserTierResponse(userTier);
    }

    @Transactional(readOnly = true)
    public Optional<UserTierResponse> findUserTier(UUID userId) {
        return userTierRepository.findByUserId(userId)
                .map(DtoMapper::toUserTierResponse);
    }
}
