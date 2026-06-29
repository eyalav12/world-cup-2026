import Link from "next/link";
import { MatchCard } from "@/components/matches/match-card";
import { CompactMatchList } from "@/components/matches/compact-match-list";
import { TopScorerTeaser } from "@/components/predictions/top-scorer-panel";
import { TournamentWinnerTeaser } from "@/components/predictions/tournament-winner-panel";
import { ErrorBanner } from "@/components/ui/error-banner";
import { EmptyState } from "@/components/ui/empty-state";
import {
  getTeamsByGroups,
  getTopScorerOdds,
  getTournamentWinnerOdds,
} from "@/lib/api/endpoints";
import { ApiError } from "@/lib/api/client";
import { fetchHomeMatchSections } from "@/lib/matches";
import {
  filterTournamentOddsByTeams,
  flattenTeamsFromGroups,
} from "@/lib/predictions";
import { formatGroupId, sortGroupIds } from "@/lib/teams";

export default async function HomePage() {
  let live = [] as Awaited<ReturnType<typeof fetchHomeMatchSections>>["live"];
  let upcoming = [] as Awaited<
    ReturnType<typeof fetchHomeMatchSections>
  >["upcoming"];
  let justFinished = [] as Awaited<
    ReturnType<typeof fetchHomeMatchSections>
  >["justFinished"];
  let teamsByGroup: Record<string, string[]> = {};
  let winnerOdds: Awaited<ReturnType<typeof getTournamentWinnerOdds>> = null;
  let topScorerOdds: Awaited<ReturnType<typeof getTopScorerOdds>> = null;
  let apiError: string | null = null;

  try {
    const [sections, groups] = await Promise.all([
      fetchHomeMatchSections(10),
      getTeamsByGroups(),
    ]);
    live = sections.live;
    upcoming = sections.upcoming;
    justFinished = sections.justFinished;
    teamsByGroup = groups;
  } catch (e) {
    apiError =
      e instanceof ApiError
        ? e.message
        : "We couldn't load tournament data right now. Please try again in a moment.";
  }

  try {
    [winnerOdds, topScorerOdds] = await Promise.all([
      getTournamentWinnerOdds(),
      getTopScorerOdds(),
    ]);
    const qualified = flattenTeamsFromGroups(teamsByGroup);
    if (qualified.length) {
      winnerOdds = filterTournamentOddsByTeams(winnerOdds, qualified);
    }
  } catch {
    /* optional teaser — ignore */
  }

  const groups = sortGroupIds(Object.keys(teamsByGroup));
  const teamCount = Object.values(teamsByGroup).flat().length;

  return (
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6 sm:py-12">
      <section className="mb-12">
        <p className="text-sm font-medium uppercase tracking-wider text-emerald-400">
          FIFA World Cup 2026
        </p>
        <h1 className="mt-2 max-w-2xl text-4xl font-bold tracking-tight text-white sm:text-5xl">
          Your tournament command center
        </h1>
        <p className="mt-4 max-w-xl text-emerald-100/70">
          Upcoming fixtures, group standings, squad lists, betting odds, and
          historical form for World Cup 2026.
        </p>
        <div className="mt-6 flex flex-wrap gap-3">
          <Link
            href="/matches"
            className="rounded-full bg-emerald-500 px-5 py-2.5 text-sm font-semibold text-[#0b1f14] hover:bg-emerald-400"
          >
            Browse matches
          </Link>
          <Link
            href="/agent"
            className="rounded-full border border-white/20 px-5 py-2.5 text-sm font-medium text-white hover:bg-white/10"
          >
            Open AI assistant
          </Link>
        </div>
      </section>

      {apiError ? (
        <div className="mb-8">
          <ErrorBanner message={apiError} />
        </div>
      ) : null}

      <div className="mb-10 grid gap-4 sm:grid-cols-3">
        <Stat label="Groups" value={String(groups.length)} />
        <Stat label="Teams" value={String(teamCount)} />
        <Stat
          label="Fixtures ahead"
          value={String(live.length + upcoming.length)}
        />
      </div>

      {live.length > 0 ? (
        <section className="mb-12">
          <div className="mb-4 flex items-end justify-between">
            <h2 className="text-2xl font-semibold text-white">Live now</h2>
            <Link
              href="/matches"
              className="text-sm text-emerald-400 hover:text-emerald-300"
            >
              All matches →
            </Link>
          </div>
          <div className="grid gap-4 sm:grid-cols-2">
            {live.map((m) => (
              <MatchCard key={m.matchId} match={m} />
            ))}
          </div>
        </section>
      ) : null}

      <section className="mb-12">
        <div className="mb-4 flex items-end justify-between">
          <h2 className="text-2xl font-semibold text-white">Upcoming matches</h2>
          <Link
            href="/matches"
            className="text-sm text-emerald-400 hover:text-emerald-300"
          >
            View all →
          </Link>
        </div>
        {upcoming.length === 0 ? (
          <EmptyState
            title="No upcoming matches"
            description="The tournament kicks off on 11 June 2026. Check back closer to the opening fixtures, or browse by date on the Matches page."
          />
        ) : (
          <div className="grid gap-4 sm:grid-cols-2">
            {upcoming.slice(0, 6).map((m) => (
              <MatchCard key={m.matchId} match={m} />
            ))}
          </div>
        )}
      </section>

      {justFinished.length > 0 ? (
        <section className="mb-12">
          <div className="mb-4 flex items-end justify-between">
            <h2 className="text-2xl font-semibold text-white">Just finished</h2>
            <Link
              href="/matches"
              className="text-sm text-emerald-400 hover:text-emerald-300"
            >
              Browse by date →
            </Link>
          </div>
          <CompactMatchList matches={justFinished} linkToDetail />
        </section>
      ) : null}

      {winnerOdds?.markets?.length || topScorerOdds?.markets?.length ? (
        <section className="mb-12">
          <div className="mb-4 flex items-end justify-between">
            <h2 className="text-2xl font-semibold text-white">
              Prediction markets
            </h2>
            <Link
              href="/markets"
              className="text-sm text-emerald-400 hover:text-emerald-300"
            >
              All prediction markets →
            </Link>
          </div>
          <div className="grid gap-6 lg:grid-cols-2">
            {winnerOdds?.markets?.length ? (
              <TournamentWinnerTeaser data={winnerOdds} topN={5} />
            ) : null}
            {topScorerOdds?.markets?.length ? (
              <TopScorerTeaser data={topScorerOdds} topN={5} />
            ) : null}
          </div>
        </section>
      ) : null}

      {groups.length > 0 ? (
        <section>
          <h2 className="mb-4 text-2xl font-semibold text-white">Groups</h2>
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            {groups.slice(0, 8).map((g) => (
              <Link
                key={g}
                href={`/groups?group=${encodeURIComponent(g)}`}
                className="rounded-2xl border border-white/10 bg-white/5 p-4 hover:border-emerald-400/30"
              >
                <p className="font-semibold text-white">{formatGroupId(g)}</p>
                <p className="mt-1 text-sm text-emerald-100/60">
                  {(teamsByGroup[g] ?? []).length} teams
                </p>
              </Link>
            ))}
          </div>
        </section>
      ) : null}
    </div>
  );
}

function Stat({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-2xl border border-white/10 bg-white/5 px-5 py-4">
      <p className="text-sm text-emerald-100/60">{label}</p>
      <p className="mt-1 text-3xl font-bold text-white">{value}</p>
    </div>
  );
}
