package com.firstclub.membership.modules.tier.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.firstclub.membership.modules.tier.domain.entity.UserTier;

@Repository
public interface UserTierRepository extends JpaRepository<UserTier, UUID> {
    Optional<UserTier> findByUserId(UUID userId);

    Optional<UserTier> findTopByUserIdOrderByAssignedAtDesc(UUID userId);

    boolean existsByUserId(UUID userId);
}
