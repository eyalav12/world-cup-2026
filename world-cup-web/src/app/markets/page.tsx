import Link from "next/link";
import { MarketsExplorer } from "@/components/predictions/markets-explorer";
import { getTeamsByGroups, getTopScorerOdds, getTournamentWinnerOdds } from "@/lib/api/endpoints";
import { ApiError } from "@/lib/api/client";
import { ErrorBanner } from "@/components/ui/error-banner";
import { sortGroupIds } from "@/lib/teams";
import {
  filterTournamentOddsByTeams,
  flattenTeamsFromGroups,
} from "@/lib/predictions";

export const metadata = { title: "Markets" };

export default async function MarketsPage() {
  let winnerOdds: Awaited<ReturnType<typeof getTournamentWinnerOdds>> = null;
  let topScorerOdds: Awaited<ReturnType<typeof getTopScorerOdds>> = null;
  let groups: string[] = [];
  let error: string | null = null;

  try {
    const [winner, topScorer, teamsByGroup] = await Promise.all([
      getTournamentWinnerOdds(),
      getTopScorerOdds(),
      getTeamsByGroups(),
    ]);
    winnerOdds = filterTournamentOddsByTeams(
      winner,
      flattenTeamsFromGroups(teamsByGroup),
    );
    topScorerOdds = topScorer;
    groups = sortGroupIds(Object.keys(teamsByGroup));
  } catch (e) {
    error =
      e instanceof ApiError
        ? e.message
        : "Could not load prediction markets.";
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
