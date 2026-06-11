import Link from "next/link";
import { MatchCard } from "@/components/matches/match-card";
import { TeamCrest } from "@/components/teams/team-crest";
import { GroupsExplorer } from "@/components/groups/groups-explorer";
import { getTeamsByGroups } from "@/lib/api/endpoints";
import { formatGroupId, sortGroupIds } from "@/lib/teams";
import { slugifyTeamName } from "@/lib/utils";

export const metadata = { title: "Groups" };

type Props = { searchParams: Promise<{ group?: string }> };

export default async function GroupsPage({ searchParams }: Props) {
  const { group: selectedGroup } = await searchParams;
  const teamsByGroup = await getTeamsByGroups();
  const groups = sortGroupIds(Object.keys(teamsByGroup));

  return (
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
      <h1 className="text-3xl font-bold text-white">Groups</h1>
      <p className="mt-2 text-emerald-100/70">
        Teams per group, standings, fixtures, and group winner markets.
      </p>

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

      <div className="mt-10">
        <GroupsExplorer
          groups={groups}
          initialGroup={selectedGroup ?? groups[0]}
        />
      </div>
    </div>
  );
}
