package com.firstclub.membership.modules.tier.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.firstclub.membership.enums.BenefitType;
import com.firstclub.membership.modules.tier.domain.entity.TierBenefit;

@Repository
public interface TierBenefitRepository extends JpaRepository<TierBenefit, UUID> {
    Optional<TierBenefit> findById(UUID benefitId);

    boolean existsByTierIdAndBenefitType(UUID TierId, BenefitType benefitType);
}
