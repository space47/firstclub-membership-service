package com.firstclub.membership.modules.membershipplan.controller;

import java.util.List;
import java.util.UUID;

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
import com.firstclub.membership.modules.membershipplan.application.MembershipPlanService;
import com.firstclub.membership.modules.membershipplan.dto.request.CreatePlanRequest;
import com.firstclub.membership.modules.membershipplan.dto.request.UpdatePlanRequest;
import com.firstclub.membership.modules.membershipplan.dto.response.PlanResponse;

@RestController
@RequestMapping("/api/v1/plans")
public class MembershipPlanController {

    private final MembershipPlanService planService;

    public MembershipPlanController(MembershipPlanService planService) {
        this.planService = planService;
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<PlanResponse>>> getAllPlans() {
        List<PlanResponse> plans = planService.getAllActivePlans();
        return ResponseEntity.ok(ApiResponse.ok(plans, "Active membership plans"));
    }

    @GetMapping("/{planId}")
    public ResponseEntity<ApiResponse<PlanResponse>> getPlan(@PathVariable UUID planId) {
        PlanResponse plan = planService.getPlanById(planId);
        return ResponseEntity.ok(ApiResponse.ok(plan));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<PlanResponse>> createPlan(
            @RequestBody CreatePlanRequest request) {

        PlanResponse plan = planService.createPlan(request);

        return ResponseEntity.ok(ApiResponse.ok(plan, "Plan created successfully"));
    }

    @PatchMapping("/{planId}")
    public ResponseEntity<ApiResponse<PlanResponse>> updatePlan(
            @PathVariable UUID planId,
            @RequestBody UpdatePlanRequest request) {

        PlanResponse plan = planService.updatePlan(planId, request);

        return ResponseEntity.ok(ApiResponse.ok(plan, "Plan updated successfully"));
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<ApiResponse<String>> deletePlan(@PathVariable UUID planId) {

        planService.deletePlan(planId);

        return ResponseEntity.ok(ApiResponse.ok("Plan deleted successfully"));
    }
}
