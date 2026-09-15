# Smart Parking

Web app for managing car, bike, EV, and overflow parking slots. Users book (including bulk and pre-book), staff handle entry/exit, and admins see utilization.

## Architecture

```text
Angular 18 (Material)  --HTTP/JWT-->  Quarkus 3 (Java 21)
                                      |
                                   PostgreSQL
```

- Backend: REST + Hibernate Panache + Flyway + JWT + OpenAPI + health (`/q/health`)
- Frontend: standalone Angular, JWT interceptor, role guards, visual slot layout
- Local stack: Docker Compose (Postgres + API + nginx UI)

## Prerequisites

- Docker Desktop (recommended), or Java 21 + Maven + Node 20 + PostgreSQL 16
- Git and GitHub CLI (`gh`) if you push the repo

## Environment

Copy `.env.example` to `.env` and change passwords before any cloud deploy.

| Variable | Purpose |
|---|---|
| `POSTGRES_*` | Database name and credentials |
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | JDBC used by the backend container |
| `JWT_ISSUER` | Must match the JWT issuer claim |
| `FRONTEND_ORIGIN` | CORS origin |

Demo JWT keys live in `backend/src/main/resources/jwt/`. Replace them in production.

## Run with Docker (database included)

Postgres is started by Compose. You do not need a cloud database for local work.

```bash
copy .env.example .env
docker compose up --build
```

- UI: http://localhost:8088
- API / Swagger: http://localhost:8080/swagger
- Health: http://localhost:8080/q/health

## Run without Docker (dev)

1. Start Postgres (Compose service only is enough): `docker compose up postgres`
2. Backend: `cd backend && mvn quarkus:dev`
3. Frontend: `cd frontend && npm install && npm start` → http://localhost:4200 (proxies `/api` to 8080)

## Demo users (seeded on empty database)

| Role | Email | Password |
|---|---|---|
| Admin | admin@smartparking.local | Admin@123 |
| Staff | staff@smartparking.local | Staff@123 |
| Customer | customer@smartparking.local | Customer@123 |

## API

Swagger UI documents every resource. Auth: `POST /api/auth/login` then `Authorization: Bearer <token>`.

## Tests

```bash
cd backend && mvn test
cd frontend && npm test
```

Frontend unit tests use Chrome Headless. CI builds the Angular app; Karma is optional locally.

## Free-tier hosting (optional)

| Piece | Suggested free option | Why |
|---|---|---|
| Source / CI | GitHub + Actions | Already in this repo |
| Database | Neon or Supabase Postgres | Free Postgres; set `DB_URL` |
| Frontend | Cloudflare Pages | Static Angular build |
| Backend | Render / Fly.io free JVM instance | Needs always-on Java; spins down on free tiers |
| Monitoring | Quarkus `/q/health` and `/q/metrics` | No extra paid APM |

Never commit real secrets. Use GitHub Actions secrets for deploy.
