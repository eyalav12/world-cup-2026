import "server-only";

export type AgentChatMessage = {
  role: "user" | "assistant";
  content: string;
};

export type AgentChatResult = {
  answer: string;
  messages: AgentChatMessage[];
};

const THREAD_COOKIE = "wc-agent-thread-id";
const AGENT_TIMEOUT_MS = 120_000;

function agentBase(): string {
  return process.env.AGENT_API_URL ?? "http://localhost:8000";
}

function normalizeMessages(raw: unknown): AgentChatMessage[] {
  if (!Array.isArray(raw)) return [];
  return raw.filter(
    (m): m is AgentChatMessage =>
      !!m &&
      typeof m === "object" &&
      (m.role === "user" || m.role === "assistant") &&
      typeof m.content === "string" &&
      m.content.trim().length > 0,
  );
}

export async function getThreadId(): Promise<string | null> {
  const { cookies } = await import("next/headers");
  const jar = await cookies();
  return jar.get(THREAD_COOKIE)?.value ?? null;
}

export async function ensureThreadId(): Promise<string> {
  const { cookies } = await import("next/headers");
  const jar = await cookies();
  const existing = jar.get(THREAD_COOKIE)?.value;
  if (existing) return existing;

  const id = crypto.randomUUID();
  jar.set(THREAD_COOKIE, id, {
    httpOnly: true,
    sameSite: "lax",
    path: "/",
    maxAge: 60 * 60 * 24 * 30,
  });
  return id;
}

export async function fetchThreadMessages(
  threadId: string,
): Promise<AgentChatMessage[]> {
  const res = await fetch(
    `${agentBase()}/agent/chat/${encodeURIComponent(threadId)}/messages`,
    {
      headers: { Accept: "application/json" },
      cache: "no-store",
    },
  );

  if (!res.ok) return [];

  const body = (await res.json()) as { messages?: unknown };
  return normalizeMessages(body.messages);
}

export async function invokeAgentChat(
  threadId: string,
  userPrompt: string,
): Promise<AgentChatResult> {
  const res = await fetch(`${agentBase()}/agent/chat`, {
    method: "POST",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ user_prompt: userPrompt, thread_id: threadId }),
    cache: "no-store",
    signal: AbortSignal.timeout(AGENT_TIMEOUT_MS),
  });

  const raw = await res.text();
  if (!res.ok) {
    let message = raw || res.statusText;
    try {
      const err = JSON.parse(raw) as { detail?: string | unknown; message?: string };
      if (typeof err.detail === "string") message = err.detail;
      else if (Array.isArray(err.detail)) message = JSON.stringify(err.detail);
      else if (err.message) message = err.message;
    } catch {
      /* use raw */
    }
    throw new Error(message);
  }

  if (!raw.trim()) {
    return { answer: "", messages: [] };
  }

  const body = JSON.parse(raw) as {
    answer?: string;
    messages?: unknown;
  };
  return {
    answer: body.answer ?? "",
    messages: normalizeMessages(body.messages),
  };
}
