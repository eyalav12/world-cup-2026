"use client";

import { useQuery } from "@tanstack/react-query";
import { MatchCard } from "@/components/matches/match-card";
import { GroupStandingsTable } from "@/components/groups/group-standings-table";
import { TournamentWinnerPanel } from "@/components/predictions/tournament-winner-panel";
import {
  getGroupWinnerOdds,
  getMatchesByGroupName,
  getStandingsByGroup,
} from "@/lib/api/endpoints";
import { formatGroupId } from "@/lib/teams";
import { EmptyState } from "@/components/ui/empty-state";

export function GroupsExplorer({
  groups,
  initialGroup,
}: {
  groups: string[];
  initialGroup?: string;
}) {
  const group = initialGroup ?? groups[0];

  const { data: matches, isLoading: matchesLoading } = useQuery({
    queryKey: ["group-matches", group],
    queryFn: () => getMatchesByGroupName(group!),
    enabled: Boolean(group),
  });

  const { data: standings, isLoading: standingsLoading } = useQuery({
    queryKey: ["group-standings", group],
    queryFn: () => getStandingsByGroup(group!),
    enabled: Boolean(group),
  });

  const { data: groupWinnerOdds, isLoading: oddsLoading } = useQuery({
    queryKey: ["group-winner-odds", group],
    queryFn: () => getGroupWinnerOdds(group!),
    enabled: Boolean(group),
  });

  if (!group) {
    return <EmptyState title="No groups available" />;
  }

  return (
    <div className="space-y-12">
      <section>
        <h2 className="mb-4 text-xl font-semibold text-white">
          Standings — {formatGroupId(group)}
        </h2>
        {standingsLoading ? (
          <p className="text-sm text-emerald-100/60">Loading standings…</p>
        ) : (
          <GroupStandingsTable rows={standings ?? []} />
        )}
      </section>

      <section>
        <h2 className="mb-4 text-xl font-semibold text-white">
          Group winner market — {formatGroupId(group)}
        </h2>
        {oddsLoading ? (
          <p className="text-sm text-emerald-100/60">Loading prediction market…</p>
        ) : (
          <TournamentWinnerPanel data={groupWinnerOdds ?? null} limit={4} />
        )}
      </section>

      <section>
        <h2 className="mb-4 text-xl font-semibold text-white">
          Upcoming matches — {formatGroupId(group)}
        </h2>
        {matchesLoading ? (
          <p className="text-sm text-emerald-100/60">Loading…</p>
        ) : null}
        {matches && matches.length === 0 ? (
          <EmptyState title="No upcoming group matches" />
        ) : null}
        {matches && matches.length > 0 ? (
          <div className="grid gap-4 sm:grid-cols-2">
            {matches.map((m) => (
              <MatchCard key={m.matchId} match={m} />
            ))}
          </div>
        ) : null}
      </section>
    </div>
  );
}
