# Sun Travels ERP — Full Guide

> Written for someone who already knows **Express.js + React**, but is new to **Spring Boot + Angular**.  
> By the end you should be able to explain the product, the evaluation criteria, and defend the design in a viva.

---

## Table of contents

1. [The story (what we built)](#1-the-story-what-we-built)  
2. [Quick start](#2-quick-start)  
3. [Express/React → Spring/Angular map](#3-expressreact--springangular-map)  
4. [Spring Boot concepts (simple)](#4-spring-boot-concepts-simple)  
5. [Angular concepts (simple)](#5-angular-concepts-simple)  
6. [How the app works end to end](#6-how-the-app-works-end-to-end)  
7. [Auth](#7-auth)  
8. [Contracts](#8-contracts)  
9. [Search & pricing](#9-search--pricing)  
10. [Booking & double-booking fix](#10-booking--double-booking-fix)  
11. [Database & ER design](#11-database--er-design)  
12. [Why we test + what each test does](#12-why-we-test--what-each-test-does)  
13. [Evaluation criteria walkthrough](#13-evaluation-criteria-walkthrough)  
14. [Viva cheat sheet](#14-viva-cheat-sheet)  
15. [Folder map](#15-folder-map)  

---

## 1. The story (what we built)

Sun Travels is a travel agency. Hotels send **paper contracts** with room types, rates, and how many rooms they have. Sun Travels adds a **markup** (e.g. 15%) and sells those rooms through a **call center**.

Before software, agents calculated prices by hand. This system:

1. Lets managers **load contracts** into the computer  
2. Lets agents **search** “check-in + nights + rooms/adults” and see **customer price + Available/Not available**  
3. Lets agents **book** a specific **room number** for a guest  
4. Stops two agents from selling the **same physical room** for overlapping nights  

Stack:

| Layer | Tech | Your familiar equivalent |
|-------|------|---------------------------|
| API | Spring Boot (Java) | Express + Node |
| UI | Angular | React |
| DB | MySQL (Docker) | Postgres/Mongo you used before |
| Auth | JWT + Spring Security | Passport / JWT middleware |

---

## 2. Quick start

```bash
# 1) Database (MySQL in Docker — host port 3307)
docker compose up -d

# 2) Backend API  → http://localhost:8080
cd backend
./mvnw spring-boot:run

# 3) Frontend UI  → http://localhost:4200
cd frontend
npm install
npm start
```

**Demo logins**

| User | Password | Role |
|------|----------|------|
| admin | Admin@12345 | ADMIN |
| manager | Manager@12345 | MANAGER |
| agent | Agent@12345 | AGENT |

Backend JDBC URL must match Docker: `localhost:3307` (see `backend/src/main/resources/application.properties`).

**Run tests**

```bash
# Backend
cd backend && ./mvnw test

# Frontend (Edge if Chrome missing)
cd frontend
CHROME_BIN="/c/Program Files (x86)/Microsoft/Edge/Application/msedge.exe" npx ng test --no-watch --browsers=ChromeHeadless
```

More docs: `docs/` (ER, stories, manual, test plan, peer testing).

---

## 3. Express/React → Spring/Angular map

### Backend

| Express idea | Spring Boot idea |
|--------------|------------------|
| `app.listen(8080)` | `SpringApplication.run(...)` + `server.port=8080` |
| `router.get/post` | `@RestController` + `@GetMapping` / `@PostMapping` |
| `req.body` | `@RequestBody` DTO |
| Middleware | Filters (`JwtAuthenticationFilter`) + Spring Security |
| `mongoose` / Prisma models | **JPA Entities** + **Repositories** |
| Service layer you wrote by hand | `@Service` classes |
| `async/await` DB calls | Repositories; wrap writes in `@Transactional` |
| `bcrypt` | `BCryptPasswordEncoder` |
| `jsonwebtoken` | `JwtService` (jjwt library) |
| `process.env` | `application.properties` |

### Frontend

| React idea | Angular idea |
|------------|--------------|
| Component function / JSX | `@Component` class + HTML template |
| `useState` | class fields + `signal()` (this project) |
| `useEffect` | lifecycle / subscriptions |
| React Router | Angular Router (`app.routes.ts`) |
| Context / Redux for auth | `AuthService` + signals |
| `fetch` / axios | `HttpClient` |
| Axios interceptor | `authInterceptor` |
| Protected routes | `authGuard` / `roleGuard` |
| Props | `@Input()` (less used here; services shared instead) |
| CSS modules | `component.scss` next to the component |

**Mental model:** Angular is more “batteries included” (router, HTTP, DI, forms). Spring Boot is more “batteries included” than raw Express (security, JPA, transactions).

---

## 4. Spring Boot concepts (simple)

### What is Spring Boot?

A framework that starts a web server and wires your classes together. You write small annotated classes; Boot creates the HTTP server, DB connections, security, etc.

Entry point: `SunTravelsApplication.java` with `@SpringBootApplication` — like `index.js` that boots Express.

### Layers in this project

```
Controller  →  receives HTTP, returns JSON   (like Express routes)
Service     →  business rules                (like your service files)
Repository  →  talks to MySQL                (like Prisma/Mongoose)
Entity      →  Java class mapped to a table  (like a model)
DTO         →  request/response shapes       (like Zod schemas / interfaces)
```

Why layers? So pricing/booking rules live in one place, not inside controllers. Easier to unit-test services without HTTP.

### What is JPA / Hibernate?

- **JPA** = API for mapping Java objects ↔ database tables  
- **Hibernate** = the engine that implements JPA  

You annotate a class:

```java
@Entity
@Table(name = "bookings")
public class Booking { ... }
```

Hibernate turns that into SQL.  
`spring.jpa.hibernate.ddl-auto=update` means: “keep tables in sync with entities” (fine for training; production often uses migrations).

**Repository** example:

```java
public interface BookingRepository extends JpaRepository<Booking, Long> { ... }
```

Like Prisma’s `prisma.booking.findMany()` — Spring generates the implementation.

### What is `@Transactional`?

A **database transaction**: all DB work in that method commits together, or rolls back together.

Booking create is `@Transactional`:

1. Lock room type  
2. Check overlap  
3. Insert booking  

If step 2 fails, nothing is saved. That’s **atomicity**.

### What is dependency injection?

Instead of `new AuthService()` everywhere, Spring constructs objects and passes dependencies in the constructor (like NestJS). Controllers receive services automatically.

### What is Spring Security?

Middleware for authz. We configure:

- Public: `/api/auth/**`  
- Admin only: `/api/users/**`  
- Manager/Admin: contract write  
- Agents+: search & bookings  

JWT filter reads `Authorization: Bearer ...` and sets the logged-in user for that request.

---

## 5. Angular concepts (simple)

### Standalone components

This app uses **standalone** components (Angular 19 style): each component lists its own `imports` (FormsModule, RouterLink, etc.). No giant `NgModule` required.

### Templates

HTML lives in `*.component.html`. Binding:

- `{{ value }}` — show data  
- `[(ngModel)]="username"` — two-way form binding (like controlled inputs)  
- `(click)="save()"` — events  
- `@if` / `@for` — control flow (like conditional render / map)

### Signals

`signal('')` holds reactive state. Updating `error.set('...')` refreshes the template. Similar spirit to React state, but Angular tracks it differently.

### Services + `providedIn: 'root'`

`AuthService`, `BookingService` are singletons (one app-wide instance), injected into components — like a React context provider without the JSX nesting.

### Router guards

Before opening a page:

- `authGuard` — must be logged in  
- `roleGuard(['ADMIN'])` — must have role  

Like React Router loaders / protected route wrappers.

### Interceptor

Every HTTP call can attach the JWT. On **401**, logout. Same idea as axios interceptors.

### Why UI doesn’t freeze

Long API calls use Observables (RxJS). Buttons show “Searching…” / disable while waiting. Assignment rule: UI must stay responsive during load/save/search.

---

## 6. How the app works end to end

```
Browser (Angular :4200)
    │  JWT in Authorization header
    ▼
Spring Boot API (:8080)
    │  Security + Controllers + Services
    ▼
MySQL (:3307 via Docker)
```

**Happy path for an agent**

1. Login → get JWT → stored in `localStorage`  
2. Open Room search → enter dates/rooms → API `/api/search`  
3. See prices (with markup) + Available  
4. Book room → pick room number → guest NIC → `/api/bookings`  
5. View/cancel in Bookings  

**Manager** also creates contracts. **Admin** manages users.

---

## 7. Auth

### Why auth?

Different jobs need different powers. Agents search/book; managers edit contracts; admins manage users. Without auth, anyone could delete contracts.

### How it works

**Backend**

1. Login: check username/password (BCrypt) → issue JWT with username + role  
2. Signup: always creates **AGENT**; strong password policy  
3. Every protected API: JWT filter validates token → Spring checks role  

**Frontend**

1. Login form validates empty fields  
2. Store user+token  
3. Guards protect pages  
4. Interceptor sends Bearer token  

### Important viva point

Frontend guards are **UX**. Real security is **backend**. Someone can fake localStorage; they still cannot call admin APIs without a valid ADMIN JWT.

### Roles

| Role | Can do |
|------|--------|
| AGENT | Search, book, view contracts |
| MANAGER | + create/edit/delete contracts |
| ADMIN | + manage users |

---

## 8. Contracts

Paper contract → digital record:

- Hotel name  
- Valid from / to  
- Markup (e.g. `1.15` = 15%)  
- Room types: name, price per person per night, number of rooms, max adults  

**Why separate Hotel and Contract?** Same hotel can have different contracts over years without duplicating hotel identity badly.

Agents can search contracts by hotel name (preferred requirement in the brief).

---

## 9. Search & pricing

### Inputs

1. Check-in date  
2. Number of nights  
3. List of rooms, each with adults (e.g. 1 room×2 adults + 1 room×1 adult)

### Rules (assignment)

- Stay must fall **inside** contract validity  
- **Available** if free rooms ≥ rooms requested AND every room’s adults ≤ max adults  
- **Price** = `hotelierRate × markup × nights × adults`  
  - If multiple rooms in the search, sum each room’s adults line  

Example: rate `35`, markup `1.15`, 2 nights, 2 adults →  
`35 × 1.15 × 2 × 2 = 161.00`

### Why markup is on the contract

Hotelier gives net rate. Sun Travels sells higher. Changing markup from 1.15 → 1.20 does **not** need new pricing code — search already multiplies `contract.getMarkup()`.

### UI extras (beyond brief)

Sort, filter, pagination on results so seeded data (~200 rows) stays usable.

---

## 10. Booking & double-booking fix

### The problem

A room **type** (e.g. Standard) can have many physical rooms (e.g. 15).  
Two agents booking “Standard on the same night” is OK if they take **different** rooms.  
It is **not** OK if both take **Room 1**.

Earlier, without room numbers, inventory only counted “how many Standards are free,” which is correct for capacity but unclear for “same room twice.”

### The fix

1. Physical rooms are numbered `1 .. numberOfRooms`  
2. Agent **selects a free room number** when booking  
3. On create booking (one DB transaction):  
   - **Lock** the room-type row (`PESSIMISTIC_WRITE`)  
   - Count confirmed bookings for that **room number** overlapping the stay  
   - If any → reject: “Room X is already booked for these dates”  
   - Else insert booking  

### Overlap (simple)

Treat stay as `[checkIn, checkIn + nights)` (checkout exclusive).  
Two stays overlap if each starts before the other ends.

### Cancel

Cancelled bookings no longer hold the room number.

### Industry analogy

Hotels track room 101 vs 102. Soft holds + locks are common; we use lock + confirm for the training scope.

---

## 11. Database & ER design

```
UserAccount ──< Booking >── RoomType >── Contract >── Hotel
```

| Table | Purpose |
|-------|---------|
| users | Login accounts + roles |
| hotels | Hotel names |
| contracts | Validity + markup |
| room_types | Rate + inventory + max adults |
| bookings | Guest stay + room_number + price snapshot |

**Normalization:** Hotel / Contract / RoomType split cleanly.  
**Denormalization on purpose:** Booking stores `hotel_name` / `room_type_name` snapshots so history stays readable if contracts change later.

**Data volumes:** Indexes on contract dates and booking room/status; lists are paginated; seed data (~50 hotels, ~400 bookings) stresses UI.

Schema: `database/schema.sql`  
Docker: `docker compose up -d`  
ER write-up: `docs/ER-DIAGRAM.md`

---

## 12. Why we test + what each test does

### Why unit tests?

They prove **rules** without clicking the UI every time:

- Price formula still 161.00  
- Weak password rejected  
- Same room number rejected  

Panel asks: “Written unit tests for UI? for service?” → Yes.

### Why not only manual testing?

Manual finds UX bugs. Unit tests catch logic regressions after you change code. Both appear in evaluation (unit tests + test cases/plan + peer testing).

### Backend tests (`./mvnw test`)

| Test class | What it proves |
|------------|----------------|
| `SearchServiceTest` | Markup price; unavailable when adults > max; empty when no contract; unavailable when inventory filled |
| `BookingServiceTest` | Same room number overlap rejected; free room books; available-rooms list excludes taken numbers |
| `AuthServiceTest` | Login OK; bad password; disabled user; signup AGENT + encode password; duplicate username; weak password |
| `SunTravelsApplicationTests` | Context starts (smoke) |

These **mock** repositories — no real MySQL needed. Focus = business logic.

### Frontend tests (`ng test`)

| Spec | What it proves |
|------|----------------|
| `auth.guard.spec` | Logged out → `/login`; logged in → allow |
| `role.guard.spec` | Wrong role → home redirect |
| `auth.service.spec` | Login/signup persist token; roles; home routes; logout |
| `login.component.spec` | Empty fields message; success navigates; API error shown |
| `signup.component.spec` | Required fields; weak password; mismatch; success |
| `search.component.spec` | Need check-in; search stores results; filter; pagination; booking requires fields |
| `form-validation.util.spec` | Missing-field messages |
| `table-query.util.spec` | Sort / filter / toggle sort |

### Manual / peer testing docs

| Doc | Purpose |
|-----|---------|
| `docs/TEST-PLAN.md` | Entry/exit criteria, scope, severity |
| `docs/TEST-CASES.md` | Step-by-step cases (price, double book, roles…) |
| `docs/PEER-TESTING.md` | Log when you test a colleague’s system |

**You still must fill peer testing yourself** — the panel grades that process.

---

## 13. Evaluation criteria walkthrough

From `evaluation-criteria.md`, explained so you can talk naturally.

### ER Design
**What they want:** sensible tables, relationships, normalization, thought about volume.  
**What we have:** Hotel→Contract→RoomType→Booking; indexes; volume notes in ER doc.  
**Say:** “We normalized hotels and contracts; bookings snapshot names; room numbers are 1..N from inventory.”

### UI Design
**What they want:** usable UI, some novelty, UX care.  
**What we have:** ERP sidebar, branded fonts/colors, sort/filter/pagination, validation messages, confirm modal, scroll to booking form.  
**Say:** “Agents stay guided; large seed results don’t dump 200 rows without pagination.”

### Application Flow
**What they want:** start-to-end thought; errors; guidance.  
**What we have:** login → role home → contracts/search/book/bookings; empty states; booking errors next to the form.  
**Say:** “Errors tell you which field or that Room 1 is taken — not a silent disabled button.”

### Unit Tests
**What they want:** UI tests + service tests.  
**What we have:** Angular specs + Java service tests (auth/search/booking).  
**Say:** “Services cover pricing and double-book; UI tests cover guards, login/signup, search validation.”

### User Stories & Manual
**Docs:** `USER-STORIES.md`, `USER-MANUAL.md`.  
**Say:** “Stories cover auth, contracts, search rules, booking, admin; manual is how to run and use it.”

### Test Case Writing
**Docs:** `TEST-PLAN.md` (entry/exit), `TEST-CASES.md` (full scope including TC-B02 double book).  

### Peer Testing
**Action:** test a peer’s app, raise issues, use entry/exit from their/your plan. Template: `PEER-TESTING.md`.

### Code Quality
Layered Spring, constructor injection, clear service methods, Angular standalone structure. Mention CG practices (accessors, no silent catch, readable names).

### Defending the Solution
Be ready for “what if markup changes?” / “why lock?” / “why JWT on server?” — see cheat sheet below.

### Meeting the Deadline
GitLab commits + submission folder with code + finalized docs.

---

## 14. Viva cheat sheet

**Q: Explain the price.**  
A: `rate × markup × nights × adults`, summed per searched room.

**Q: Why markup?**  
A: Hotelier rate vs Sun Travels selling price; stored on contract as multiplier `1.15`.

**Q: How do you stop double booking?**  
A: Room numbers + overlap query + pessimistic lock inside `@Transactional` create.

**Q: Are transactions atomic?**  
A: Yes for booking create — lock, check, insert commit or roll back together.

**Q: Frontend vs backend auth?**  
A: Guards hide pages; SecurityConfig + JWT enforce APIs.

**Q: Slight requirement change: markup 20%.**  
A: Update contract field; no formula rewrite.

**Q: Why Spring Boot / Angular?**  
A: Assignment required them; Boot gives REST+JPA+Security; Angular gives structured SPA like React but with built-in DI/router/forms.

**Q: Why Docker MySQL?**  
A: Same DB setup on any machine: `docker compose up -d` + schema script.

**Q: What did you unit test?**  
A: Search pricing/availability; booking conflict; auth login/signup rules; Angular guards/forms/search UX logic.

---

## 15. Folder map

```
training assignment/
├── README.md                 ← this guide
├── docker-compose.yml        ← MySQL on host :3307
├── database/
│   ├── schema.sql            ← tables
│   ├── init.sql              ← local MySQL without Docker
│   └── README.md
├── docs/                     ← evaluation deliverables
│   ├── ER-DIAGRAM.md
│   ├── USER-STORIES.md
│   ├── USER-MANUAL.md
│   ├── TEST-PLAN.md
│   ├── TEST-CASES.md
│   ├── PEER-TESTING.md
│   └── EVALUATION-CHECKLIST.md
├── backend/                  ← Spring Boot API
│   └── src/main/java/.../    ← controllers, services, entities, security
│   └── src/test/java/.../    ← Auth/Search/Booking tests
└── frontend/                 ← Angular UI
    └── src/app/
        ├── core/             ← auth, guards, interceptors, services
        ├── features/         ← login, search, contracts, bookings…
        └── layout/           ← ERP shell
```

---

## Final mental picture

You already know **Express + React**. This project is the same kind of app:

- **API** with routes, services, DB, JWT  
- **SPA** with pages, forms, protected routes  

Spring Boot and Angular change the **shape** of the code (annotations, DI, templates), not the **ideas**.

Sun Travels specifically is: **load contracts → search with markup price → book numbered rooms without overlap → prove it with tests and docs for the panel.**

If you can tell that story clearly, you are ready for the viva.
