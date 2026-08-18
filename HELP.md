### Prerequisites

* Java Development Kit (JDK) 17 or higher installed on your machine (required for Java record types).

* IntelliJ IDEA (Community or Ultimate edition).

* Postman, Insomnia, or cURL (to test the REST APIs).

#### Step 1: Open the Project in IntelliJ
Launch IntelliJ IDEA.

Click File > Open (or "Open" on the welcome screen).

Navigate to the root folder where you saved the pom.xml file (e.g., the membership-system folder) and select it.

If prompted, click Trust Project.

Wait a few moments for IntelliJ to download the Maven dependencies and index the project. You will know it is done when the progress bar at the bottom right disappears and your folder icons show little blue squares (indicating they are recognized as Java source roots).

#### Step 2: Run the Application
You have two ways to start the server:

##### Option A: Using IntelliJ (Recommended)

In the project explorer on the left, navigate to: src/main/java/com/firstclub/MembershipApplication.java.

Open the file and look for the green "Play" triangle next to the public static void main method.

Click the green triangle and select Run 'MembershipApplication'.

##### Option B: Using the Terminal

Open the built-in terminal in IntelliJ (at the bottom of the screen).

Run the following Maven command:

Bash
`mvn spring-boot:run`
You will see the Spring Boot banner in the console. Look for the message ✅ Spring Boot Application Started - Initial Data Seeded. and Tomcat started on port 8080. This means your backend is running successfully!

#### Step 3: Test the APIs
With the server running, you can now interact with the endpoints. Open your terminal or command prompt and try out these cURL commands to simulate a user's journey.

(Note: If you use Postman, you can copy the URL and JSON body into the Postman UI).

1. ##### View Available Plans
   Check the catalog of plans loaded during startup.

```Bash
curl -X GET http://localhost:8080/api/membership/plans
```

2. ##### View Available Tiers
   Check the available tiers and their benefits.

``` Bash
curl -X GET http://localhost:8080/api/membership/tiers
```

3. #### Subscribe a User
   Subscribe our pre-seeded user (USER_101) to the Monthly Plan (P_MONTHLY) at the Silver Tier (T_SILVER).

``` Bash
curl -X POST http://localhost:8080/api/membership/subscribe \
-H "Content-Type: application/json" \
-d '{"userId": "USER_101", "planId": "P_MONTHLY", "tierId": "T_SILVER"}'
```

4. #### Track Membership
   Verify the user's active subscription and expiry date.

```Bash
curl -X GET http://localhost:8080/api/membership/USER_101 
```

5. #### Simulate an Order Placed (Automatic Tier Upgrade)
   Simulate a webhook event where the user just spent 600 Rs. The system will evaluate this against the Platinum strategy threshold (>500 Rs) and automatically upgrade them.

``` Bash
curl -X POST "http://localhost:8080/api/membership/webhooks/order-placed?userId=USER_101" \
-H "Content-Type: application/json" \
-d '{"totalOrders": 12, "totalOrderValue": 600.00, "cohortId": "COHORT_A"}'
```

6. ##### Verify the Upgrade
   Run the tracking endpoint again. You will see their tier has changed from T_SILVER to T_PLATINUM.

```Bash
curl -X GET http://localhost:8080/api/membership/USER_101
```

7. ##### Cancel Subscription
   Cancel the user's active membership.

``` Bash
curl -X DELETE http://localhost:8080/api/membership/USER_101
```