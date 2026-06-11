import Link from "next/link";
import { MatchCard } from "@/components/matches/match-card";
import { TeamCrest } from "@/components/teams/team-crest";
import { GroupsExplorer } from "@/components/groups/groups-explorer";
import { ErrorBanner } from "@/components/ui/error-banner";
import { EmptyState } from "@/components/ui/empty-state";
import { getTeamsByGroups } from "@/lib/api/endpoints";
import { toUserMessage } from "@/lib/api/client";
import { formatGroupId, sortGroupIds } from "@/lib/teams";
import { slugifyTeamName } from "@/lib/utils";

export const metadata = { title: "Groups" };

type Props = { searchParams: Promise<{ group?: string }> };

export default async function GroupsPage({ searchParams }: Props) {
  const { group: selectedGroup } = await searchParams;
  let teamsByGroup: Record<string, string[]> = {};
  let error: string | null = null;

  try {
    teamsByGroup = await getTeamsByGroups();
  } catch (e) {
    error = toUserMessage(e, "Could not load groups. Please try again later.");
  }

  const groups = sortGroupIds(Object.keys(teamsByGroup));

  return (
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
      <h1 className="text-3xl font-bold text-white">Groups</h1>
      <p className="mt-2 text-emerald-100/70">
        Teams per group, standings, fixtures, and group winner markets.
      </p>

      {error ? (
        <div className="mt-6">
          <ErrorBanner message={error} />
        </div>
      ) : null}

      {groups.length === 0 && !error ? (
        <div className="mt-8">
          <EmptyState title="Groups not available yet" />
        </div>
      ) : null}

      <div className="mt-6 flex flex-wrap gap-2">
        {groups.map((g) => (
          <Link
            key={g}
            href={`/groups?group=${encodeURIComponent(g)}`}
            className={
              selectedGroup === g
                ? "rounded-full bg-emerald-500 px-4 py-1.5 text-sm font-semibold text-[#0b1f14]"
                : "rounded-full border border-white/15 px-4 py-1.5 text-sm text-white hover:bg-white/10"
            }
          >
            {formatGroupId(g)}
          </Link>
        ))}
      </div>

      {selectedGroup && teamsByGroup[selectedGroup] ? (
        <section className="mt-8">
          <h2 className="mb-4 text-xl font-semibold text-white">
            {formatGroupId(selectedGroup)} teams
          </h2>
          <div className="flex flex-wrap gap-3">
            {teamsByGroup[selectedGroup].map((team) => (
              <Link
                key={team}
                href={`/teams/${slugifyTeamName(team)}`}
                className="flex items-center gap-2 rounded-full border border-white/10 bg-white/5 px-3 py-1.5 hover:border-emerald-400/30"
              >
                <TeamCrest teamName={team} size={28} />
                <span className="text-sm text-white">{team}</span>
              </Link>
            ))}
          </div>
        </section>
      ) : null}

      {groups.length > 0 ? (
        <div className="mt-10">
          <GroupsExplorer
            groups={groups}
            initialGroup={selectedGroup ?? groups[0]}
          />
        </div>
      ) : null}
    </div>
  );
}
