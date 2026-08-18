package org.example.membershipsystem.model;

public record MembershipPlan(String planId, String name, BillingCycle cycle, double basePrice) {
}
