# Database setup

## Option A — Docker (recommended on a new machine)

From the project root (needs [Docker Desktop](https://www.docker.com/products/docker-desktop/)):

```bash
docker compose up -d
```

Waits until healthy, then:

| Setting | Value |
|---------|--------|
| Host | `localhost` |
| Port | `3307` (mapped from container `3306`) |
| Database | `suntravels` |
| User | `suntravels` |
| Password | `suntravels` |

- Schema loads once from `schema.sql` into the container.
- Demo users + dummy hotels/bookings are created when you start the **backend** (Java seeders).

```bash
docker compose ps
docker compose logs -f mysql
docker compose down        # stop
docker compose down -v     # stop and delete data (fresh schema next up)
```

Compose uses host port `3307` to avoid clashing with a local MySQL on `3306`. If you change the left side of `"3307:3306"`, update `backend/.../application.properties` to the same host port.

## Option B — Local MySQL (no Docker)

As root:

```bash
mysql -u root -p < database/init.sql
```

Or run `init.sql` in MySQL Workbench, then `schema.sql` if tables were not created.

## After the DB is up

```bash
cd backend && ./mvnw spring-boot:run
cd frontend && npm start
```
