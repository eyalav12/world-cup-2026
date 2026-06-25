import { MatchCard } from "@/components/matches/match-card";
import { CompactMatchList } from "@/components/matches/compact-match-list";
import type { MatchDto } from "@/lib/api/types";
import {
  formatTournamentDayLabel,
  groupUpcomingByTournamentDay,
  partitionMatchesByStatus,
} from "@/lib/matches";
import { EmptyState } from "@/components/ui/empty-state";

type Props = {
  matches: MatchDto[];
  /** Group upcoming fixtures under date headers (good for group pages). */
  groupUpcomingByDate?: boolean;
  showLive?: boolean;
  showUpcoming?: boolean;
  showFinished?: boolean;
  upcomingLimit?: number;
  finishedLimit?: number;
  emptyTitle?: string;
};

export function MatchStatusSections({
  matches,
  groupUpcomingByDate = false,
  showLive = true,
  showUpcoming = true,
  showFinished = true,
  upcomingLimit,
  finishedLimit,
  emptyTitle = "No matches in this group yet",
}: Props) {
  const { live, upcoming, finished } = partitionMatchesByStatus(matches);
  const upcomingSlice =
    upcomingLimit != null ? upcoming.slice(0, upcomingLimit) : upcoming;
  const finishedSlice =
    finishedLimit != null ? finished.slice(0, finishedLimit) : finished;
  const upcomingByDay = groupUpcomingByDate
    ? groupUpcomingByTournamentDay(upcomingSlice)
    : null;

  const hasContent =
    (showLive && live.length > 0) ||
    (showUpcoming && upcomingSlice.length > 0) ||
    (showFinished && finishedSlice.length > 0);

  if (!hasContent) {
    return <EmptyState title={emptyTitle} />;
  }

  return (
    <div className="space-y-8">
      {showLive && live.length > 0 ? (
        <div>
          <h3 className="mb-3 text-sm font-semibold uppercase tracking-wider text-red-400">
            Live now
          </h3>
          <div className="grid gap-4 sm:grid-cols-2">
            {live.map((m) => (
              <MatchCard key={m.matchId} match={m} />
            ))}
          </div>
        </div>
      ) : null}

      {showUpcoming && upcomingSlice.length > 0 ? (
        <div>
          <h3 className="mb-3 text-sm font-semibold uppercase tracking-wider text-emerald-400">
            Upcoming
          </h3>
          {upcomingByDay ? (
            <div className="space-y-6">
              {[...upcomingByDay.entries()].map(([dayKey, dayMatches]) => (
                <div key={dayKey}>
                  <p className="mb-3 text-sm font-medium text-emerald-100/70">
                    {formatTournamentDayLabel(dayKey)}
                  </p>
                  <div className="grid gap-4 sm:grid-cols-2">
                    {dayMatches.map((m) => (
                      <MatchCard key={m.matchId} match={m} />
                    ))}
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="grid gap-4 sm:grid-cols-2">
              {upcomingSlice.map((m) => (
                <MatchCard key={m.matchId} match={m} />
              ))}
            </div>
          )}
        </div>
      ) : null}

      {showFinished && finishedSlice.length > 0 ? (
        <div>
          <h3 className="mb-3 text-sm font-semibold uppercase tracking-wider text-emerald-100/50">
            Recent results
          </h3>
          <CompactMatchList matches={finishedSlice} linkToDetail />
        </div>
      ) : null}
    </div>
  );
}
