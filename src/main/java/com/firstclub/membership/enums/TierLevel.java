package com.firstclub.membership.enums;

public enum TierLevel {
    SILVER(1),
    GOLD(2),
    PLATINUM(3);

    private final int rank;

    TierLevel(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public boolean isHigherThan(TierLevel other) {
        return this.rank > other.rank;
    }

    public boolean isLowerThan(TierLevel other) {
        return this.rank < other.rank;
    }
}
