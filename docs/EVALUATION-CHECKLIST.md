# Evaluation checklist — Sun Travels ERP

Mapped to `evaluation-criteria.md`. Status: **Met** / **Partial** / **Gap**.

| Criterion | Status | Notes |
|-----------|--------|--------|
| **ER Design** — attributes, relationships, normalization, data volumes | **Met** (doc) | See `ER-DIAGRAM.md`. 3NF-ish: Hotel→Contract→RoomType; Booking refs RoomType + User. Volumes discussed for search/booking indexes + seed scale. |
| **UI Design** — guidelines, novelty, UX | **Met** | Angular ERP shell, role nav, searchable/sortable/paginated tables, form hints, confirm modal, booking scroll. Distinct teal/Fraunces brand (not generic purple template). |
| **Application Flow** — end-to-end, errors, guidance | **Met** | Login → role home → contracts / search → book (room#) → bookings. Validation messages, booking-panel errors, empty states, loading states. |
| **Unit Tests** — UI + service | **Met** | Backend: Auth + Search + Booking service tests. Frontend: guards, login/signup, search, auth service, utils (~29 specs). |
| **User Stories & Manual** | **Met** (doc) | `USER-STORIES.md`, `USER-MANUAL.md`. |
| **Test Case Writing** — full scope + plan with entry/exit | **Met** (doc) | `TEST-PLAN.md`, `TEST-CASES.md`. |
| **Peer Testing** | **You must do** | Template in `PEER-TESTING.md`. Execute against a peer’s system and raise issues; they test yours. |
| **Code Quality** — CG practices | **Partial / defendable** | Layered Spring services, constructors, comments on key logic, Angular standalone. Align viva answers with `java-best-practices.md` (String usage, accessors, no silent catches). |
| **Defending the Solution** | **Prep** | See viva talking points at end of this file. |
| **Meeting the Deadline** | **You own** | Daily GitLab commits; submit folder with docs + final code by EOD 25-11-2026. |

## Core assignment requirements (training-assignment.md)

| Requirement | Status |
|-------------|--------|
| Spring Boot REST backend | Met |
| Angular contracts UI + search UI | Met |
| Search: check-in, nights, rooms×adults | Met |
| Results: hotel, room type, marked-up price, Available/Not available | Met |
| Stay inside contract dates | Met |
| Price = rate × markup × nights × adults | Met |
| Preferred: search contracts UI | Met (contracts page search box) |
| UI stays responsive while loading | Met (disabled buttons, loading text) |
| MySQL / non-commercial DB | Met (+ Docker Compose) |

## Extra (beyond brief — defend as enhancements)

- Roles AGENT / MANAGER / ADMIN, JWT auth, signup  
- Bookings with guest NIC + **room numbers** + double-book guard  
- Pagination, sort/filter, Docker DB, seed data  

## Viva — defend in one minute each

1. **Why markup on contract?** Paper contracts carry hotelier rate; Sun Travels sells at rate×markup.  
2. **Why room numbers?** Same type has N physical rooms; prevent two agents selling the same unit for overlapping nights.  
3. **Are transactions atomic?** `@Transactional` + `PESSIMISTIC_WRITE` on room type: lock → overlap check → insert → commit/rollback.  
4. **How would “15% → 20% markup” change?** Update contract markup field; search/booking already multiply `contract.getMarkup()`.  
5. **How would multi-room booking in one reservation change?** New aggregate Booking with lines, or multiple Booking rows sharing a `reservationId`; price sum already exists in search.  
6. **Data volume?** Indexes on contract dates / booking room_type+status; paginated lists; search scans covering contracts (acceptable for agency scale; cache later if needed).
