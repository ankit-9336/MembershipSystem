package org.example.membershipsystem.service;

import org.example.membershipsystem.exception.UserDoesNotExistException;
import org.example.membershipsystem.model.*;
import org.example.membershipsystem.repository.InMemoryStore;
import org.example.membershipsystem.strategy.TierEligibilityStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MembershipService {

    private final InMemoryStore store;
    private final Map<String, TierEligibilityStrategy> tierStrategies = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(MembershipService.class);

    public MembershipService(InMemoryStore store) {
        this.store = store;
    }

    public void registerStrategy(String tierId, TierEligibilityStrategy strategy) {
        tierStrategies.put(tierId, strategy);
    }

    public List<MembershipPlan> getAvailablePlans() {
        return store.getAllPlans();
    }

    public List<MembershipTier> getAvailableTiers() {
        return store.getAllTiers();
    }

    public Subscription subscribe(String userId, String planId, String tierId) {
        User user = store.getUser(userId);
        if (user == null) {
            throw new UserDoesNotExistException("User does not exist!");
        }

        MembershipPlan plan = store.getPlan(planId);
        MembershipTier tier = store.getTier(tierId);

        if (plan == null || tier == null) throw new IllegalArgumentException("Invalid plan or tier ID");

        user.getLock().writeLock().lock();
        try {
            if (user.getSubscription() != null && user.getSubscription().getStatus() == SubscriptionStatus.ACTIVE) {
                throw new IllegalStateException("User already has an active subscription.");
            }
            String subId = "SUB_" + UUID.randomUUID().toString().substring(0, 8);
            Subscription sub = new Subscription(subId, plan, tier);
            user.setSubscription(sub);
            return sub;
        } finally {
            user.getLock().writeLock().unlock();
        }
    }

    public boolean upgradeOrDowngradeTier(String userId, String targetTierId) {
        User user = store.getUser(userId);
        MembershipTier newTier = store.getTier(targetTierId);

        if (user == null) {
            throw new UserDoesNotExistException("User does not exist in the system!");
        }

        if (newTier == null) {
            throw new IllegalArgumentException("Target tier is invalid!");
        }

        user.getLock().writeLock().lock();
        try {
            Subscription sub = user.getSubscription();
            if (sub == null || sub.getStatus() != SubscriptionStatus.ACTIVE) return false;
            sub.changeTier(newTier);
            return true;
        } finally {
            user.getLock().writeLock().unlock();
        }
    }

    public boolean cancelSubscription(String userId) {
        User user = store.getUser(userId);
        if (user == null) return false;

        user.getLock().writeLock().lock();
        try {
            Subscription sub = user.getSubscription();
            if (sub != null && sub.getStatus() == SubscriptionStatus.ACTIVE) {
                sub.cancel();
                return true;
            }
            return false;
        } finally {
            user.getLock().writeLock().unlock();
        }
    }

    public Subscription trackMembership(String userId) {
        User user = store.getUser(userId);
        if (user == null) {
            throw new UserDoesNotExistException("User does not exist!");
        }
        ;

        user.getLock().readLock().lock();
        try {
            return user.getSubscription();
        } finally {
            user.getLock().readLock().unlock();
        }
    }

    public void evaluateAutomaticTierUpgrade(String userId, UserMetrics metrics) {
        User user = store.getUser(userId);
        if (user == null) return;

        user.getLock().readLock().lock();
        MembershipTier currentTier;
        try {
            Subscription sub = user.getSubscription();
            if (sub == null || sub.getStatus() != SubscriptionStatus.ACTIVE) return;
            currentTier = sub.getTier();
        } finally {
            user.getLock().readLock().unlock();
        }

        MembershipTier bestEligibleTier = currentTier;
        for (MembershipTier tier : store.getTiersCollection()) {
            if (tier.rank() > bestEligibleTier.rank()) {
                TierEligibilityStrategy strategy = tierStrategies.get(tier.tierId());
                if (strategy != null && strategy.isEligible(metrics)) {
                    bestEligibleTier = tier;
                }
            }
        }

        if (bestEligibleTier.rank() > currentTier.rank()) {
            upgradeOrDowngradeTier(userId, bestEligibleTier.tierId());
        }
    }
}
