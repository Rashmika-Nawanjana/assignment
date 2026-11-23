# Test Cases — Sun Travels ERP

Linked to `TEST-PLAN.md`. Priority: C = Critical, H = High, M = Medium.

## Auth

| ID | Title | Steps | Expected | P |
|----|-------|-------|----------|---|
| TC-A01 | Login success | Login as `agent` / `Agent@12345` | ERP shell; search visible | C |
| TC-A02 | Login wrong password | Valid user, wrong password | Error; stay on login | C |
| TC-A03 | Login empty fields | Submit blank | Message to fill username/password | M |
| TC-A04 | Signup strong password | New user with strong password | AGENT created; can login | H |
| TC-A05 | Signup weak password | Password `123` | Rejected with policy hint | H |

## Contracts

| ID | Title | Steps | Expected | P |
|----|-------|-------|----------|---|
| TC-C01 | Agent cannot edit | Login agent; open Contracts | No save form / no edit delete | H |
| TC-C02 | Manager creates contract | Hotel, dates, markup 1.15, Standard $35, 15 rooms, max 2 | Saved; appears in list | C |
| TC-C03 | Contract missing fields | Save with empty hotel | Fill-required message | M |
| TC-C04 | Search contracts | Type hotel name; Search | Filtered list | M |
| TC-C05 | Edit markup | Change 1.15 → 1.20; save | Search prices use new markup | H |

## Search (assignment core)

| ID | Title | Steps | Expected | P |
|----|-------|-------|----------|---|
| TC-S01 | Happy search | Contract Hilton-like; check-in inside validity; 2 nights; 1 room 2 adults | Results with hotel, type, price, status | C |
| TC-S02 | Price formula | Rate 35, markup 1.15, 2 nights, 2 adults | Price **161.00** | C |
| TC-S03 | Outside validity | Check-in outside contract | No that hotel / empty or not listed | C |
| TC-S04 | Adults exceed max | Standard max 2; request 3 adults | Not available for Standard | C |
| TC-S05 | Rooms exceed inventory | Request more rooms than free | Not available | C |
| TC-S06 | Multi-room search | 1×2 adults + 1×1 adult | Price sums both rooms’ adults | H |
| TC-S07 | Empty criteria | Search without date | Fill check-in message | M |
| TC-S08 | Pagination | Seed data; search | ≤10 rows/page; Next works | M |

## Booking

| ID | Title | Steps | Expected | P |
|----|-------|-------|----------|---|
| TC-B01 | Book available room | Book room → pick free # → guest+NIC → confirm | Success; booking in list with Room N | C |
| TC-B02 | Double book same room | Two users book same type, same dates, **same room number** | Second fails; error near booking form | C |
| TC-B03 | Different room numbers | Same type/dates, Room 1 then Room 2 | Both succeed if inventory allows | H |
| TC-B04 | Cancel frees room | Cancel confirmed; book same room again | Allowed | H |
| TC-B05 | Missing guest fields | Confirm without name/NIC | Fill-required on booking form | M |

## Admin / profile

| ID | Title | Steps | Expected | P |
|----|-------|-------|----------|---|
| TC-U01 | Admin creates user | Users → new MANAGER | User can login | H |
| TC-U02 | Agent blocked from Users | Login agent; hit /users if possible | Redirect / forbidden | H |
| TC-P01 | Change password | Profile → current + new strong | Success; login with new | M |

## Unit automation (run locally)

```bash
cd backend && ./mvnw test
cd frontend && npx ng test --no-watch --browsers=ChromeHeadless
```

| Suite | Covers |
|-------|--------|
| `SearchServiceTest` | Markup price, unavailable adults, empty contracts, filled inventory |
| `BookingServiceTest` | Reject overlapping same room number |
| Angular specs | Auth service / search component smoke |

## Traceability (sample)

| Story | Cases |
|-------|-------|
| US-07 Room search | TC-S01–S07 |
| US-09 Book a room | TC-B01–B05 |
| US-04 Load contract | TC-C02–C03 |
