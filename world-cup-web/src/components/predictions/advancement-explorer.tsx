"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { PredictionMarketPanel } from "@/components/predictions/prediction-market-panel";
import { getAdvancementOdds } from "@/lib/api/endpoints";
import { parseSubjectFromQuestion, sortMarketsByYesPct } from "@/lib/predictions";
import { TeamCrest } from "@/components/teams/team-crest";
import { EmptyState } from "@/components/ui/empty-state";
import { cn } from "@/lib/utils";

const STAGES = [
  { id: "round-of-16", label: "Round of 16" },
  { id: "quarterfinals", label: "Quarterfinals" },
  { id: "semifinals", label: "Semifinals" },
  { id: "final", label: "Final" },
] as const;

export function AdvancementExplorer() {
  const [selected, setSelected] = useState<string>(STAGES[0].id);

  const { data, isLoading, isError } = useQuery({
    queryKey: ["advancement-odds", selected],
    queryFn: () => getAdvancementOdds(selected),
    enabled: Boolean(selected),
  });

  const sorted = data?.markets ? sortMarketsByYesPct(data.markets) : [];

  return (
    <div>
      <div className="mb-6 flex flex-wrap gap-2">
        {STAGES.map((stage) => (
          <button
            key={stage.id}
            type="button"
            onClick={() => setSelected(stage.id)}
            className={cn(
              "rounded-full px-4 py-1.5 text-sm font-medium transition-colors",
              selected === stage.id
                ? "bg-amber-500/90 text-[#0b1f14]"
                : "border border-white/15 text-emerald-100/80 hover:bg-white/10",
            )}
          >
            {stage.label}
          </button>
        ))}
      </div>

      {isLoading ? (
        <p className="text-sm text-emerald-100/60">Loading advancement markets…</p>
      ) : null}

      {isError ? (
        <EmptyState
          title="Could not load advancement markets"
          description="Check that the backend is running and the Polymarket sync has completed."
        />
      ) : null}

      {!isLoading && !isError && data ? (
        <PredictionMarketPanel
          title={data.title || "Advancement"}
          markets={sorted.slice(0, 16)}
          renderLabel={(market) => parseSubjectFromQuestion(market.question)}
          renderIcon={(market) => {
            const team = parseSubjectFromQuestion(market.question);
            return team ? <TeamCrest teamName={team} size={32} /> : null;
          }}
        />
      ) : null}

      {!isLoading && !isError && !data ? (
        <EmptyState
          title="No advancement odds yet"
          description="Markets sync on a schedule. Try again after the backend Polymarket job runs."
        />
      ) : null}
    </div>
  );
}
