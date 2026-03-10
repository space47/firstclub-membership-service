# FirstClub Membership Service

A production-grade membership program backend with tiered benefits, built with **Spring Boot 3.2**, **PostgreSQL**, and a **strategy-pattern-based tier evaluation engine**.

---

## Architecture Overview

```
                                    HTTP Requests
                                          │
       ┌──────────────────┬───────────────┼────────────────┬────────────────────┐
       │                  │               │                │                    │
┌──────▼───────┐  ┌───────▼──────┐  ┌────▼──────┐  ┌──────▼──────┐  ┌─────────▼────────┐
│  /api/v1     │  │  /api/v1     │  │ /api/v1   │  │  /api/v1    │  │   /api/v1        │
│  /plans      │  │  /subscript- │  │  /tiers   │  │  /benefits  │  │   /demo          │
│              │  │  ions        │  │           │  │             │  │                  │
│ GET    /     │  │ POST /subscri│  │ GET    /  │  │ GET /check  │  │ POST /simulate-  │
│ GET    /{id} │  │ POST /cancel │  │ GET /{id} │  │  /{uid}/{bt}│  │       orders     │
│ POST   /     │  │ PUT  /change │  │ POST /    │  │ POST/record-│  │ POST /add-cohort │
│ PATCH  /{id} │  │       -plan  │  │ PATCH/{id}│  │      usage  │  │ GET  /user-stats │
│ DELETE /{id} │  │ GET  /user/  │  │ DELETE/{} │  │             │  │      /{userId}   │
│              │  │      {id}    │  │ POST /{id}│  │             │  │ DELETE /reset/   │
│              │  │ GET  /user/  │  │   /benefi-│  │             │  │        {userId}  │
│              │  │   {id}/hist- │  │   ts      │  │             │  │                  │
│              │  │   ory        │  │ PATCH/DELE│  │             │  │  [calls Mock     │
│              │  │              │  │ /benefits/│  │             │  │   OrderService   │
│              │  │              │  │{benefitId}│  │             │  │   directly]      │
└──────┬───────┘  └───────┬──────┘  └────┬──────┘  └──────┬──────┘  └─────────┬────────┘
       │                  │               │                │                    │
       │                  │          ┌────┴──────┐         │                    │
       │                  │          │TierService│         │                    │
       │                  │          │           │         │                    │
       │                  │          │ CRUD for  │         │                    │
       │                  │          │ tiers &   │         │                    │
       │                  │          │ benefits  │         │                    │
       │                  │          └────┬──────┘         │                    │
       │                  │               │                │                    │
┌──────▼───────┐  ┌───────▼──────┐  ┌────▼──────────────┐ │          ┌─────────▼────────┐
│Membership    │  │Subscription  │  │TierEvaluationSvc  │ │          │  MockOrderService │
│PlanService   │  │Service       │  │                   │ │          │  (in-memory,      │
│              │  │              │  │ evaluateAndAssign │ │          │  resets on        │
│ Plan CRUD    │  │ subscribe()  │  │ Tier()            │ │          │  restart)         │
│              │  │ cancel()     │  │ evaluateAndAssign │ │          │                   │
│              │  │ changePlan() │  │ TierAsync()       │ │          │ simulateOrders()  │
│              │  │ getUserMemb- │  │ checkEligibility()│ │          │ addUserToCohort() │
│              │  │ ership()     │  └────┬──────────────┘ │          │ getUserStats()    │
└──────┬───────┘  └───────┬──────┘       │                │          │ resetUser()       │
       │                  │               │                │          └─────────┬────────┘
       │                  │     ┌─────────▼──────────────┐ │                    │
       │                  │     │  TierEligibilityEvalua- │ │                    │
       │                  │     │  tor (Strategy Dispatch)│ │                    │
       │                  │     │                         │ │                    │
       │                  │     │  ┌───────────────────┐  │ │                    │
       │                  │     │  │ OrderCountStrategy│──┼─┼────────────────────┤
       │                  │     │  │ OrderValueStrategy│──┼─┼────────────────────┤
       │                  │     │  │ CohortStrategy    │──┼─┼────────────────────┘
       │                  │     │  └───────────────────┘  │ │
       │                  │     └─────────┬───────────────┘ │
       │                  │               │                  │
       │                  │     ┌─────────▼──────────────┐   │
       │                  │     │  BenefitService         │◄──┘
       │                  │     │                         │
       │                  │     │  checkBenefitEligibil- │
       │                  │     │  ity()                  │
       │                  │     │  recordUsage()          │
       │                  │     └─────────┬──────────────┘
       │                  │               │
       └──────────────────┼───────────────┘
                          │
       ┌──────────────────┼─────────────────────────────────┐
       │                  │                                  │
┌──────▼───────┐  ┌───────▼──────────────┐  ┌──────────────▼──────┐
│Membership    │  │UserSubscription      │  │MembershipTier       │
│Plan          │  │Repository            │  │Repository           │
│Repository    │  │(@Version optimistic  │  │                     │
│              │  │ locking)             │  │TierBenefit          │
│              │  │                      │  │Repository           │
└──────┬───────┘  └───────┬──────────────┘  │                     │
       │                  │                  │UserTier             │
       │                  │                  │Repository           │
       │                  │                  │(@Version optimistic │
       │                  │                  │ locking)            │
       │                  │                  └──────────┬──────────┘
       │                  │         ┌────────────────────▼──────────┐
       │                  │         │  BenefitUsage Repository      │
       │                  │         └────────────────────┬──────────┘
       │                  │                              │
       └──────────────────┼──────────────────────────────┘
                          │
          ┌───────────────▼──────────────────────────────────┐
          │                   PostgreSQL                      │
          │                                                   │
          │  membership_plans    membership_tiers             │
          │  tier_benefits       tier_eligibility_criteria    │
          │  user_subscriptions  user_tiers                   │
          │  benefit_usage                                    │
          └───────────────────────────────────────────────────┘

  ┌─────────────────────────────────────┐
  │  SubscriptionExpiryJob (@Scheduled) │──► UserSubscriptionRepository
  └─────────────────────────────────────┘
```

---

## Key Design Decisions

### 1. Plan vs Tier Separation
- **Plan** = what the user *pays* for (Monthly / Quarterly / Yearly)
- **Tier** = what the user *earns* (Silver / Gold / Platinum)
- These are independent: a user on a Monthly plan can be Platinum tier if they order enough.

### 2. Strategy Pattern for Tier Eligibility
- Each eligibility rule (order count, order value, cohort) is a separate `TierEligibilityStrategy`.
- Adding a new rule = new class + new enum value. Zero changes to existing code.
- `TierEligibilityEvaluator` auto-discovers all strategies via Spring dependency injection.

### 3. Concurrency
- **Optimistic Locking** (`@Version`) on `UserSubscription` and `UserTier` prevents lost updates.
- **ConcurrentHashMap** in `MockOrderService` for thread-safe in-memory data.
- **Configurable thread pool** for async tier evaluation.
- **Unique constraint** prevents duplicate active subscriptions.

### 4. Configurable Benefits
- Benefits are DB rows, not code. Add/remove/modify benefits by changing the `tier_benefits` table.
- No code deployment needed to change what perks a tier offers.

## Setup

### Prerequisites
- **Java 17+** — verify: `java -version`
- **Docker & Docker Compose** — for PostgreSQL
- **Maven 3.8+** — see install options below

### Installing Maven (if needed)

```bash
# macOS
brew install maven

# Ubuntu/Debian
sudo apt install maven

# Windows (via Chocolatey)
choco install maven

# Or download from: https://maven.apache.org/download.cgi
```

Verify: `mvn -version`

### Run

```bash
# 1. Start PostgreSQL
docker-compose up -d

# 2. Build and run (pick ONE option)

# Option A: Maven wrapper (auto-downloads Maven if needed)
chmod +x mvnw
./mvnw spring-boot:run

# Option B: Globally installed Maven
mvn spring-boot:run

# Server starts on http://localhost:8080
```

### Troubleshooting

| Issue | Fix |
|-------|-----|
| `./mvnw: Permission denied` | Run `chmod +x mvnw` |
| `./mvnw` hangs or fails | Install Maven globally and use `mvn spring-boot:run` |
| Port 5432 in use | `docker-compose down` then `docker-compose up -d` |
| Compilation errors | Ensure Java 17+: `java -version` |

---

## API Demo Walkthrough

**Postman collection** is included with the complete flow and test cases.
1. Import the Postman collection JSON into Postman.
2. Follow the step-by-step requests to exercise the full membership lifecycle.

---

## API Reference

### Plans & Tiers (Read)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/plans/` | List active plans |
| GET | `/api/v1/plans/{planId}` | Get plan details |
| GET | `/api/v1/tiers/` | List tiers with benefits & criteria |
| GET | `/api/v1/tiers/{id}` | Get tier details |

### Subscriptions (User Interaction)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/subscriptions/subscribe` | Subscribe to a plan |
| POST | `/api/v1/subscriptions/cancel` | Cancel subscription |
| PUT | `/api/v1/subscriptions/change-plan` | Upgrade / downgrade plan |
| GET | `/api/v1/subscriptions/user/{id}` | Full membership status |
| GET | `/api/v1/subscriptions/user/{id}/history` | Subscription history |

### Tier Evaluation
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/tiers/evaluate-tier` | Sync tier evaluation |
| POST | `/api/v1/tiers/evaluate-tier-async` | Async tier evaluation |
| GET | `/api/v1/tiers/check-eligibility/{userId}/{tierId}` | Check tier eligibility |
| GET | `/api/v1/tiers/users/{userId}/tier` | Get user's current tier |

### Tier Management (Admin)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/tiers/` | Create tier |
| PATCH | `/api/v1/tiers/{tierId}` | Update tier |
| DELETE | `/api/v1/tiers/{tierId}` | Delete tier |
| POST | `/api/v1/tiers/benefits/{tierId}/benefits` | Add benefit to tier |
| PATCH | `/api/v1/tiers/benefits/{benefitId}` | Update benefit |
| DELETE | `/api/v1/tiers/benefits/{benefitId}` | Delete benefit |

### Plan Management (Admin)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/plans/` | Create plan |
| PATCH | `/api/v1/plans/{planId}` | Update plan |
| DELETE | `/api/v1/plans/{planId}` | Delete plan |

### Benefit Usage
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/benefits/check/{userId}/{benefitType}` | Check benefit availability |
| POST | `/api/v1/benefits/record-usage` | Record benefit usage |

### Demo (Simulated Data — resets on restart)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/demo/simulate-orders` | Simulate orders for a user |
| POST | `/api/v1/demo/add-cohort` | Add user to a cohort |
| GET | `/api/v1/demo/user-stats/{id}` | View mock user stats |

---

## Production Considerations (Not Implemented)

| Area | Recommendation |
|------|----------------|
| Authentication | Spring Security + JWT |
| Payment | Razorpay / Stripe for subscription billing |
| Event-Driven | Kafka for order events → automatic tier re-evaluation |
| Caching | Redis for plan/tier catalog (changes rarely) |
| DB Migrations | Flyway instead of `ddl-auto: create-drop` |
| Rate Limiting | Spring Cloud Gateway or Resilience4j |
| Observability | Micrometer + Prometheus + Grafana |
| Distributed Locking | Redis / ZooKeeper for scheduled jobs across instances |
