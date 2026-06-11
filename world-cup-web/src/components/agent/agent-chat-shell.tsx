"use client";

import { useActionState } from "react";
import {
  sendAgentMessage,
  type AgentFormState,
} from "@/app/agent/actions";
import type { AgentChatMessage } from "@/lib/agent/server";
import { AgentMessageList } from "@/components/agent/agent-message-list";

const SUGGESTIONS = [
  "Who is favored in Brazil vs France?",
  "Show head-to-head for Argentina and Germany",
  "Latest World Cup news",
  "Group A standings",
];

type Props = {
  messages: AgentChatMessage[];
};

export function AgentChatShell({ messages }: Props) {
  const [state, formAction, isPending] = useActionState<
    AgentFormState | null,
    FormData
  >(sendAgentMessage, null);

  return (
    <div className="flex h-[min(70vh,640px)] flex-col rounded-2xl border border-white/10 bg-[#050f0a] shadow-2xl">
      <AgentMessageList messages={messages} pending={isPending} />

      <div className="border-t border-white/10 p-3">
        <div className="mb-2 flex flex-wrap gap-2">
          {SUGGESTIONS.map((s) => (
            <form key={s} action={formAction}>
              <input type="hidden" name="prompt" value={s} />
              <button
                type="submit"
                disabled={isPending}
                className="rounded-full border border-white/10 px-3 py-1 text-xs text-emerald-100/80 hover:bg-white/10 disabled:cursor-not-allowed disabled:opacity-50"
              >
                {s}
              </button>
            </form>
          ))}
        </div>

        {state?.error ? (
          <div className="mb-2 rounded-lg bg-red-500/15 px-3 py-2 text-sm text-red-100">
            Could not get a response: {state.error}
          </div>
        ) : null}

        <form action={formAction} className="flex gap-2">
          <input
            name="prompt"
            required
            disabled={isPending}
            placeholder="Ask about World Cup 2026…"
            className="flex-1 rounded-xl border border-white/15 bg-black/40 px-4 py-2.5 font-mono text-sm text-white outline-none focus:border-emerald-500/50 disabled:opacity-50"
          />
          <button
            type="submit"
            disabled={isPending}
            className="rounded-xl bg-emerald-500 px-5 py-2.5 text-sm font-semibold text-[#0b1f14] hover:bg-emerald-400 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {isPending ? "…" : "Send"}
          </button>
        </form>
      </div>
    </div>
  );
}
