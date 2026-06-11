import { NextResponse } from "next/server";

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
