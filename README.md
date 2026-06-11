# World Cup 2026

Monorepo with three services:

| Folder | Stack | Port |
|--------|-------|------|
| `world-cup-web` | Next.js | 3000 |
| `world-cup-server` | Spring Boot | 8083 |
| `world-cup-agents` | FastAPI + LangGraph | 8000 |

## Local setup

### 1. Backend (`world-cup-server`)

```bash
# Copy secrets template → local file (gitignored)
cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
# Edit application-local.properties with your DB + API tokens

./mvnw spring-boot:run
```

Requires Postgres, Redis, RabbitMQ (see `application.properties` defaults).

### 2. Agents (`world-cup-agents`)

```bash
cp .env.example .env
# Edit .env — OPENAI_API_KEY, BACKEND_API_URL

pip install -r requirements.txt   # or your venv workflow
uvicorn main:app --reload
```

### 3. Frontend (`world-cup-web`)

```bash
cp .env.example .env.local   # optional for local overrides

npm install
npm run dev
```

Open http://localhost:3000

## Secrets

- **Never commit** `application-local.properties`, `.env`, `.env.local`
- **Safe to commit** `application.properties`, `.env.example`, `application-local.properties.example`
- On deploy: set the same variable names in your cloud host's environment settings

## Deploy (later)

Each folder becomes its own Docker service from this single repo.
