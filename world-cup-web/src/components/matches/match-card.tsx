import Link from "next/link";
import { TeamCrest } from "@/components/teams/team-crest";
import { Badge } from "@/components/ui/badge";
import type { MatchDto } from "@/lib/api/types";
import {
  formatMatchDateTime,
  isFinishedMatch,
  isLiveMatch,
  matchStatusLabel,
} from "@/lib/matches";

export function MatchCard({ match }: { match: MatchDto }) {
  const live = isLiveMatch(match);
  const finished = isFinishedMatch(match);

  return (
    <Link
      href={`/matches/${match.matchId}`}
      className="group block rounded-2xl border border-white/10 bg-white/5 p-4 transition hover:border-emerald-400/40 hover:bg-white/[0.08]"
    >
      <div className="mb-3 flex items-center justify-between gap-2">
        <Badge
          variant={live ? "live" : finished ? "finished" : "upcoming"}
        >
          {matchStatusLabel(match.status)}
        </Badge>
        <span className="text-xs text-emerald-100/50">
          {formatMatchDateTime(match)}
        </span>
      </div>

      <div className="flex items-center justify-between gap-3">
        <div className="flex min-w-0 flex-1 items-center gap-2">
          <TeamCrest teamName={match.homeTeam} size={36} />
          <span className="truncate font-medium text-white">
            {match.homeTeam ?? "TBD"}
          </span>
        </div>
        <div className="shrink-0 px-2 text-center">
          {finished || live ? (
            <span className="text-lg font-bold tabular-nums text-white">
              {match.score ?? "–"}
            </span>
          ) : (
            <span className="text-sm text-emerald-200/60">vs</span>
          )}
        </div>
        <div className="flex min-w-0 flex-1 items-center justify-end gap-2">
          <span className="truncate text-right font-medium text-white">
            {match.awayTeam ?? "TBD"}
          </span>
          <TeamCrest teamName={match.awayTeam} size={36} />
        </div>
      </div>

      <p className="mt-3 text-xs text-emerald-100/50">
        {(match.stage ?? "Match").replace(/_/g, " ")} · {match.competition}
      </p>
    </Link>
  );
}
