"use client";

import { useQuery } from "@tanstack/react-query";
import { useSearchParams } from "next/navigation";
import { useMemo } from "react";
import { MatchStatusSections } from "@/components/matches/match-status-sections";
import { GroupStandingsTable } from "@/components/groups/group-standings-table";
import { TournamentWinnerPanel } from "@/components/predictions/tournament-winner-panel";
import {
  getGroupWinnerOdds,
  getMatchesByGroupName,
  getStandingsByGroup,
} from "@/lib/api/endpoints";
import { formatGroupId } from "@/lib/teams";
import { EmptyState } from "@/components/ui/empty-state";

function resolveGroup(
  groups: string[],
  groupParam: string | null,
): string | undefined {
  if (groupParam && groups.includes(groupParam)) return groupParam;
  return groups[0];
}

export function GroupsExplorer({ groups }: { groups: string[] }) {
  const searchParams = useSearchParams();
  const group = useMemo(
    () => resolveGroup(groups, searchParams.get("group")),
    [groups, searchParams],
  );

  const { data: matches, isPending: matchesPending } = useQuery({
    queryKey: ["group-matches", group],
    queryFn: () => getMatchesByGroupName(group!),
    enabled: Boolean(group),
  });

  const { data: standings, isPending: standingsPending } = useQuery({
    queryKey: ["group-standings", group],
    queryFn: () => getStandingsByGroup(group!),
    enabled: Boolean(group),
  });

  const { data: groupWinnerOdds, isPending: oddsPending } = useQuery({
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
        {standingsPending ? (
          <p className="text-sm text-emerald-100/60">Loading standings…</p>
        ) : (
          <GroupStandingsTable rows={standings ?? []} />
        )}
      </section>

      <section>
        <h2 className="mb-4 text-xl font-semibold text-white">
          Group winner market — {formatGroupId(group)}
        </h2>
        {oddsPending ? (
          <p className="text-sm text-emerald-100/60">Loading prediction market…</p>
        ) : (
          <TournamentWinnerPanel data={groupWinnerOdds ?? null} limit={4} />
        )}
      </section>

      <section>
        <h2 className="mb-4 text-xl font-semibold text-white">
          Matches — {formatGroupId(group)}
        </h2>
        {matchesPending ? (
          <p className="text-sm text-emerald-100/60">Loading…</p>
        ) : (
          <MatchStatusSections
            matches={matches ?? []}
            groupUpcomingByDate
            emptyTitle="No group matches yet"
          />
        )}
      </section>
    </div>
  );
}
