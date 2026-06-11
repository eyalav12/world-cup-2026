import Link from "next/link";
import { MarketsExplorer } from "@/components/predictions/markets-explorer";
import { getTeamsByGroups, getTopScorerOdds, getTournamentWinnerOdds } from "@/lib/api/endpoints";
import { toUserMessage } from "@/lib/api/client";
import { ErrorBanner } from "@/components/ui/error-banner";
import { sortGroupIds } from "@/lib/teams";

export const metadata = { title: "Markets" };

export default async function MarketsPage() {
  let winnerOdds: Awaited<ReturnType<typeof getTournamentWinnerOdds>> = null;
  let topScorerOdds: Awaited<ReturnType<typeof getTopScorerOdds>> = null;
  let groups: string[] = [];
  let error: string | null = null;

  const [winnerResult, topScorerResult, teamsResult] = await Promise.allSettled([
    getTournamentWinnerOdds(),
    getTopScorerOdds(),
    getTeamsByGroups(),
  ]);

  if (winnerResult.status === "fulfilled") {
    winnerOdds = winnerResult.value;
  } else {
    error = toUserMessage(winnerResult.reason, "Could not load prediction markets.");
  }

  if (topScorerResult.status === "fulfilled") {
    topScorerOdds = topScorerResult.value;
  } else if (!error) {
    error = toUserMessage(topScorerResult.reason, "Could not load prediction markets.");
  }

  if (teamsResult.status === "fulfilled") {
    groups = sortGroupIds(Object.keys(teamsResult.value));
  }

  return (
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
      <h1 className="text-3xl font-bold text-white">Prediction markets</h1>
      <p className="mt-2 text-emerald-100/70">
        Crowd probabilities from Polymarket — tournament winner, top scorer, and
        more.
      </p>
      <p className="mt-2 text-sm text-emerald-100/50">
        For bookmaker match lines see{" "}
        <Link href="/odds" className="text-emerald-400 hover:text-emerald-300">
          Match odds
        </Link>
        .
      </p>

      {error ? (
        <div className="mt-6">
          <ErrorBanner message={error} />
        </div>
      ) : null}

      <div className="mt-10">
        <MarketsExplorer
          winnerOdds={winnerOdds}
          topScorerOdds={topScorerOdds}
          groups={groups}
        />
      </div>
    </div>
  );
}
