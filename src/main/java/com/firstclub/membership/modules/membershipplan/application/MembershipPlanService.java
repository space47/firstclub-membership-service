package com.firstclub.membership.modules.membershipplan.application;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firstclub.membership.common.exception.ResourceNotFoundException;
import com.firstclub.membership.common.mapper.DtoMapper;
import com.firstclub.membership.enums.PlanDuration;
import com.firstclub.membership.modules.membershipplan.domain.entity.MembershipPlan;
import com.firstclub.membership.modules.membershipplan.domain.repository.MembershipPlanRepository;
import com.firstclub.membership.modules.membershipplan.dto.request.CreatePlanRequest;
import com.firstclub.membership.modules.membershipplan.dto.request.UpdatePlanRequest;
import com.firstclub.membership.modules.membershipplan.dto.response.PlanResponse;

@Service
@Transactional(readOnly = true)
public class MembershipPlanService {

    private final MembershipPlanRepository planRepository;

    public MembershipPlanService(MembershipPlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public List<PlanResponse> getAllActivePlans() {
        return planRepository.findByActiveTrue().stream()
                .map(DtoMapper::toPlanResponse)
                .collect(Collectors.toList());
    }

    public PlanResponse getPlanById(UUID planId) {
        MembershipPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found: " + planId));
        return DtoMapper.toPlanResponse(plan);
    }

    @Transactional
    public PlanResponse createPlan(CreatePlanRequest request) {
        if (planRepository.existsByDurationAndActiveTrue(request.getDuration())) {
            throw new IllegalArgumentException(
                    "Active plan already exists for duration: " + request.getDuration());
        }

        MembershipPlan plan = MembershipPlan.builder()
                .name(request.getName())
                .description(request.getDescription())
                .duration(request.getDuration())
                .price(request.getPrice())
                .active(request.getActive() != null ? request.getActive() : true)
                .autoRenewDefault(request.getAutoRenewDefault() != null ? request.getAutoRenewDefault() : true)
                .build();

        planRepository.save(plan);

        return DtoMapper.toPlanResponse(plan);
    }

    @Transactional
    public PlanResponse updatePlan(UUID planId, UpdatePlanRequest request) {

        MembershipPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found: " + planId));

        // Determine final values after update
        PlanDuration finalDuration = request.getDuration() != null
                ? request.getDuration()
                : plan.getDuration();

        boolean finalActive = request.getActive() != null
                ? request.getActive()
                : plan.isActive();

        // Check if another active plan exists with same duration
        if (finalActive && planRepository.existsByDurationAndActiveTrueAndIdNot(finalDuration, planId)) {
            throw new IllegalArgumentException(
                    "Another active plan already exists for duration: " + finalDuration);
        }

        plan.updateFrom(request);
        return DtoMapper.toPlanResponse(plan);
    }

    @Transactional
    public void deletePlan(UUID planId) {

        MembershipPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found: " + planId));

        plan.setActive(false);
    }
}
