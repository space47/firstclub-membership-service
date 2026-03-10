package com.firstclub.membership.modules.membershipplan.dto.request;

import java.math.BigDecimal;

import com.firstclub.membership.enums.PlanDuration;

public class UpdatePlanRequest {

    private String name;
    private String description;
    private PlanDuration duration;
    private BigDecimal price;
    private Boolean active;
    private Boolean autoRenewDefault;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PlanDuration getDuration() {
        return duration;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Boolean getActive() {
        return active;
    }

    public Boolean getAutoRenewDefault() {
        return autoRenewDefault;
    }
}