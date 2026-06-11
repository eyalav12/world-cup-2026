"use client";

import { useState } from "react";
import type { CleanedMarket, CleanedTournamentOdds } from "@/lib/api/types";
import { parseTeamFromQuestion, sortMarketsByYesPct } from "@/lib/predictions";
import { PredictionMarketPanel } from "@/components/predictions/prediction-market-panel";
import { TeamCrest } from "@/components/teams/team-crest";
import { EmptyState } from "@/components/ui/empty-state";

const DEFAULT_VISIBLE = 12;

export function TournamentWinnerPanel({
  data,
  limit,
  compact = false,
}: {
  data: CleanedTournamentOdds | null;
  limit?: number;
  compact?: boolean;
}) {
  const [expanded, setExpanded] = useState(false);

  if (!data) {
    return (
      <section className="rounded-3xl border border-amber-400/20 bg-white/[0.03] p-6">
        <EmptyState
          title="Tournament winner prices loading"
          description="Prediction market odds are fetched on a schedule. Check back in a few minutes."
        />
      </section>
    );
  }

  const sorted = sortMarketsByYesPct(data.markets);
  const cap = limit ?? (compact ? 5 : DEFAULT_VISIBLE);
  const visible = expanded ? sorted : sorted.slice(0, cap);
  const hasMore = !limit && sorted.length > cap;

  return (
    <div>
      <PredictionMarketPanel
        title={data.title || "Tournament winner"}
        markets={visible}
        compact={compact}
        renderLabel={(market) =>
          parseTeamFromQuestion(market.question) ?? market.question
        }
        renderIcon={(market) => {
          const team = parseTeamFromQuestion(market.question);
          return team ? (
            <TeamCrest teamName={team} size={compact ? 28 : 32} />
          ) : null;
        }}
      />
      {hasMore ? (
        <button
          type="button"
          onClick={() => setExpanded((v) => !v)}
          className="mt-3 text-sm font-medium text-emerald-400 hover:text-emerald-300"
        >
          {expanded ? "Show less" : `Show all ${sorted.length} teams`}
        </button>
      ) : null}
    </div>
  );
}

/** Compact teaser for home page — top N without expand control. */
export function TournamentWinnerTeaser({
  data,
  topN = 5,
}: {
  data: CleanedTournamentOdds | null;
  topN?: number;
}) {
  if (!data?.markets?.length) return null;

  const markets = sortMarketsByYesPct(data.markets).slice(0, topN);

  return (
    <PredictionMarketPanel
      title="Who wins the World Cup?"
      markets={markets}
      compact
      renderLabel={(market: CleanedMarket) =>
        parseTeamFromQuestion(market.question) ?? market.question
      }
      renderIcon={(market) => {
        const team = parseTeamFromQuestion(market.question);
        return team ? <TeamCrest teamName={team} size={28} /> : null;
      }}
    />
  );
}
