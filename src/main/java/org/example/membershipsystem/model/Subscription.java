package org.example.membershipsystem.model;

import java.time.LocalDate;

public class Subscription {
    private final String subscriptionId;
    private final MembershipPlan plan;
    private MembershipTier tier;
    private final LocalDate startDate;
    private final LocalDate expiryDate;
    private SubscriptionStatus status;

    public Subscription(String subscriptionId, MembershipPlan plan, MembershipTier tier) {
        this.subscriptionId = subscriptionId;
        this.plan = plan;
        this.tier = tier;
        this.startDate = LocalDate.now();
        this.expiryDate = this.startDate.plusMonths(
                plan.cycle() == BillingCycle.MONTHLY ? 1 :
                        plan.cycle() == BillingCycle.QUARTERLY ? 3 : 12
        );
        this.status = SubscriptionStatus.ACTIVE;
    }

    public void changeTier(MembershipTier newTier) {
        this.tier = newTier;
    }

    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public MembershipPlan getPlan() {
        return plan;
    }

    public MembershipTier getTier() {
        return tier;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }
}
