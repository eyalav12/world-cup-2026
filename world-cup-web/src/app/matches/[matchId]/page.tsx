import Link from "next/link";
import { notFound } from "next/navigation";
import { OddsPanel } from "@/components/odds/odds-panel";
import { TeamCrest } from "@/components/teams/team-crest";
import { HistoryMatchList } from "@/components/history/history-match-list";
import { Badge } from "@/components/ui/badge";
import { Card, CardTitle } from "@/components/ui/card";
import { ErrorBanner } from "@/components/ui/error-banner";
import {
  getHeadToHead,
  getMatchLineups,
  getOddsSummary,
} from "@/lib/api/endpoints";
import { MatchLineupsPanel } from "@/components/matches/match-lineups-panel";
import { findMatchById } from "@/lib/match-lookup";
import {
  formatMatchDateTime,
  isFinishedMatch,
  isLiveMatch,
  matchStatusLabel,
} from "@/lib/matches";

type Props = { params: Promise<{ matchId: string }> };

export async function generateMetadata({ params }: Props) {
  const { matchId } = await params;
  const match = await findMatchById(Number(matchId));
  if (!match) return { title: "Match" };
  return {
    title: `${match.homeTeam} vs ${match.awayTeam}`,
  };
}

export default async function MatchDetailPage({ params }: Props) {
  const { matchId: raw } = await params;
  const matchId = Number(raw);
  if (Number.isNaN(matchId)) notFound();

  const match = await findMatchById(matchId);
  if (!match) notFound();

  let odds = null;
  let lineups: Awaited<ReturnType<typeof getMatchLineups>> = null;
  let h2h: Awaited<ReturnType<typeof getHeadToHead>> = [];
  let oddsError: string | null = null;

  try {
    [odds, lineups, h2h] = await Promise.all([
      getOddsSummary(matchId),
      getMatchLineups(matchId),
      getHeadToHead(match.homeTeam, match.awayTeam),
    ]);
  } catch (e) {
    oddsError = e instanceof Error ? e.message : "Could not load match details.";
  }

  const live = isLiveMatch(match);
  const finished = isFinishedMatch(match);

  return (
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
      <Link
        href="/matches"
        className="text-sm text-emerald-400 hover:text-emerald-300"
      >
        ← Back to matches
      </Link>

      <div className="mt-6 rounded-3xl border border-white/10 bg-white/5 p-6 sm:p-8">
        <div className="mb-4 flex flex-wrap items-center gap-2">
          <Badge
            variant={live ? "live" : finished ? "finished" : "upcoming"}
          >
            {matchStatusLabel(match.status)}
          </Badge>
          <span className="text-sm text-emerald-100/60">
            {formatMatchDateTime(match)}
          </span>
        </div>

        <div className="flex flex-col items-center gap-6 sm:flex-row sm:justify-center">
          <div className="flex flex-col items-center gap-2 text-center">
            <TeamCrest teamName={match.homeTeam} size={64} />
            <h1 className="text-2xl font-bold text-white">{match.homeTeam}</h1>
          </div>
          <div className="text-center">
            <p className="text-4xl font-bold tabular-nums text-white">
              {finished || live ? (match.score ?? "–") : "vs"}
            </p>
            <p className="mt-2 text-sm text-emerald-100/50">
              {match.stage.replace(/_/g, " ")}
            </p>
          </div>
          <div className="flex flex-col items-center gap-2 text-center">
            <TeamCrest teamName={match.awayTeam} size={64} />
            <h1 className="text-2xl font-bold text-white">{match.awayTeam}</h1>
          </div>
        </div>
      </div>

      {oddsError ? (
        <div className="mt-6">
          <ErrorBanner message={oddsError} />
        </div>
      ) : null}

      <section className="mt-10">
        <h2 className="mb-4 text-2xl font-semibold text-white">Lineups</h2>
        <MatchLineupsPanel lineups={lineups} />
      </section>

      <section className="mt-10">
        <h2 className="mb-4 text-2xl font-semibold text-white">Odds</h2>
        <OddsPanel
          odds={odds ?? {}}
          homeTeam={match.homeTeam}
          awayTeam={match.awayTeam}
        />
      </section>

      <section className="mt-10">
        <Card>
          <CardTitle className="mb-4">Head to head (World Cup history)</CardTitle>
          <HistoryMatchList matches={h2h} />
        </Card>
      </section>

      <section className="mt-10 flex flex-wrap gap-3">
        <Link
          href={`/teams/${encodeURIComponent(match.homeTeam)}`}
          className="rounded-full border border-white/20 px-4 py-2 text-sm text-white hover:bg-white/10"
        >
          {match.homeTeam} squad →
        </Link>
        <Link
          href={`/teams/${encodeURIComponent(match.awayTeam)}`}
          className="rounded-full border border-white/20 px-4 py-2 text-sm text-white hover:bg-white/10"
        >
          {match.awayTeam} squad →
        </Link>
      </section>
    </div>
  );
}
