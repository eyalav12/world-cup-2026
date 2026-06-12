# World Cup 2026 — AI Multi-Agent Tournament Hub

A full-stack FIFA World Cup 2026 hub with a **multi-agent AI assistant** — fixtures, teams, standings, news, odds, and natural-language chat that routes questions to specialist agents and combines live tournament data into one answer.

## Live site

http://165.232.81.63:3000/

## What it does

**Matches & tournament**
- Browse fixtures by date, group, and team
- Match detail pages with lineups, head-to-head history, and odds summaries
- Live / upcoming / finished match status
- Recent finished results as the tournament progresses

**Teams & groups**
- All 48 teams with squads and crests
- Group explorer with standings tables (points, form, goal difference)
- Per-team pages: upcoming fixtures, World Cup history, recent form from external APIs, tournament results, related ESPN news, and Polymarket props where available

**News**
- General World Cup headlines and team-specific ESPN news (cached and synced on a schedule)

**Odds & markets**
- Match odds from sportsbooks
- Polymarket integration: tournament winner, top scorer, group winners, advancement stages, team props

**Leaderboard**
- User prediction / betting leaderboard

**AI assistant**
- Chat interface powered by a multi-agent system
- Routes questions to specialist agents for matches, news, and odds
- Supports follow-up conversation (e.g. “matches on June 11 + news + prediction”)
- Combines specialist data into a single answer

## Architecture

Single monorepo, three independently deployable services:

| Service | Role |
|---------|------|
| `world-cup-web` | Web UI — Next.js app users interact with |
| `world-cup-server` | REST API, schedulers, caching, external data sync |
| `world-cup-agents` | LLM orchestration — LangGraph router + LangChain tool agents |

The frontend talks to Spring Boot for tournament data and to the agent service for chat. Agents call back into the backend APIs via tools.

## Tech stack

### Frontend — `world-cup-web`
- **Next.js 16** (App Router), **React 19**, **TypeScript**
- **Tailwind CSS 4**
- **TanStack Query** for client-side data fetching
- Server components, API route proxies, server actions for the agent chat

### Backend — `world-cup-server`
- **Java 17**, **Spring Boot 4**
- **Spring Data JPA** + **PostgreSQL**
- **Redis** — match/news/odds caching
- **RabbitMQ** — async messaging (finished games, match updates, notifications pipeline)
- Scheduled jobs for standings, matches, news (ESPN, NewsAPI), and odds (Football Data API, The Odds API, Polymarket)

### AI agents — `world-cup-agents`
- **Python 3.11+**, **FastAPI**, **Uvicorn**
- **LangChain** — tool-calling specialist workers (matches, news, bets)
- **LangGraph** — multi-agent router → matches / news / odds workers → synthesizer
- **OpenAI** (GPT-4.1) for routing and answer synthesis
- Session memory via LangGraph checkpointer (`thread_id`)

### External data sources
- Football Data API — fixtures, standings, team form
- ESPN — World Cup news
- The Odds API — sportsbook lines
- Polymarket — prediction market odds
- Historical World Cup match data (PostgreSQL)

## Docker

The app is built and run with **Docker** throughout development and deployment. Each service has its own `Dockerfile`; the repo root includes **Docker Compose** files to run the full stack locally (`docker-compose.yml`) and on the VPS (`docker-compose.prod.yml`).

## Repository layout

```
world-cup-2026/
├── docker-compose.yml           # Local stack
├── docker-compose.prod.yml      # Production / VPS stack
├── world-cup-web/               # Next.js frontend (+ Dockerfile)
├── world-cup-server/            # Spring Boot API (+ Dockerfile)
└── world-cup-agents/            # FastAPI + LangGraph agents (+ Dockerfile)
```

Configuration secrets (API keys, DB passwords) live in local env files — see `.env.example` and `application-local.properties.example` in each service. Those real files are gitignored and are not part of the repo.
