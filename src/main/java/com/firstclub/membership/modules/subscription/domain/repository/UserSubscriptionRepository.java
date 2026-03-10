package com.firstclub.membership.modules.subscription.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.modules.subscription.domain.entity.UserSubscription;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UUID> {

    Optional<UserSubscription> findByUserIdAndStatus(UUID userId, SubscriptionStatus status);

    boolean existsByUserIdAndStatus(UUID userId, SubscriptionStatus status);

    @Query("SELECT s FROM UserSubscription s WHERE s.status = :status AND s.endDate < :now")
    List<UserSubscription> findExpiredSubscriptions(
            @Param("status") SubscriptionStatus status,
            @Param("now") LocalDateTime now);

    List<UserSubscription> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
