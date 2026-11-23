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
| Port | `3306` |
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

If port `3306` is already used on the machine, change the mapping in `docker-compose.yml` to `"3307:3306"` and update `application.properties` to `localhost:3307`.

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
