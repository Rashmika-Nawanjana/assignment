# User Stories — Sun Travels ERP

Format: As a … I want … so that …

---

## Authentication & roles

### US-01 Login
**As a** Sun Travels staff member  
**I want** to sign in with username and password  
**So that** I only access features allowed for my role  

**Acceptance**
- Valid credentials return JWT and land in ERP shell  
- Invalid credentials show a clear error  
- Empty fields prompt “fill required fields”

### US-02 Signup (agent)
**As a** new call-center agent  
**I want** to self-register with a strong password  
**So that** I can search and book without admin creating my account first  

**Acceptance**
- Creates AGENT role only  
- Rejects weak passwords with policy hint  
- Confirm password must match  

### US-03 Role-based navigation
**As an** ADMIN / MANAGER / AGENT  
**I want** the sidebar to show only my modules  
**So that** I am not confused by actions I cannot perform  

---

## Contracts

### US-04 Load contract
**As a** MANAGER  
**I want** to enter hotel name, validity, markup, and room types  
**So that** paper contracts become searchable inventory  

**Acceptance**
- Markup default 1.15  
- At least one room type (name, price/person/night, no. of rooms, max adults)  
- Validation messages if required fields missing  

### US-05 View / search contracts
**As an** AGENT or MANAGER  
**I want** to list and search contracts by hotel name  
**So that** I can verify what is loaded  

### US-06 Edit / delete contract
**As a** MANAGER  
**I want** to update or delete a contract  
**So that** corrections to paper terms are reflected  

---

## Search (core assignment)

### US-07 Room search
**As an** AGENT  
**I want** to enter check-in date, nights, and rooms with adults  
**So that** I see matching hotels with customer prices  

**Acceptance**
- Results show hotel, room type, marked-up price, Available / Not available  
- Price = hotelier rate × markup × nights × adults (summed per requested room)  
- Stay must fall fully inside contract validity  
- Available only if free rooms ≥ requested rooms and adults ≤ max adults  

### US-08 Search UX
**As an** AGENT  
**I want** sort, filter, and pagination on results  
**So that** large result sets stay usable  

---

## Bookings

### US-09 Book a room
**As an** AGENT  
**I want** to pick an available room number and enter guest details  
**So that** the stay is reserved for the customer  

**Acceptance**
- Required: room number, guest name, NIC, adults  
- Optional: phone, email  
- Same room number cannot be double-booked for overlapping dates  

### US-10 View / cancel bookings
**As** staff  
**I want** to list bookings and cancel confirmed ones  
**So that** inventory is freed when plans change  

---

## Admin & profile

### US-11 Manage users
**As an** ADMIN  
**I want** to create/edit/disable users and assign roles  
**So that** access control stays accurate  

### US-12 Profile
**As a** logged-in user  
**I want** to update my name/email and change password  
**So that** my account stays current  

---

## Non-functional

### US-13 Responsive UI during I/O
**As a** user  
**I want** loading indicators and non-blocking UI while search/save runs  
**So that** the screen does not freeze  

### US-14 Dockerized database
**As a** developer on a new machine  
**I want** `docker compose up -d` to create MySQL + schema  
**So that** setup is repeatable  

---

## Out of scope (explicit)

- Online payment gateway  
- Customer self-service portal  
- Channel manager / OTA sync  
