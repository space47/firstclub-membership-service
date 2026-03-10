package com.firstclub.membership.external.order;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MockOrderService {

    private static final Logger log = LoggerFactory.getLogger(MockOrderService.class);

    private final ConcurrentHashMap<UUID, Integer> orderCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, BigDecimal> orderValues = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Set<String>> userCohorts = new ConcurrentHashMap<>();

    public int getOrderCount(UUID userId, int periodDays) {
        return orderCounts.getOrDefault(userId, 0);
    }

    public BigDecimal getTotalOrderValue(UUID userId, int periodDays) {
        return orderValues.getOrDefault(userId, BigDecimal.ZERO);
    }

    public boolean isUserInCohort(UUID userId, String cohortName) {
        Set<String> cohorts = userCohorts.getOrDefault(userId, Collections.emptySet());
        return cohorts.contains(cohortName);
    }

    public void simulateOrders(UUID userId, int count, BigDecimal totalValue) {
        orderCounts.compute(userId, (k, existing) -> (existing == null ? 0 : existing) + count);
        orderValues.compute(userId, (k, existing) -> (existing == null ? BigDecimal.ZERO : existing).add(totalValue));
        log.info("Simulated {} orders worth {} for user {}", count, totalValue, userId);
    }

    public void addUserToCohort(UUID userId, String cohortName) {
        userCohorts.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(cohortName);
        log.info("Added user {} to cohort {}", userId, cohortName);
    }

    public void resetUser(UUID userId) {
        orderCounts.remove(userId);
        orderValues.remove(userId);
        userCohorts.remove(userId);
    }

    public Map<String, Object> getUserStats(UUID userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", userId);
        stats.put("orderCount", orderCounts.getOrDefault(userId, 0));
        stats.put("totalOrderValue", orderValues.getOrDefault(userId, BigDecimal.ZERO));
        stats.put("cohorts", userCohorts.getOrDefault(userId, Collections.emptySet()));
        return stats;
    }
}
