import { NextResponse } from "next/server";

/** Agent tool loops can run 30–90s; avoid default proxy timeouts. */
export const maxDuration = 120;

const AGENT_TIMEOUT_MS = 120_000;
const agentBase = () => process.env.AGENT_API_URL ?? "http://localhost:8000";

type AgentChatMessage = { role: "user" | "assistant"; content: string };

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

export async function GET(
  _request: Request,
  context: { params: Promise<{ threadId: string }> },
) {
  const { threadId } = await context.params;
  if (!threadId?.trim()) {
    return NextResponse.json({ message: "thread_id is required" }, { status: 400 });
  }

  try {
    const res = await fetch(
      `${agentBase()}/agent/chat/${encodeURIComponent(threadId)}/messages`,
      { headers: { Accept: "application/json" } },
    );
    const raw = await res.text();

    if (!res.ok) {
      return NextResponse.json(
        { message: raw || res.statusText },
        { status: res.status },
      );
    }

    const parsed = raw.trim() ? (JSON.parse(raw) as { messages?: unknown }) : {};
    return NextResponse.json({ messages: normalizeMessages(parsed.messages) });
  } catch (error) {
    const message =
      error instanceof Error ? error.message : "Failed to reach agent service";
    return NextResponse.json({ message }, { status: 502 });
  }
}

export async function POST(request: Request) {
  let body: { user_prompt?: string; thread_id?: string };
  try {
    body = (await request.json()) as {
      user_prompt?: string;
      thread_id?: string;
    };
  } catch {
    return NextResponse.json({ message: "Invalid JSON body" }, { status: 400 });
  }

  const userPrompt = body.user_prompt?.trim();
  if (!userPrompt) {
    return NextResponse.json(
      { message: "user_prompt is required" },
      { status: 400 },
    );
  }

  const threadId = body.thread_id?.trim();
  if (!threadId) {
    return NextResponse.json(
      { message: "thread_id is required" },
      { status: 400 },
    );
  }

  const url = `${agentBase()}/agent/chat`;

  try {
    const res = await fetch(url, {
      method: "POST",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ user_prompt: userPrompt, thread_id: threadId }),
      signal: AbortSignal.timeout(AGENT_TIMEOUT_MS),
    });

    const raw = await res.text();

    if (!res.ok) {
      let message = raw || res.statusText;
      try {
        const err = JSON.parse(raw) as { detail?: string; message?: string };
        if (err.detail) message = String(err.detail);
        else if (err.message) message = err.message;
      } catch {
        /* use raw text */
      }
      return NextResponse.json({ message }, { status: res.status });
    }

    if (!raw.trim()) {
      return NextResponse.json({ answer: "", messages: [] });
    }

    try {
      const parsed = JSON.parse(raw) as {
        answer?: unknown;
        messages?: unknown;
      };
      return NextResponse.json({
        answer: typeof parsed.answer === "string" ? parsed.answer : "",
        messages: normalizeMessages(parsed.messages),
      });
    } catch {
      return NextResponse.json({ answer: raw, messages: [] });
    }
  } catch (error) {
    const message =
      error instanceof Error && error.name === "TimeoutError"
        ? "Agent request timed out after 2 minutes. Try a simpler question."
        : error instanceof Error
          ? error.message
          : "Failed to reach agent service";
    return NextResponse.json({ message }, { status: 502 });
  }
}
