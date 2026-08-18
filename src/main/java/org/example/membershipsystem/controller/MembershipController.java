package org.example.membershipsystem.controller;

import org.example.membershipsystem.dto.ChangeTierRequest;
import org.example.membershipsystem.dto.SubscribeRequest;
import org.example.membershipsystem.exception.UserDoesNotExistException;
import org.example.membershipsystem.model.MembershipPlan;
import org.example.membershipsystem.model.MembershipTier;
import org.example.membershipsystem.model.Subscription;
import org.example.membershipsystem.model.UserMetrics;
import org.example.membershipsystem.service.MembershipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership")
public class MembershipController {

    private final MembershipService service;

    public MembershipController(MembershipService service) {
        this.service = service;
    }

    @GetMapping("/plans")
    public ResponseEntity<List<MembershipPlan>> getPlans() {
        return ResponseEntity.ok(service.getAvailablePlans());
    }

    @GetMapping("/tiers")
    public ResponseEntity<List<MembershipTier>> getTiers() {
        return ResponseEntity.ok(service.getAvailableTiers());
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody SubscribeRequest req) {
        try {
            Subscription sub = service.subscribe(req.userId(), req.planId(), req.tierId());
            return ResponseEntity.ok(sub);
        } catch (UserDoesNotExistException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/tier")
    public ResponseEntity<String> changeTier(@RequestBody ChangeTierRequest req) {
        try {
            boolean success = service.upgradeOrDowngradeTier(req.userId(), req.tierId());
            return success ? ResponseEntity.ok("Tier changed successfully!") : ResponseEntity.badRequest()
                    .body("Failed to change tier!");
        } catch (UserDoesNotExistException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> cancelSubscription(@PathVariable String userId) {
        boolean success = service.cancelSubscription(userId);
        return success ? ResponseEntity.ok("Subscription cancelled") : ResponseEntity.badRequest()
                .body("Failed to cancel");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> trackMembership(@PathVariable String userId) {
        try {
            Subscription sub = service.trackMembership(userId);
            return sub != null ? ResponseEntity.ok(sub) : ResponseEntity.badRequest()
                    .body("Subscription does not exist!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/webhooks/order-placed")
    public ResponseEntity<String> handleOrderPlacedEvent(@RequestBody UserMetrics metrics,
                                                         @RequestParam String userId) {
        service.evaluateAutomaticTierUpgrade(userId, metrics);
        return ResponseEntity.ok("User evaluation complete.");
    }
}
