package com.firstclub.membership.modules.tier.controller;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firstclub.membership.common.dto.ApiResponse;
import com.firstclub.membership.modules.tier.application.TierEvaluationService;
import com.firstclub.membership.modules.tier.application.TierService;
import com.firstclub.membership.modules.tier.dto.request.CreateTierBenefitRequest;
import com.firstclub.membership.modules.tier.dto.request.CreateTierRequest;
import com.firstclub.membership.modules.tier.dto.request.EvaluateTierRequest;
import com.firstclub.membership.modules.tier.dto.request.UpdateTierBenefitRequest;
import com.firstclub.membership.modules.tier.dto.request.UpdateTierRequest;
import com.firstclub.membership.modules.tier.dto.response.BenefitResponse;
import com.firstclub.membership.modules.tier.dto.response.EligibilityResponse;
import com.firstclub.membership.modules.tier.dto.response.TierResponse;
import com.firstclub.membership.modules.tier.dto.response.UserTierResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/tiers")
public class TierController {

    private final TierService tierService;
    private final TierEvaluationService tierEvaluationService;

    public TierController(TierService tierService, TierEvaluationService tierEvaluationService) {
        this.tierService = tierService;
        this.tierEvaluationService = tierEvaluationService;
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<TierResponse>>> getAllTiers() {
        List<TierResponse> tiers = tierService.getAllTiers();
        return ResponseEntity.ok(ApiResponse.ok(tiers, "Membership tiers"));
    }

    @GetMapping("/{tierId}")
    public ResponseEntity<ApiResponse<TierResponse>> getTier(@PathVariable UUID tierId) {
        TierResponse tier = tierService.getTierById(tierId);
        return ResponseEntity.ok(ApiResponse.ok(tier));
    }

    @PostMapping("/evaluate-tier")
    public ResponseEntity<ApiResponse<UserTierResponse>> evaluateTier(
            @Valid @RequestBody EvaluateTierRequest request) {
        UserTierResponse response = tierEvaluationService.evaluateAndAssignTier(request.getUserId(),
                "MANUAL_EVALUATION");
        return ResponseEntity.ok(ApiResponse.ok(response, "Tier evaluated"));
    }

    @PostMapping("/evaluate-tier-async")
    public ResponseEntity<ApiResponse<String>> evaluateTierAsync(
            @Valid @RequestBody EvaluateTierRequest request) {
        CompletableFuture<UserTierResponse> future = tierEvaluationService
                .evaluateAndAssignTierAsync(request.getUserId());
        return ResponseEntity.accepted()
                .body(ApiResponse.ok("Tier evaluation initiated", "Processing in background"));
    }

    @GetMapping("/check-eligibility/{userId}/{tierId}")
    public ResponseEntity<ApiResponse<EligibilityResponse>> checkEligibility(
            @PathVariable UUID userId,
            @PathVariable UUID tierId) {
        var result = tierEvaluationService.checkEligibility(userId, tierId);
        EligibilityResponse response = new EligibilityResponse(
                userId, result.getTierLevel(), result.isEligible(), result.getFailedCriteria());

        String message = result.isEligible()
                ? "User is eligible for " + result.getTierLevel()
                : "User is NOT eligible for " + result.getTierLevel();
        return ResponseEntity.ok(ApiResponse.ok(response, message));
    }

    @GetMapping("/users/{userId}/tier")
    public ResponseEntity<ApiResponse<UserTierResponse>> getUserTier(@PathVariable UUID userId) {

        UserTierResponse response = tierService.getUserTier(userId);

        return ResponseEntity.ok(ApiResponse.ok(response, "User tier fetched"));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<TierResponse>> createTier(
            @Valid @RequestBody CreateTierRequest request) {

        TierResponse response = tierService.createTier(request);

        return ResponseEntity.ok(ApiResponse.ok(response, "Tier created"));
    }

    @PatchMapping("/{tierId}")
    public ResponseEntity<ApiResponse<String>> updateTier(
            @PathVariable UUID tierId,
            @RequestBody UpdateTierRequest request) {

        tierService.updateTier(tierId, request);

        return ResponseEntity.ok(ApiResponse.ok("Tier updated"));
    }

    @DeleteMapping("/{tierId}")
    public ResponseEntity<ApiResponse<String>> deleteTier(@PathVariable UUID tierId) {

        tierService.deleteTier(tierId);

        return ResponseEntity.ok(ApiResponse.ok("Tier deleted"));
    }

    @PostMapping("/{tierId}/benefits")
    public ResponseEntity<ApiResponse<BenefitResponse>> createBenefit(
            @PathVariable UUID tierId,
            @RequestBody CreateTierBenefitRequest request) {

        BenefitResponse response = tierService.createBenefit(tierId, request);

        return ResponseEntity.ok(ApiResponse.ok(response, "Benefit created"));
    }

    @PatchMapping("/benefits/{benefitId}")
    public ResponseEntity<ApiResponse<String>> updateBenefit(
            @PathVariable UUID benefitId,
            @RequestBody UpdateTierBenefitRequest request) {

        tierService.updateBenefit(benefitId, request);

        return ResponseEntity.ok(ApiResponse.ok("Benefit Updated"));
    }

    @DeleteMapping("/benefits/{benefitId}")
    public ResponseEntity<ApiResponse<String>> deleteBenefit(
            @PathVariable UUID benefitId) {

        tierService.deleteBenefit(benefitId);

        return ResponseEntity.ok(ApiResponse.ok("Benefit deleted"));
    }
}