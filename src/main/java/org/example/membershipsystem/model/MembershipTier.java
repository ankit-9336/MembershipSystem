package org.example.membershipsystem.model;

public record MembershipTier(String tierId, String name, int rank, TierBenefits benefits) {
}
