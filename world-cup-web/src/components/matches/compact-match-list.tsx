import Link from "next/link";
import type { MatchDto } from "@/lib/api/types";
import { TeamCrest } from "@/components/teams/team-crest";
import { formatMatchCalendarDate, formatMatchDateTime } from "@/lib/matches";

type Props = {
  matches: MatchDto[];
  linkToDetail?: boolean;
  emptyMessage?: string;
  /** "datetime" shows kickoff time; "date" shows yyyy-MM-dd like history rows. */
  dateFormat?: "datetime" | "date";
};

export function CompactMatchList({
  matches,
  linkToDetail = false,
  emptyMessage = "No matches found.",
  dateFormat = "datetime",
}: Props) {
  if (matches.length === 0) {
    return <p className="text-sm text-emerald-100/50">{emptyMessage}</p>;
  }

  return (
    <ul className="space-y-2">
      {matches.map((m) => {
        const content = (
          <>
            <div className="flex min-w-0 flex-1 items-center gap-1.5">
              <TeamCrest teamName={m.homeTeam} size={24} />
              <span className="truncate text-white">{m.homeTeam}</span>
              <span className="shrink-0 font-semibold tabular-nums text-emerald-300">
                {m.score?.replace("-", "–") ?? "–"}
              </span>
              <span className="truncate text-white">{m.awayTeam}</span>
              <TeamCrest teamName={m.awayTeam} size={24} />
            </div>
            <div className="shrink-0 text-right text-xs text-emerald-100/50">
              <div>
                {dateFormat === "date"
                  ? formatMatchCalendarDate(m)
                  : formatMatchDateTime(m)}
              </div>
              {m.competition ? (
                <div className="truncate max-w-[10rem]">{m.competition}</div>
              ) : null}
            </div>
          </>
        );

        const className =
          "flex items-center gap-3 rounded-xl border border-white/10 bg-white/5 px-3 py-2 text-sm";

        if (linkToDetail && m.matchId) {
          return (
            <li key={m.matchId}>
              <Link
                href={`/matches/${m.matchId}`}
                className={`${className} transition hover:border-emerald-400/30 hover:bg-white/[0.08]`}
              >
                {content}
              </Link>
            </li>
          );
        }

        return (
          <li key={`${m.matchDate}-${m.homeTeam}-${m.awayTeam}`} className={className}>
            {content}
          </li>
        );
      })}
    </ul>
  );
}
