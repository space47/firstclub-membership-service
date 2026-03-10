package com.firstclub.membership.modules.membershipplan.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.firstclub.membership.enums.PlanDuration;
import com.firstclub.membership.modules.membershipplan.domain.entity.MembershipPlan;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, UUID> {
    List<MembershipPlan> findByActiveTrue();

    List<MembershipPlan> findByDurationAndActiveTrue(PlanDuration duration);

    boolean existsByDurationAndActiveTrue(PlanDuration duration);

    boolean existsByDurationAndActiveTrueAndIdNot(PlanDuration duration, UUID id);
}
