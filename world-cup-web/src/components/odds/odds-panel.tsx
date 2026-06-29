import type { OddsSummaryDTO } from "@/lib/api/types";
import { Card, CardTitle } from "@/components/ui/card";
import { EmptyState } from "@/components/ui/empty-state";

function pct(value: number | undefined): string {
  if (value == null || Number.isNaN(value)) return "—";
  return `${value.toFixed(1)}%`;
}

export function OddsPanel({
  odds,
  homeTeam,
  awayTeam,
  closedLines = false,
}: {
  odds: OddsSummaryDTO;
  homeTeam: string;
  awayTeam: string;
  /** True for finished matches — shows last synced pre-kickoff lines. */
  closedLines?: boolean;
}) {
  const avg = odds.marketAverage;
  const books = odds.topSportsbooks ?? [];

  if (!avg && books.length === 0) {
    return (
      <EmptyState
        title={closedLines ? "Closing lines not saved" : "Odds not available yet"}
        description={
          closedLines
            ? "We don't have stored bookmaker lines for this finished match. Lines are saved when synced before kickoff."
            : "Betting lines for this match haven't been published yet. Check back closer to kickoff."
        }
      />
    );
  }

  return (
    <div className="space-y-4">
      {closedLines ? (
        <p className="text-sm text-emerald-100/60">
          Closing lines — last bookmaker snapshot before kickoff.
        </p>
      ) : null}
      {avg ? (
        <Card>
          <CardTitle className="mb-3">
            {closedLines ? "Closing market average" : "Market average"}
          </CardTitle>
          <div className="grid grid-cols-3 gap-3 text-center text-sm">
            <div>
              <p className="text-emerald-100/60">{homeTeam}</p>
              <p className="mt-1 text-xl font-semibold text-white">
                {pct(avg.home_win_pct)}
              </p>
            </div>
            <div>
              <p className="text-emerald-100/60">Draw</p>
              <p className="mt-1 text-xl font-semibold text-white">
                {pct(avg.draw_pct)}
              </p>
            </div>
            <div>
              <p className="text-emerald-100/60">{awayTeam}</p>
              <p className="mt-1 text-xl font-semibold text-white">
                {pct(avg.away_win_pct)}
              </p>
            </div>
          </div>
        </Card>
      ) : null}

      {books.length > 0 ? (
        <Card>
          <CardTitle className="mb-3">Top sportsbooks</CardTitle>
          <div className="overflow-x-auto">
            <table className="w-full min-w-[480px] text-left text-sm">
              <thead>
                <tr className="text-emerald-100/50">
                  <th className="pb-2 pr-4 font-medium">Book</th>
                  <th className="pb-2 pr-4 font-medium">Home</th>
                  <th className="pb-2 pr-4 font-medium">Draw</th>
                  <th className="pb-2 font-medium">Away</th>
                </tr>
              </thead>
              <tbody>
                {books.map((row) => (
                  <tr
                    key={row.bookmaker}
                    className="border-t border-white/10 text-white"
                  >
                    <td className="py-2 pr-4">{row.bookmaker}</td>
                    <td className="py-2 pr-4 tabular-nums">
                      {row.homeOdds}{" "}
                      <span className="text-emerald-100/50">
                        ({pct(row.homePct)})
                      </span>
                    </td>
                    <td className="py-2 pr-4 tabular-nums">
                      {row.drawOdds}{" "}
                      <span className="text-emerald-100/50">
                        ({pct(row.drawPct)})
                      </span>
                    </td>
                    <td className="py-2 tabular-nums">
                      {row.awayOdds}{" "}
                      <span className="text-emerald-100/50">
                        ({pct(row.awayPct)})
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Card>
      ) : null}
    </div>
  );
}
