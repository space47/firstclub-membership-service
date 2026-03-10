package com.firstclub.membership.modules.benefitusage.controller;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.firstclub.membership.common.dto.ApiResponse;
import com.firstclub.membership.enums.BenefitType;
import com.firstclub.membership.modules.benefitusage.application.BenefitService;
import com.firstclub.membership.modules.benefitusage.application.BenefitService.BenefitCheckResult;

/**
 * Endpoints consumed by the Checkout Service during order placement.
 * 
 * Flow:
 * 1. Checkout calls GET /check → "can this user get free delivery?"
 * 2. If yes, Checkout applies the benefit to the order
 * 3. After order is placed, Checkout calls POST /record-usage
 */
@RestController
@RequestMapping("/api/v1/benefits")
public class BenefitController {

    private final BenefitService benefitService;

    public BenefitController(BenefitService benefitService) {
        this.benefitService = benefitService;
    }

    /**
     * GET /api/v1/benefits/check/{userId}/{benefitType}?orderValue=600
     * 
     * Called by Checkout Service to check if a benefit can be applied.
     * 
     * Example responses:
     * 
     * Eligible:
     * { eligible: true, benefitValue: 1, remainingUsage: 2, maxUsageLimit: 3 }
     * 
     * Not eligible (order too small):
     * { eligible: false, reason: "Minimum order value of 500 required. Current:
     * 300" }
     * 
     * Not eligible (usage exhausted):
     * { eligible: false, reason: "Usage limit reached: 3/3 this month" }
     */
    @GetMapping("/check/{userId}/{benefitType}")
    public ResponseEntity<ApiResponse<BenefitCheckResult>> checkEligibility(
            @PathVariable UUID userId,
            @PathVariable BenefitType benefitType,
            @RequestParam(defaultValue = "0") BigDecimal orderValue) {

        BenefitCheckResult result = benefitService.checkBenefitEligibility(
                userId, benefitType, orderValue);

        String message = result.isEligible()
                ? "Benefit available"
                : "Benefit not available: " + result.getReason();

        return ResponseEntity.ok(ApiResponse.ok(result, message));
    }

    /**
     * POST /api/v1/benefits/record-usage
     * Body: { "userId": "...", "benefitType": "FREE_DELIVERY", "orderId": "..." }
     * 
     * Called by Checkout Service AFTER order is placed to record consumption.
     */
    @PostMapping("/record-usage")
    public ResponseEntity<ApiResponse<String>> recordUsage(
            @RequestBody Map<String, String> request) {

        UUID userId = UUID.fromString(request.get("userId"));
        BenefitType benefitType = BenefitType.valueOf(request.get("benefitType"));
        UUID orderId = UUID.fromString(request.get("orderId"));

        benefitService.recordUsage(userId, benefitType, orderId);

        return ResponseEntity.ok(ApiResponse.ok("Usage recorded", "Benefit usage tracked"));
    }
}