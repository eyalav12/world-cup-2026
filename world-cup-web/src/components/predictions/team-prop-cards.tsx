import type { CleanedMarket } from "@/lib/api/types";
import { formatPredictionPct, yesPercent } from "@/lib/predictions";
import { PredictionMarketBadge } from "@/components/predictions/prediction-market-badge";
import { TeamCrest } from "@/components/teams/team-crest";
import { EmptyState } from "@/components/ui/empty-state";

/** Ready for GET /polymarketodds/team?teamName= when backend ships. */
export function TeamPropCards({
  teamName,
  props,
}: {
  teamName: string;
  props: CleanedMarket[];
}) {
  return (
    <section className="mt-10">
      <div className="mb-4 flex flex-wrap items-center gap-3">
        <h2 className="text-2xl font-semibold text-white">Related predictions</h2>
        <PredictionMarketBadge />
      </div>

      {props.length === 0 ? (
        <EmptyState
          title="No predictions for this team yet"
          description="Market odds for this nation will appear here when available."
        />
      ) : (
        <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
          {props.slice(0, 3).map((market) => (
            <div
              key={market.question}
              className="rounded-2xl border border-amber-400/20 bg-white/5 p-4"
            >
              <div className="mb-3 flex items-center gap-2">
                <TeamCrest teamName={teamName} size={28} />
                <p className="line-clamp-2 text-sm text-emerald-100/80">
                  {market.question}
                </p>
              </div>
              <p className="text-2xl font-bold tabular-nums text-emerald-300">
                {formatPredictionPct(yesPercent(market))}
              </p>
              <p className="mt-1 text-xs text-emerald-100/50">Yes probability</p>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
