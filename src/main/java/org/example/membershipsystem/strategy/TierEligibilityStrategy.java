package org.example.membershipsystem.strategy;

import org.example.membershipsystem.model.UserMetrics;

public interface TierEligibilityStrategy {
    boolean isEligible(UserMetrics metrics);
}