# Sun Travels ERP

Hotel contract management and call-center room search.

## Roles

| Role | Access |
|------|--------|
| **AGENT** | View contracts, room search |
| **MANAGER** | Create/edit/delete contracts + search |
| **ADMIN** | Users + everything |

## Run

1. MySQL: run `database/init.sql` once (Workbench as root)
2. API: `cd backend && ./mvnw spring-boot:run`
3. UI: `cd frontend && npm start`

- UI http://localhost:4200
- API http://localhost:8080

## Demo users (strong passwords)

| User | Password | Role |
|------|----------|------|
| admin | Admin@12345 | ADMIN |
| manager | Manager@12345 | MANAGER |
| agent | Agent@12345 | AGENT |

Public **Sign up** creates an AGENT account (strong password required).

## Pricing

`price = hotelPrice × markup × nights × adults` (markup `1.15` = 15%)
