"use client";

import { useState } from "react";
import type { CleanedMarket, CleanedTournamentOdds } from "@/lib/api/types";
import { parseSubjectFromQuestion, sortMarketsByYesPct } from "@/lib/predictions";
import { PredictionMarketPanel } from "@/components/predictions/prediction-market-panel";
import { EmptyState } from "@/components/ui/empty-state";

const DEFAULT_VISIBLE = 12;

export function TopScorerPanel({
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
          title="Golden Boot prices loading"
          description="Top scorer markets are fetched on a schedule. Check back in a few minutes."
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
        title={data.title || "Golden Boot"}
        markets={visible}
        compact={compact}
        emptyTitle="No top scorer markets yet"
        emptyDescription="Golden Boot odds will appear after the next Polymarket sync."
        renderLabel={(market) => parseSubjectFromQuestion(market.question)}
      />
      {hasMore ? (
        <button
          type="button"
          onClick={() => setExpanded((v) => !v)}
          className="mt-3 text-sm font-medium text-emerald-400 hover:text-emerald-300"
        >
          {expanded ? "Show less" : `Show all ${sorted.length} players`}
        </button>
      ) : null}
    </div>
  );
}

/** Compact teaser for home page — top N without expand control. */
export function TopScorerTeaser({
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
      title="Who wins the Golden Boot?"
      markets={markets}
      compact
      renderLabel={(market: CleanedMarket) =>
        parseSubjectFromQuestion(market.question)
      }
    />
  );
}
