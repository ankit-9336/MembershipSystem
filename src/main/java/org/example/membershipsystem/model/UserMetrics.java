package org.example.membershipsystem.model;

public record UserMetrics(
        int totalOrders,
        double totalOrderValue,
        String cohortId
) {
}
