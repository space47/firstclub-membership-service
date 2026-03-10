package com.firstclub.membership.common.controller;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firstclub.membership.common.dto.ApiResponse;
import com.firstclub.membership.external.order.MockOrderService;

@RestController
@RequestMapping("/api/v1/demo")
public class DemoController {

    private final MockOrderService orderService;

    public DemoController(MockOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/simulate-orders")
    public ResponseEntity<ApiResponse<Map<String, Object>>> simulateOrders(
            @RequestBody Map<String, Object> request) {
        UUID userId = UUID.fromString((String) request.get("userId"));
        int count = (int) request.get("orderCount");
        BigDecimal value = new BigDecimal(request.get("totalValue").toString());

        orderService.simulateOrders(userId, count, value);

        return ResponseEntity.ok(ApiResponse.ok(
                orderService.getUserStats(userId),
                "Orders simulated successfully"));
    }

    @PostMapping("/add-cohort")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addCohort(
            @RequestBody Map<String, Object> request) {
        UUID userId = UUID.fromString((String) request.get("userId"));
        String cohort = (String) request.get("cohortName");

        orderService.addUserToCohort(userId, cohort);

        return ResponseEntity.ok(ApiResponse.ok(
                orderService.getUserStats(userId),
                "User added to cohort"));
    }

    @GetMapping("/user-stats/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStats(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getUserStats(userId)));
    }

    @DeleteMapping("/reset/{userId}")
    public ResponseEntity<ApiResponse<String>> resetUser(@PathVariable UUID userId) {
        orderService.resetUser(userId);
        return ResponseEntity.ok(ApiResponse.ok("User data reset", "Mock data cleared"));
    }
}
