package com.firstclub.membership.modules.tier.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.modules.tier.domain.entity.MembershipTier;

@Repository
public interface MembershipTierRepository extends JpaRepository<MembershipTier, UUID> {
    Optional<MembershipTier> findByTierLevel(TierLevel tierLevel);

    boolean existsByTierLevel(TierLevel tierLevel);
}
