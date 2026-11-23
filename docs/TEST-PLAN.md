# Test Plan — Sun Travels ERP

## 1. Purpose

Verify that contract loading, room search pricing/availability, booking with room numbers, auth/roles, and core UX behave as specified in the training assignment and user stories.

## 2. Scope

**In scope**

- Auth (login, signup, JWT session)  
- Contracts CRUD (role-gated)  
- Search criteria, price formula, Available/Not available  
- Booking + double-book prevention on room number  
- Bookings list/cancel  
- Admin users, profile  
- UI validation, loading, empty states  

**Out of scope**

- Performance SLAs beyond manual latency feel  
- Security penetration testing  
- Mobile-native apps  
- Payment / OTA integrations  

## 3. Test items

| ID | Item |
|----|------|
| TI-01 | Backend SearchService pricing & availability |
| TI-02 | Backend BookingService room lock & overlap |
| TI-03 | Angular auth + search flows (manual + unit) |
| TI-04 | Contracts UI/API |
| TI-05 | End-to-end agent happy path |

## 4. Approach

- **Unit:** JUnit (services), Jasmine/Karma (selected Angular)  
- **Manual functional:** Test cases in `TEST-CASES.md`  
- **Peer testing:** Execute peer’s cases; log defects in `PEER-TESTING.md`  

## 5. Entry criteria

Testing of a build may **start** only when all are true:

1. Backend starts and connects to MySQL (`docker compose up -d` or local DB)  
2. Frontend builds/serves without compile errors  
3. Demo users login successfully (admin / manager / agent)  
4. At least one contract exists covering the test stay dates (or seed data loaded)  
5. Test environment URL noted (localhost:4200 / :8080)  
6. Tester has this test plan + test cases document  

## 6. Exit criteria

A release/demo build may be **signed off** when:

1. All **Critical** and **High** test cases passed  
2. No open Critical defects  
3. Medium defects documented with workaround or fix plan  
4. Unit tests for SearchService (and BookingService) pass in CI/local Maven  
5. Peer testing session completed and findings logged  
6. User manual matches actual UI labels for the demo path  

## 7. Pass / fail

- **Pass:** Actual result matches expected in the test case  
- **Fail:** Deviation; log defect (ID, steps, severity, screenshot if UI)  

### Severity

| Level | Meaning |
|-------|---------|
| Critical | Cannot login, search, or book; wrong price formula; double-book same room succeeds |
| High | Role bypass; contract save broken; availability clearly wrong |
| Medium | UX/validation gaps; pagination/sort issues |
| Low | Cosmetic |

## 8. Roles & schedule

| Role | Responsibility |
|------|----------------|
| Developer | Unit tests, fix defects |
| Peer tester | Execute cases on peer system; raise issues |
| Author | Execute own regression before viva |

Schedule: align with assignment windows (stories → test cases → development → peer test before viva).

## 9. Environment

- OS: Windows / macOS / Linux  
- JDK 21+, Node 18+, Docker (MySQL 8)  
- Browsers: Chrome or Edge latest  

## 10. Risks

| Risk | Mitigation |
|------|------------|
| Port 3306/8080 conflict | Document alternate ports; kill stale processes |
| Seed data already present | Idempotent seed; `docker compose down -v` for clean DB |
| Clock/date confusion | Use dates inside seeded contract validity |
