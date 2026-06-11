/** Client-side: use Next rewrite proxy. Server-side: talk to Spring directly. */
export function getApiBaseUrl(): string {
  if (typeof window !== "undefined") {
    return "/api/backend";
  }
  return (
    process.env.API_BASE_URL ??
    process.env.NEXT_PUBLIC_API_BASE_URL ??
    "http://localhost:8083"
  );
}

/** Client-side: use Next rewrite proxy. Server-side: talk to FastAPI agent directly. */
export function getAgentApiBaseUrl(): string {
  if (typeof window !== "undefined") {
    return "/api/agent";
  }
  return process.env.AGENT_API_URL ?? "http://localhost:8000/agent";
}
