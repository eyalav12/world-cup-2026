import type { AgentChatMessage } from "@/lib/agent/server";
import { cn } from "@/lib/utils";

const WELCOME =
  "Welcome! Ask about World Cup 2026 — fixtures, group standings, head-to-head history, news, and lineups.";

type Props = {
  messages: AgentChatMessage[];
  pending?: boolean;
};

export function AgentMessageList({ messages, pending = false }: Props) {
  return (
    <div className="flex-1 overflow-y-auto p-4 font-mono text-sm">
      <div className="mb-3 max-w-[90%] rounded-lg bg-amber-500/10 px-3 py-2 text-amber-100/90">
        <span className="mr-2 text-xs uppercase opacity-50">system</span>
        {WELCOME}
      </div>

      {messages.map((m, i) => (
        <div
          key={`${m.role}-${i}-${m.content.slice(0, 32)}`}
          className={cn(
            "mb-3 max-w-[90%] rounded-lg px-3 py-2",
            m.role === "user" && "ml-auto bg-emerald-600/30 text-emerald-50",
            m.role === "assistant" && "bg-white/10 text-emerald-100",
          )}
        >
          <span className="mr-2 text-xs uppercase opacity-50">{m.role}</span>
          {m.content}
        </div>
      ))}

      {pending ? (
        <div className="mb-3 max-w-[90%] animate-pulse rounded-lg bg-white/10 px-3 py-2 text-emerald-100 opacity-70">
          <span className="mr-2 text-xs uppercase opacity-50">assistant</span>
          Thinking…
        </div>
      ) : null}
    </div>
  );
}
