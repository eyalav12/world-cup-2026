import Link from "next/link";
import { notFound } from "next/navigation";
import { CompactMatchList } from "@/components/matches/compact-match-list";
import { MatchCard } from "@/components/matches/match-card";
import { TeamCrest } from "@/components/teams/team-crest";
import { HistoryMatchList } from "@/components/history/history-match-list";
import { TeamRelatedNews } from "@/components/news/team-related-news";
import { Card, CardTitle } from "@/components/ui/card";
import { EmptyState } from "@/components/ui/empty-state";
import { TeamPropCards } from "@/components/predictions/team-prop-cards";
import {
  getMatchesByTeamName,
  getTeamLastMatches,
  getTeamLastMatchesFromApi,
  getTeamPolymarketProps,
  getTeamSquad,
  getTeamTournamentResults,
} from "@/lib/api/endpoints";
import { teamNameFromSlug } from "@/lib/utils";

type Props = { params: Promise<{ teamSlug: string }> };

export async function generateMetadata({ params }: Props) {
  const name = teamNameFromSlug((await params).teamSlug);
  return { title: name };
}

export default async function TeamDetailPage({ params }: Props) {
  const teamName = teamNameFromSlug((await params).teamSlug);
  if (!teamName) notFound();

  const [
    squadResult,
    upcomingResult,
    historyResult,
    apiRecentResult,
    tournamentResultsResult,
    teamPropsResult,
  ] = await Promise.allSettled([
    getTeamSquad(teamName),
    getMatchesByTeamName(teamName),
    getTeamLastMatches(teamName),
    getTeamLastMatchesFromApi(teamName, 5),
    getTeamTournamentResults(teamName, 6),
    getTeamPolymarketProps(teamName),
  ]);

  const squad =
    squadResult.status === "fulfilled" ? squadResult.value : [];
  const upcoming =
    upcomingResult.status === "fulfilled" ? upcomingResult.value : [];
  const history =
    historyResult.status === "fulfilled" ? historyResult.value : [];
  const apiRecent =
    apiRecentResult.status === "fulfilled" ? apiRecentResult.value : [];
  const tournamentResults =
    tournamentResultsResult.status === "fulfilled"
      ? tournamentResultsResult.value
      : [];
  const teamProps =
    teamPropsResult.status === "fulfilled" ? teamPropsResult.value : null;

  if (squad.length === 0 && upcoming.length === 0 && history.length === 0) {
    notFound();
  }

  const byPosition = squad.reduce<Record<string, typeof squad>>((acc, p) => {
    const key = p.position || "Other";
    (acc[key] ??= []).push(p);
    return acc;
  }, {});

  return (
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
      <Link
        href="/teams"
        className="text-sm text-emerald-400 hover:text-emerald-300"
      >
        ← All teams
      </Link>

      <div className="mt-6 flex items-center gap-4">
        <TeamCrest teamName={teamName} size={72} />
        <div>
          <h1 className="text-3xl font-bold text-white">{teamName}</h1>
          <p className="text-emerald-100/60">Squad & fixtures</p>
        </div>
      </div>

      <section className="mt-10">
        <h2 className="mb-4 text-2xl font-semibold text-white">Squad</h2>
        {squad.length === 0 ? (
          <EmptyState title="Squad not available yet" />
        ) : (
          <div className="grid gap-6 sm:grid-cols-2">
            {Object.entries(byPosition).map(([pos, players]) => (
              <Card key={pos}>
                <CardTitle className="mb-3">{pos}</CardTitle>
                <ul className="space-y-1 text-sm text-emerald-100/90">
                  {players.map((p) => (
                    <li key={p.name} className="flex justify-between gap-2">
                      <span className="text-white">{p.name}</span>
                    </li>
                  ))}
                </ul>
              </Card>
            ))}
          </div>
        )}
      </section>

      <section className="mt-10">
        <h2 className="mb-4 text-2xl font-semibold text-white">
          Upcoming matches
        </h2>
        {upcoming.length === 0 ? (
          <EmptyState title="No upcoming matches" />
        ) : (
          <div className="grid gap-4 sm:grid-cols-2">
            {upcoming.map((m) => (
              <MatchCard key={m.matchId} match={m} />
            ))}
          </div>
        )}
      </section>

      <section className="mt-10">
        <Card>
          <CardTitle className="mb-4">2026 World Cup results</CardTitle>
          <CompactMatchList
            matches={tournamentResults}
            linkToDetail
            emptyMessage="No finished matches in this tournament yet."
          />
        </Card>
      </section>

      <section className="mt-10">
        <Card>
          <CardTitle className="mb-4">Recent form (Football Data API)</CardTitle>
          <CompactMatchList
            matches={apiRecent}
            emptyMessage="No recent matches returned from the API for this team."
          />
        </Card>
      </section>

      <section className="mt-10">
        <Card>
          <CardTitle className="mb-4">Recent World Cup history</CardTitle>
          <HistoryMatchList matches={history} />
        </Card>
      </section>

      {teamProps?.markets?.length ? (
        <TeamPropCards
          teamName={teamName}
          props={teamProps.markets}
        />
      ) : null}

      <TeamRelatedNews teamName={teamName} />
    </div>
  );
}
