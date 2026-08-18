package org.example.membershipsystem.model;

public record TierBenefits(
        double discountPercentage,
        boolean freeDelivery,
        boolean prioritySupport,
        boolean earlyAccessSales
) {
}
