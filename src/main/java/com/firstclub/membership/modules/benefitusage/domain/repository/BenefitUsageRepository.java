package com.firstclub.membership.modules.benefitusage.domain.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.firstclub.membership.enums.BenefitType;
import com.firstclub.membership.modules.benefitusage.domain.entity.BenefitUsage;

@Repository
public interface BenefitUsageRepository extends JpaRepository<BenefitUsage, UUID> {

    /**
     * Count how many times a user has used a specific benefit since a given date.
     * 
     * Called as: countUsage(userId, FREE_DELIVERY, startOfCurrentMonth)
     * Returns: 2 → meaning user has used 2 of their 3 free deliveries this month
     */
    @Query("SELECT COUNT(u) FROM BenefitUsage u " +
            "WHERE u.userId = :userId " +
            "AND u.benefitType = :benefitType " +
            "AND u.usedAt >= :since")
    int countUsage(
            @Param("userId") UUID userId,
            @Param("benefitType") BenefitType benefitType,
            @Param("since") LocalDateTime since);
}