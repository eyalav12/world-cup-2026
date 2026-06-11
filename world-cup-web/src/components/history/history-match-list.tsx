import type { HistoryMatchData } from "@/lib/api/types";
import { TeamCrest } from "@/components/teams/team-crest";

export function HistoryMatchList({
  matches,
  title,
}: {
  matches: HistoryMatchData[];
  title?: string;
}) {
  if (matches.length === 0) {
    return (
      <p className="text-sm text-emerald-100/50">No historical matches found.</p>
    );
  }

  return (
    <div>
      {title ? (
        <h4 className="mb-3 text-sm font-semibold text-emerald-200/80">
          {title}
        </h4>
      ) : null}
      <ul className="space-y-2">
        {matches.map((m, i) => (
          <li
            key={`${m.matchDate}-${m.homeTeamName}-${m.awayTeamName}-${i}`}
            className="flex items-center gap-3 rounded-xl border border-white/10 bg-white/5 px-3 py-2 text-sm"
          >
            <div className="flex items-center gap-1.5 min-w-0 flex-1">
              <TeamCrest teamName={m.homeTeamName} size={24} />
              <span className="truncate text-white">{m.homeTeamName}</span>
              <span className="shrink-0 font-semibold tabular-nums text-emerald-300">
                {m.score ?? "–"}
              </span>
              <span className="truncate text-white">{m.awayTeamName}</span>
              <TeamCrest teamName={m.awayTeamName} size={24} />
            </div>
            <span className="shrink-0 text-xs text-emerald-100/50">
              {m.matchDate}
            </span>
          </li>
        ))}
      </ul>
    </div>
  );
}
