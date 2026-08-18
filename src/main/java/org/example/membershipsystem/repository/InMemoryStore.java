package org.example.membershipsystem.repository;

import org.example.membershipsystem.model.MembershipPlan;
import org.example.membershipsystem.model.MembershipTier;
import org.example.membershipsystem.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryStore {
    private final Map<String, MembershipPlan> plans = new ConcurrentHashMap<>();
    private final Map<String, MembershipTier> tiers = new ConcurrentHashMap<>();
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public void savePlan(MembershipPlan plan) {
        plans.put(plan.planId(), plan);
    }

    public void saveTier(MembershipTier tier) {
        tiers.put(tier.tierId(), tier);
    }

    public void saveUser(User user) {
        users.put(user.getUserId(), user);
    }

    public MembershipPlan getPlan(String planId) {
        return plans.get(planId);
    }

    public MembershipTier getTier(String tierId) {
        return tiers.get(tierId);
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public List<MembershipPlan> getAllPlans() {
        return new ArrayList<>(plans.values());
    }

    public List<MembershipTier> getAllTiers() {
        return new ArrayList<>(tiers.values());
    }

    public Collection<MembershipTier> getTiersCollection() {
        return tiers.values();
    }
}
