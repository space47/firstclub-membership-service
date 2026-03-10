package com.firstclub.membership.modules.subscription.dto.response;

import java.util.UUID;

import com.firstclub.membership.modules.tier.dto.response.UserTierResponse;

public class UserMembershipResponse {
    private UUID userId;
    private SubscriptionResponse subscription;
    private UserTierResponse tier;
    private boolean isMember;

    public UserMembershipResponse() {
    }

    private UserMembershipResponse(Builder b) {
        this.userId = b.userId;
        this.subscription = b.subscription;
        this.tier = b.tier;
        this.isMember = b.isMember;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public SubscriptionResponse getSubscription() {
        return subscription;
    }

    public void setSubscription(SubscriptionResponse subscription) {
        this.subscription = subscription;
    }

    public UserTierResponse getTier() {
        return tier;
    }

    public void setTier(UserTierResponse tier) {
        this.tier = tier;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean member) {
        this.isMember = member;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID userId;
        private SubscriptionResponse subscription;
        private UserTierResponse tier;
        private boolean isMember;

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder subscription(SubscriptionResponse sub) {
            this.subscription = sub;
            return this;
        }

        public Builder tier(UserTierResponse tier) {
            this.tier = tier;
            return this;
        }

        public Builder isMember(boolean isMember) {
            this.isMember = isMember;
            return this;
        }

        public UserMembershipResponse build() {
            return new UserMembershipResponse(this);
        }
    }
}