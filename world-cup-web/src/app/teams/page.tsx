import Link from "next/link";
import { TeamCrest } from "@/components/teams/team-crest";
import { ErrorBanner } from "@/components/ui/error-banner";
import { getTeamsByGroups } from "@/lib/api/endpoints";
import { ApiError } from "@/lib/api/client";
import { formatGroupId, sortGroupIds } from "@/lib/teams";
import { slugifyTeamName } from "@/lib/utils";

export const metadata = { title: "Teams" };

export const dynamic = "force-dynamic";

export default async function TeamsPage() {
  let teamsByGroup: Record<string, string[]> = {};
  let error: string | null = null;

  try {
    teamsByGroup = await getTeamsByGroups();
  } catch (e) {
    error =
      e instanceof ApiError
        ? e.message
        : "Could not load teams. Please try again later.";
  }

  const groups = sortGroupIds(Object.keys(teamsByGroup));

  return (
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
      <h1 className="text-3xl font-bold text-white">Teams</h1>
      <p className="mt-2 text-emerald-100/70">
        All 48 nations competing at World Cup 2026, grouped by draw.
      </p>

      {error ? (
        <div className="mt-6">
          <ErrorBanner message={error} />
        </div>
      ) : null}

      <div className="mt-8 space-y-10">
        {groups.map((groupId) => (
          <section key={groupId}>
            <h2 className="mb-4 text-xl font-semibold text-white">
              {formatGroupId(groupId)}
            </h2>
            <div className="grid gap-3 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
              {(teamsByGroup[groupId] ?? []).map((team) => (
                <Link
                  key={team}
                  href={`/teams/${slugifyTeamName(team)}`}
                  className="flex items-center gap-3 rounded-2xl border border-white/10 bg-white/5 px-4 py-3 hover:border-emerald-400/30"
                >
                  <TeamCrest teamName={team} size={40} />
                  <span className="font-medium text-white">{team}</span>
                </Link>
              ))}
            </div>
          </section>
        ))}
      </div>
    </div>
  );
}
