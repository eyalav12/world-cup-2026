import Link from "next/link";
import { OddsPanel } from "@/components/odds/odds-panel";
import { TournamentWinnerPanel } from "@/components/predictions/tournament-winner-panel";
import { TopScorerPanel } from "@/components/predictions/top-scorer-panel";
import { TeamCrest } from "@/components/teams/team-crest";
import { ErrorBanner } from "@/components/ui/error-banner";
import { EmptyState } from "@/components/ui/empty-state";
import { getTeamsByGroups, getTopScorerOdds, getTournamentWinnerOdds } from "@/lib/api/endpoints";
import { ApiError } from "@/lib/api/client";
import type { MatchDto } from "@/lib/api/types";
import {
  fetchRecentFinishedForOdds,
  fetchUpcomingWindow,
  formatMatchDateTime,
  hasOddsData,
  loadOddsForMatches,
} from "@/lib/matches";
import {
  filterTournamentOddsByTeams,
  flattenTeamsFromGroups,
} from "@/lib/predictions";

export const metadata = { title: "Odds" };

function MatchOddsBlock({
  match,
  odds,
  error,
  closedLines,
}: {
  match: MatchDto;
  odds: Awaited<ReturnType<typeof loadOddsForMatches>>[number]["odds"];
  error: string | null;
  closedLines?: boolean;
}) {
  return (
    <section className="rounded-3xl border border-white/10 bg-white/[0.03] p-6">
      <div className="mb-6 flex flex-wrap items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <TeamCrest teamName={match.homeTeam} size={36} />
          <span className="font-semibold text-white">{match.homeTeam}</span>
          <span className="text-emerald-100/50">vs</span>
          <span className="font-semibold text-white">{match.awayTeam}</span>
          <TeamCrest teamName={match.awayTeam} size={36} />
        </div>
        <div className="text-right text-sm">
          <p className="text-emerald-100/60">{formatMatchDateTime(match)}</p>
          <Link
            href={`/matches/${match.matchId}`}
            className="text-emerald-400 hover:text-emerald-300"
          >
            Match details →
          </Link>
        </div>
      </div>
      {error ? <ErrorBanner message={error} /> : null}
      <OddsPanel
        odds={odds}
        homeTeam={match.homeTeam}
        awayTeam={match.awayTeam}
        closedLines={closedLines}
      />
    </section>
  );
}

export default async function OddsPage() {
  let winnerOdds: Awaited<ReturnType<typeof getTournamentWinnerOdds>> = null;
  let topScorerOdds: Awaited<ReturnType<typeof getTopScorerOdds>> = null;
  let winnerError: string | null = null;

  try {
    const [winner, topScorer, teamsByGroup] = await Promise.all([
      getTournamentWinnerOdds(),
      getTopScorerOdds(),
      getTeamsByGroups(),
    ]);
    const qualified = flattenTeamsFromGroups(teamsByGroup);
    winnerOdds = filterTournamentOddsByTeams(winner, qualified);
    topScorerOdds = topScorer;
  } catch (e) {
    winnerError =
      e instanceof ApiError
        ? e.message
        : "Could not load prediction market odds.";
  }

  const [upcoming, recentFinished] = await Promise.all([
    fetchUpcomingWindow(7),
    fetchRecentFinishedForOdds(6),
  ]);
  const featuredUpcoming = upcoming.slice(0, 6);

  const [upcomingOdds, finishedOdds] = await Promise.all([
    loadOddsForMatches(featuredUpcoming),
    loadOddsForMatches(recentFinished),
  ]);

  const hasUpcomingOdds = upcomingOdds.some((r) => hasOddsData(r.odds));
  const hasFinishedOdds = finishedOdds.some((r) => hasOddsData(r.odds));

  return (
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
      <h1 className="text-3xl font-bold text-white">Odds</h1>
      <p className="mt-2 text-emerald-100/70">
        Tournament prediction markets and bookmaker lines.{" "}
        <Link href="/markets" className="text-emerald-400 hover:text-emerald-300">
          Browse all markets →
        </Link>
      </p>

      {winnerError ? (
        <div className="mt-6">
          <ErrorBanner message={winnerError} />
        </div>
      ) : null}

      <section className="mt-10">
        <h2 className="mb-4 text-sm font-medium uppercase tracking-wider text-amber-200/80">
          Prediction markets
        </h2>
        <TournamentWinnerPanel data={winnerOdds} />
        <div className="mt-8">
          <TopScorerPanel data={topScorerOdds} />
        </div>
      </section>

      <section className="mt-14">
        <h2 className="mb-6 text-sm font-medium uppercase tracking-wider text-emerald-200/80">
          Upcoming match odds
        </h2>
        <p className="mb-6 text-sm text-emerald-100/60">
          Home, draw, and away lines from sportsbooks for upcoming fixtures.
        </p>

        {featuredUpcoming.length === 0 ? (
          <EmptyState title="No upcoming matches to show odds for" />
        ) : null}

        {!hasUpcomingOdds && featuredUpcoming.length > 0 ? (
          <div className="mb-6">
            <ErrorBanner message="Odds aren't available for these matches yet. They'll appear closer to kickoff." />
          </div>
        ) : null}

        <div className="space-y-12">
          {upcomingOdds.map(({ match, odds, error }) => (
            <MatchOddsBlock key={match.matchId} match={match} odds={odds} error={error} />
          ))}
        </div>
      </section>

      <section className="mt-14">
        <h2 className="mb-6 text-sm font-medium uppercase tracking-wider text-emerald-200/80">
          Recent results — closing lines
        </h2>
        <p className="mb-6 text-sm text-emerald-100/60">
          Last bookmaker snapshot saved before kickoff for finished matches.
        </p>

        {recentFinished.length === 0 ? (
          <EmptyState title="No recent finished matches" />
        ) : null}

        {!hasFinishedOdds && recentFinished.length > 0 ? (
          <div className="mb-6">
            <ErrorBanner message="No closing lines saved for these results yet. They appear after odds sync runs while the match is still upcoming." />
          </div>
        ) : null}

        <div className="space-y-12">
          {finishedOdds.map(({ match, odds, error }) => (
            <MatchOddsBlock
              key={match.matchId}
              match={match}
              odds={odds}
              error={error}
              closedLines
            />
          ))}
        </div>
      </section>
    </div>
  );
}
