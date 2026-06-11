"use client";

import { useState } from "react";
import type { CleanedTournamentOdds } from "@/lib/api/types";
import { TournamentWinnerPanel } from "@/components/predictions/tournament-winner-panel";
import { GroupWinnersExplorer } from "@/components/predictions/group-winners-explorer";
import { AdvancementExplorer } from "@/components/predictions/advancement-explorer";
import { TopScorerPanel } from "@/components/predictions/top-scorer-panel";
import { EmptyState } from "@/components/ui/empty-state";
import { cn } from "@/lib/utils";

const TABS = [
  { id: "featured", label: "Featured" },
  { id: "winner", label: "Winner" },
  { id: "scorer", label: "Top scorer" },
  { id: "advancement", label: "Advancement" },
  { id: "groups", label: "Groups" },
] as const;

type TabId = (typeof TABS)[number]["id"];

export function MarketsExplorer({
  winnerOdds,
  topScorerOdds,
  groups,
}: {
  winnerOdds: CleanedTournamentOdds | null;
  topScorerOdds: CleanedTournamentOdds | null;
  groups: string[];
}) {
  const [tab, setTab] = useState<TabId>("featured");

  return (
    <div>
      <div className="mb-6 flex flex-wrap gap-2">
        {TABS.map((t) => (
          <button
            key={t.id}
            type="button"
            onClick={() => setTab(t.id)}
            className={cn(
              "rounded-full px-4 py-1.5 text-sm font-medium transition-colors",
              tab === t.id
                ? "bg-amber-500/90 text-[#0b1f14]"
                : "border border-white/15 text-emerald-100/80 hover:bg-white/10",
            )}
          >
            {t.label}
          </button>
        ))}
      </div>

      {tab === "featured" ? (
        <div className="space-y-8">
          <TournamentWinnerPanel data={winnerOdds} limit={8} />
          <TopScorerPanel data={topScorerOdds} />
          {!winnerOdds && !topScorerOdds?.markets?.length ? (
            <EmptyState
              title="Markets loading"
              description="More prediction categories will appear here as they are synced."
            />
          ) : null}
        </div>
      ) : null}

      {tab === "winner" ? <TournamentWinnerPanel data={winnerOdds} /> : null}

      {tab === "scorer" ? <TopScorerPanel data={topScorerOdds} /> : null}

      {tab === "groups" ? <GroupWinnersExplorer groups={groups} /> : null}

      {tab === "advancement" ? <AdvancementExplorer /> : null}
    </div>
  );
}
