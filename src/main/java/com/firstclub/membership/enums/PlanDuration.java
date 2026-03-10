package com.firstclub.membership.enums;

public enum PlanDuration {
    MONTHLY(30),
    QUARTERLY(90),
    YEARLY(365);

    private final int days;

    PlanDuration(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }
}
