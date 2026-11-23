# User Manual — Sun Travels ERP

## 1. Overview

Sun Travels ERP helps staff:

1. Load hotel contracts (rates, rooms, markup)  
2. Search availability and **customer price** for a stay  
3. Book a **specific room number** with guest details  

**URLs**

| App | URL |
|-----|-----|
| UI | http://localhost:4200 |
| API | http://localhost:8080 |

## 2. Getting started (technical)

1. Start MySQL: `docker compose up -d` (from project root)  
2. Backend: `cd backend && ./mvnw spring-boot:run`  
3. Frontend: `cd frontend && npm start`  

Demo logins:

| Username | Password | Role |
|----------|----------|------|
| admin | Admin@12345 | ADMIN |
| manager | Manager@12345 | MANAGER |
| agent | Agent@12345 | AGENT |

## 3. Sign in / Sign up

1. Open the UI → Sign in  
2. Enter username and password → **Sign in**  
3. New agents: **Create an agent account** (strong password required)  

If fields are empty, the form shows which fields to fill.

## 4. Contracts (MANAGER / ADMIN)

1. Open **Contracts** in the sidebar  
2. Fill hotel name, markup (e.g. `1.15` = 15%), valid from/to  
3. Add room types: name, price per person per night, number of rooms, max adults  
4. **Save contract**  
5. Use the search box to find by hotel; **Edit** / **Delete** as needed  
6. Agents can **view** contracts but not edit  

## 5. Room search (AGENT / MANAGER / ADMIN)

1. Open **Room search**  
2. Enter **check-in date**, **nights**, and adults per room (Add room for more)  
3. Click **Find rooms**  
4. Results show hotel, room type, **total price** (with markup), availability  
5. Use filter, column sort, and pagination  

**Price formula:**  
`rate × markup × nights × adults` (summed for each requested room in the search).

**Available** means enough free physical rooms for the stay and adults fit max adults.

## 6. Booking a room

1. On an **Available** row, click **Book room** (page scrolls to the form)  
2. Choose a **Room number** from free rooms for those dates  
3. Enter guest name, NIC, adults; phone/email optional  
4. **Confirm booking**  
5. If that room was taken meanwhile, an error appears **on the booking form**  

## 7. Bookings list

1. Open **Bookings**  
2. Search/filter/sort as needed  
3. **Cancel** a confirmed booking to free the room number  

## 8. Users (ADMIN only)

Create or edit users, roles (ADMIN / MANAGER / AGENT), active flag, passwords (strong policy).

## 9. Profile

Update full name / email or change password (current + new strong password).

## 10. Troubleshooting

| Issue | What to try |
|-------|-------------|
| Cannot sign in | Check backend on :8080; use demo users |
| Port 8080 in use | Stop the other Java process; restart backend |
| No search results | Contract dates must cover the whole stay; check markup/contracts |
| Room already booked | Pick another room number from the dropdown |
| DB connection error | `docker compose ps` — MySQL should be healthy |

## 11. Roles summary

| Feature | AGENT | MANAGER | ADMIN |
|---------|-------|---------|-------|
| Search / book | ✓ | ✓ | ✓ |
| View contracts | ✓ | ✓ | ✓ |
| Edit contracts | | ✓ | ✓ |
| Manage users | | | ✓ |
