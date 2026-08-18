package org.example.membershipsystem;

import org.example.membershipsystem.model.*;
import org.example.membershipsystem.repository.InMemoryStore;
import org.example.membershipsystem.service.MembershipService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MembershipSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MembershipSystemApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedData(InMemoryStore store, MembershipService service) {
        return args -> {
            store.savePlan(new MembershipPlan("P_MONTHLY", "Monthly Saver", BillingCycle.MONTHLY, 9.99));
            store.savePlan(new MembershipPlan("P_YEARLY", "Yearly Elite", BillingCycle.YEARLY, 99.99));

            store.saveTier(new MembershipTier("T_SILVER", "Silver", 1, new TierBenefits(5.0, false, false, false)));
            store.saveTier(new MembershipTier("T_GOLD", "Gold", 2, new TierBenefits(10.0, true, false, true)));
            store.saveTier(new MembershipTier("T_PLATINUM", "Platinum", 3, new TierBenefits(20.0, true, true, true)));

            service.registerStrategy("T_GOLD", metrics -> metrics.totalOrders() >= 10);
            service.registerStrategy("T_PLATINUM", metrics -> metrics.totalOrderValue() >= 500.00);

            store.saveUser(new User("USER_101"));
            store.saveUser(new User("USER_102"));
            System.out.println("✅ Spring Boot Application Started - Initial Data Seeded.");
        };
    }

}
