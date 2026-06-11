# World Cup 2026 Hub (Frontend)

Production-oriented Next.js App Router UI for the Spring Boot API in `../demo`.

## Stack

- **Next.js 16** (App Router)
- **React 19**
- **Tailwind CSS 4**
- **TanStack Query** (client-side data)
- TypeScript throughout

## Quick start

1. Start backend infrastructure and API (from `../demo`):

   ```bash
   docker compose up -d
   ./mvnw spring-boot:run
   ```

   API: `http://localhost:8083`

2. Frontend:

   ```bash
   cp .env.example .env.local
   npm install
   npm run dev
   ```

   Open `http://localhost:3000`

3. Agent service (for `/agent` chat):

   ```bash
   cd ../world-cup-agents   # or your path to world-cup-agents
   uvicorn app:app --port 8000
   ```

   Requires Spring on `:8083` (agent tools call the backend).

## API routing

- **Browser / client components:** `/api/backend/*` → proxied to Spring Boot (`next.config.ts` rewrites).
- **Server components (SSR):** direct to `API_BASE_URL` (default `http://localhost:8083`).
- **Agent chat:** `POST /api/agent/chat` → Next.js route handler proxies to FastAPI (`http://localhost:8000/agent/chat`, 2 min timeout). Set `AGENT_API_URL` if the agent runs elsewhere.

## Pages (phase 1)

| Route | Backend |
|-------|---------|
| `/` | Upcoming matches, groups overview |
| `/matches` | `GET /matches/byDate` |
| `/matches/[id]` | Odds + head-to-head |
| `/teams`, `/teams/[name]` | Groups, squad, team matches, history, `GET /news/byTeamName` |
| `/groups` | Teams + `GET /matches/byGroupName` |
| `/odds` | `GET /odds/oddsSummaryByMatchId` |
| `/leaderboard` | `GET /leaderBoard/global` |
| `/news` | `GET /news/generalNews` |
| `/agent` | Chat UI → `POST /api/agent/chat` (FastAPI agent on `:8000`) |

## Deploy

```bash
npm run build
npm start
```

Set `API_BASE_URL` to your deployed Spring URL. For Vercel, add the same env var and ensure the backend allows your production origin in CORS.

## Backend gaps to close (recommended)

See parent project notes: `GET /teams/all` with `teamId` for crests, `GET /matches/{id}`, auth + user bets, agent chat endpoint.
