import { AgentChatShell } from "@/components/agent/agent-chat-shell";
import { Card, CardTitle } from "@/components/ui/card";
import { fetchThreadMessages, getThreadId } from "@/lib/agent/server";

export const metadata = { title: "Agent" };

/** Agent tool loops can run 30–90s. */
export const maxDuration = 120;

export default async function AgentPage() {
  const threadId = await getThreadId();
  const messages = threadId ? await fetchThreadMessages(threadId) : [];

  return (
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
      <h1 className="text-3xl font-bold text-white">AI assistant</h1>
      <p className="mt-2 max-w-2xl text-emerald-100/70">
        Ask about fixtures, teams, standings, history, news, and lineups — your
        personal World Cup analyst. Responses may take 10–30 seconds while the
        agent looks up data.
      </p>

      <div className="mt-8">
        <AgentChatShell messages={messages} />
      </div>

      <div className="mt-8 grid gap-4 sm:grid-cols-2">
        <Card>
          <CardTitle className="mb-2 text-base">Match insights</CardTitle>
          <p className="text-sm text-emerald-100/60">
            Fixtures, live scores, group tables, and head-to-head records.
          </p>
        </Card>
        <Card>
          <CardTitle className="mb-2 text-base">Betting help</CardTitle>
          <p className="text-sm text-emerald-100/60">
            Odds summaries and guidance on upcoming matches.
          </p>
        </Card>
        <Card>
          <CardTitle className="mb-2 text-base">News & updates</CardTitle>
          <p className="text-sm text-emerald-100/60">
            Tournament headlines and team news as they break.
          </p>
        </Card>
        <Card>
          <CardTitle className="mb-2 text-base">Your predictions</CardTitle>
          <p className="text-sm text-emerald-100/60">
            Place and track bets once you sign in.
          </p>
        </Card>
      </div>
    </div>
  );
}
