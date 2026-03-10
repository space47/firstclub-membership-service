package com.firstclub.membership.modules.subscription.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firstclub.membership.common.dto.ApiResponse;
import com.firstclub.membership.modules.subscription.application.SubscriptionService;
import com.firstclub.membership.modules.subscription.dto.request.CancelRequest;
import com.firstclub.membership.modules.subscription.dto.request.ChangePlanRequest;
import com.firstclub.membership.modules.subscription.dto.request.SubscribeRequest;
import com.firstclub.membership.modules.subscription.dto.response.SubscriptionResponse;
import com.firstclub.membership.modules.subscription.dto.response.UserMembershipResponse;
import com.firstclub.membership.modules.tier.application.TierEvaluationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

        private final SubscriptionService subscriptionService;

        public SubscriptionController(SubscriptionService subscriptionService,
                        TierEvaluationService tierEvaluationService) {
                this.subscriptionService = subscriptionService;
        }

        @PostMapping("/subscribe")
        public ResponseEntity<ApiResponse<UserMembershipResponse>> subscribe(
                        @Valid @RequestBody SubscribeRequest request) {
                UserMembershipResponse response = subscriptionService.subscribe(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.ok(response, "Successfully subscribed"));
        }

        @PostMapping("/cancel")
        public ResponseEntity<ApiResponse<SubscriptionResponse>> cancel(
                        @Valid @RequestBody CancelRequest request) {
                SubscriptionResponse response = subscriptionService.cancel(request);
                return ResponseEntity.ok(ApiResponse.ok(response, "Subscription cancelled"));
        }

        @PutMapping("/change-plan")
        public ResponseEntity<ApiResponse<UserMembershipResponse>> changePlan(
                        @Valid @RequestBody ChangePlanRequest request) {
                UserMembershipResponse response = subscriptionService.changePlan(request);
                return ResponseEntity.ok(ApiResponse.ok(response, "Plan changed successfully"));
        }

        @GetMapping("/user/{userId}")
        public ResponseEntity<ApiResponse<UserMembershipResponse>> getUserMembership(
                        @PathVariable UUID userId) {
                UserMembershipResponse response = subscriptionService.getUserMembership(userId);
                return ResponseEntity.ok(ApiResponse.ok(response));
        }

        @GetMapping("/user/{userId}/history")
        public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getHistory(
                        @PathVariable UUID userId) {
                List<SubscriptionResponse> history = subscriptionService.getSubscriptionHistory(userId);
                return ResponseEntity.ok(ApiResponse.ok(history, "Subscription history"));
        }
}
