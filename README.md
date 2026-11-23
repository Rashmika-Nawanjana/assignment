# Sun Travels ERP

Hotel contract management and call-center room search (Spring Boot + Angular + MySQL).

## Roles

| Role | Access |
|------|--------|
| **AGENT** | View contracts, room search |
| **MANAGER** | Create/edit/delete contracts + search |
| **ADMIN** | Users + everything |

## Quick start (new machine)

### 1. Database (Docker)

Install Docker Desktop, then from the project root:

```bash
docker compose up -d
```

This starts MySQL 8, creates DB/user `suntravels` / `suntravels`, and runs `database/schema.sql`.

Details: [database/README.md](database/README.md)

### 2. Backend

```bash
cd backend
./mvnw spring-boot:run
```

On first start the API seeds demo users and optional dummy hotels/bookings.

### 3. Frontend

```bash
cd frontend
npm install
npm start
```

- UI http://localhost:4200  
- API http://localhost:8080  

## Demo users

| User | Password | Role |
|------|----------|------|
| admin | Admin@12345 | ADMIN |
| manager | Manager@12345 | MANAGER |
| agent | Agent@12345 | AGENT |

Public **Sign up** creates an AGENT account (strong password required).

## Pricing

`price = hotelPrice × markup × nights × adults` (markup `1.15` = 15%)

## Without Docker

Use a local MySQL 8 instance and run `database/init.sql` (see [database/README.md](database/README.md)). `application.properties` already points at `localhost:3306`.
