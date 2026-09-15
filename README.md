# Smart Parking

Web app for cars, bikes, EVs, and overflow parking. Customers book or pre-book slots, staff run the gate, and admins manage the layout and see utilization.

## 1. System design

```text
Browser (Angular 18 + Material)
        |  JWT Bearer
        v
Quarkus REST  (resources -> services -> Panache entities)
        |
        v
PostgreSQL (Flyway V1__init.sql)
```

| Layer | Why |
|---|---|
| JWT + roles | Admin / staff / customer without session servers |
| DTOs only | Entities stay off the wire |
| Flyway | Same schema locally and in cloud Postgres |
| Compose | One command for UI + API + database |

**Folder structure**

```text
backend/src/main/java/com/smartparking/{resource,service,domain,dto,security,exception}
frontend/src/app/{pages,services,guards,interceptors,models}
bruno/                         Bruno collection
.github/workflows/ci.yml
docker-compose.yml
render.yaml                    Free-tier backend blueprint
```

**Schema (many-to-many users↔roles; vehicles/bookings/transactions many-to-one)**

- `users`, `roles`, `user_roles`
- `vehicles` (owner, plate, type)
- `parking_slots` (number, area, floor, type, status)
- `parking_bookings` (window, status, optional `bulk_group_id`)
- `parking_transactions` (entry, exit, duration)

**HTTP surface** (Swagger at `/swagger`): `/api/auth`, `/api/vehicles`, `/api/parking-slots`, `/api/bookings` (+ `/bulk`, `/cancel`), `/api/parking/entry|exit`, `/api/dashboard/statistics`.

**Free-tier deploy**

| Piece | Choice | Why |
|---|---|---|
| Git + CI | GitHub Actions | Free for public/private hobby repos |
| Database | Neon or Supabase Postgres | Free cloud Postgres; same JDBC as local |
| API | Render Docker web service | JVM without paying for a cluster; sleeps on free tier |
| UI | Cloudflare Pages | Static Angular build, generous free tier |
| Docs / health | Quarkus Swagger + `/q/health` + `/q/metrics` | No paid APM |

## 2. Stack

Java 21, Quarkus 3.17, Hibernate Panache, Flyway, SmallRye JWT, Angular 18, Material, Chart.js, Postgres 16, Docker Compose, GitHub Actions.

## 3. Prerequisites

Docker Desktop (recommended), or Java 21 + Maven + Node 20 + PostgreSQL 16.

## 4. Environment

Copy `.env.example` to `.env`. Never commit real passwords. Demo JWT PEMs are in `backend/src/main/resources/jwt/` — replace them before production.

## 5. Run with Docker (includes Postgres)

```bash
copy .env.example .env
docker compose up --build
```

- UI: http://localhost:8088
- Swagger: http://localhost:8080/swagger
- Health: http://localhost:8080/q/health/live

## 6. Local Postgres + live reload

```bash
docker compose up postgres
cd backend && mvn quarkus:dev
cd frontend && npm install && npm start
```

UI is http://localhost:4200 (`proxy.conf.json` forwards `/api` to 8080).

## 7. Demo users (empty database is seeded)

| Role | Email | Password |
|---|---|---|
| Admin | admin@smartparking.local | Admin@123 |
| Staff | staff@smartparking.local | Staff@123 |
| Customer | customer@smartparking.local | Customer@123 |

## 8. API testing

Use Swagger Authorize with a login token, or the Bruno collection in `bruno/` (`environments/local.bru`).

## 9. Tests

```bash
cd backend && mvn test
cd frontend && npm test
```

CI runs backend tests, Angular build + Karma, then `docker compose build`. On `main`, it POSTs `RENDER_DEPLOY_HOOK` if that GitHub secret is set.

## 10. Make it public (free) — no local cluster

You do **not** need Kubernetes, a local hostname, or port-forwarding. Local Docker stays on your PC. For anyone on the internet:

1. **Code** — this GitHub repo (public).
2. **Database** — [Neon](https://neon.tech) free Postgres. Copy host, user, password, database. JDBC looks like `jdbc:postgresql://ep-xxx.region.aws.neon.tech/neondb?sslmode=require`.
3. **API** — [Render](https://render.com) free Web Service from this repo (`backend/Dockerfile`). Env: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_ISSUER=smart-parking`, `FRONTEND_ORIGIN=https://subashgoud123.github.io`. Copy the service URL, e.g. `https://smart-parking-api.onrender.com`.
4. **UI** — GitHub Pages (workflow `.github/workflows/pages.yml`). In the repo: **Settings → Secrets → Actions** add `API_BASE_URL` = that Render origin (no trailing slash). Then **Settings → Pages** → source branch `gh-pages`. Public URL: https://subashgoud123.github.io/smart-parking/

The first Render request after idle can take ~50s (free tier sleeps). Demo logins are the same as local.

Do not expose `localhost:8088` with a homemade hostname; it only works while your laptop is on.

How to try a flow: login as customer → Layout → filter vacant cars → click a green slot → Book. Staff: Gate for entry/exit. Admin: Dashboard charts and Slots CRUD.
