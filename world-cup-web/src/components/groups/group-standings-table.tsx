import Link from "next/link";
import type { GroupStandingRow } from "@/lib/api/types";
import { formatGoalDifference, formatStandingForm, formatStandingNumber } from "@/lib/standings";
import { TeamCrest } from "@/components/teams/team-crest";
import { EmptyState } from "@/components/ui/empty-state";
import { slugifyTeamName } from "@/lib/utils";

export function GroupStandingsTable({
  rows,
  compact = false,
}: {
  rows: GroupStandingRow[];
  compact?: boolean;
}) {
  if (rows.length === 0) {
    return (
      <EmptyState
        title="Standings not available yet"
        description="Group tables appear after the first standings sync."
      />
    );
  }

  const sorted = [...rows].sort(
    (a, b) => a.position - b.position || b.points - a.points,
  );

  return (
    <div className="overflow-x-auto rounded-2xl border border-white/10 bg-white/[0.03]">
      <table className="w-full min-w-[32rem] text-left text-sm">
        <thead>
          <tr className="border-b border-white/10 text-xs uppercase tracking-wider text-emerald-100/50">
            <th className="px-3 py-3 font-medium">#</th>
            <th className="px-3 py-3 font-medium">Team</th>
            <th className="px-2 py-3 text-center font-medium">P</th>
            <th className="hidden px-2 py-3 text-center font-medium sm:table-cell">
              W
            </th>
            <th className="hidden px-2 py-3 text-center font-medium sm:table-cell">
              D
            </th>
            <th className="hidden px-2 py-3 text-center font-medium sm:table-cell">
              L
            </th>
            {!compact ? (
              <>
                <th className="hidden px-2 py-3 text-center font-medium md:table-cell">
                  GF
                </th>
                <th className="hidden px-2 py-3 text-center font-medium md:table-cell">
                  GA
                </th>
                <th className="px-2 py-3 text-center font-medium">GD</th>
                <th className="hidden px-2 py-3 text-center font-medium lg:table-cell">
                  Form
                </th>
              </>
            ) : null}
            <th className="px-3 py-3 text-center font-medium">Pts</th>
          </tr>
        </thead>
        <tbody>
          {sorted.map((row) => (
            <tr
              key={`${row.groupName}-${row.teamId}`}
              className="border-b border-white/5 last:border-0"
            >
              <td className="px-3 py-3 tabular-nums text-emerald-100/70">
                {formatStandingNumber(row.position)}
              </td>
              <td className="px-3 py-3">
                <Link
                  href={`/teams/${slugifyTeamName(row.name)}`}
                  className="flex items-center gap-2 font-medium text-white hover:text-emerald-300"
                >
                  <TeamCrest teamName={row.name} size={24} />
                  <span className="truncate">{row.name}</span>
                </Link>
              </td>
              <td className="px-2 py-3 text-center tabular-nums text-emerald-100/80">
                {formatStandingNumber(row.playedGames)}
              </td>
              <td className="hidden px-2 py-3 text-center tabular-nums sm:table-cell">
                {formatStandingNumber(row.won)}
              </td>
              <td className="hidden px-2 py-3 text-center tabular-nums sm:table-cell">
                {formatStandingNumber(row.draw)}
              </td>
              <td className="hidden px-2 py-3 text-center tabular-nums sm:table-cell">
                {formatStandingNumber(row.lost)}
              </td>
              {!compact ? (
                <>
                  <td className="hidden px-2 py-3 text-center tabular-nums md:table-cell">
                    {formatStandingNumber(row.goalsFor)}
                  </td>
                  <td className="hidden px-2 py-3 text-center tabular-nums md:table-cell">
                    {formatStandingNumber(row.goalsAgainst)}
                  </td>
                  <td className="px-2 py-3 text-center tabular-nums">
                    {formatGoalDifference(row.goalsDifference)}
                  </td>
                  <td className="hidden px-2 py-3 text-center tabular-nums text-xs tracking-wide lg:table-cell">
                    {formatStandingForm(row.form)}
                  </td>
                </>
              ) : null}
              <td className="px-3 py-3 text-center tabular-nums font-semibold text-white">
                {formatStandingNumber(row.points)}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
