import type { ReactNode } from "react";
import type { CleanedMarket } from "@/lib/api/types";
import { yesPercent } from "@/lib/predictions";
import { PredictionMarketBadge } from "@/components/predictions/prediction-market-badge";
import { OutcomeBar } from "@/components/predictions/outcome-bar";
import { EmptyState } from "@/components/ui/empty-state";

export function PredictionMarketPanel({
  title,
  markets,
  renderLabel,
  renderIcon,
  emptyTitle = "No data yet",
  emptyDescription = "Prediction market prices will appear after the next sync.",
  showBadge = true,
  compact = false,
}: {
  title: string;
  markets: CleanedMarket[];
  renderLabel: (market: CleanedMarket) => string;
  renderIcon?: (market: CleanedMarket) => ReactNode;
  emptyTitle?: string;
  emptyDescription?: string;
  showBadge?: boolean;
  compact?: boolean;
}) {
  return (
    <section className="rounded-3xl border border-amber-400/20 bg-white/[0.03] p-6">
      <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
        <h2 className="text-xl font-semibold text-white">{title}</h2>
        {showBadge ? <PredictionMarketBadge /> : null}
      </div>

      {markets.length === 0 ? (
        <EmptyState title={emptyTitle} description={emptyDescription} />
      ) : (
        <div className={compact ? "space-y-3" : "space-y-4"}>
          {markets.map((market) => (
            <OutcomeBar
              key={market.question}
              label={renderLabel(market)}
              pct={yesPercent(market)}
              icon={renderIcon?.(market)}
              compact={compact}
            />
          ))}
        </div>
      )}

      {markets.length > 0 ? (
        <p className="mt-5 text-xs text-emerald-100/50">
          Source: Polymarket · crowd probabilities, not bookmaker odds
        </p>
      ) : null}
    </section>
  );
}
