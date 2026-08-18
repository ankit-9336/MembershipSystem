package org.example.membershipsystem.model;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User {
    private final String userId;
    private Subscription subscription;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public User(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public ReentrantReadWriteLock getLock() {
        return lock;
    }
}
