package com.firstclub.membership.jobs;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.modules.subscription.domain.entity.UserSubscription;
import com.firstclub.membership.modules.subscription.domain.repository.UserSubscriptionRepository;

@Component
public class SubscriptionExpiryJob {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionExpiryJob.class);

    private final UserSubscriptionRepository subscriptionRepository;

    public SubscriptionExpiryJob(UserSubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void markExpiredSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        List<UserSubscription> expired = subscriptionRepository
                .findExpiredSubscriptions(SubscriptionStatus.ACTIVE, now);

        if (expired.isEmpty())
            return;

        for (UserSubscription sub : expired) {
            sub.setStatus(SubscriptionStatus.EXPIRED);
            log.info("Subscription {} for user {} has expired", sub.getId(), sub.getUserId());
        }

        subscriptionRepository.saveAll(expired);
        log.info("Marked {} subscriptions as expired", expired.size());
    }
}
