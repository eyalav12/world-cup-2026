import type { MatchLineupsDto } from "@/lib/api/types";
import { TeamCrest } from "@/components/teams/team-crest";
import { EmptyState } from "@/components/ui/empty-state";
import { Card, CardTitle } from "@/components/ui/card";

function LineupColumn({
  teamName,
  starters,
  bench,
}: {
  teamName: string;
  starters: MatchLineupsDto["homeStarting"];
  bench: MatchLineupsDto["homeBench"];
}) {
  return (
    <div>
      <div className="mb-4 flex items-center gap-3">
        <TeamCrest teamName={teamName} size={36} />
        <h3 className="text-lg font-semibold text-white">{teamName}</h3>
      </div>

      <p className="mb-2 text-xs font-medium uppercase tracking-wide text-emerald-100/50">
        Starting XI
      </p>
      <ul className="space-y-2">
        {starters.map((player) => (
          <li
            key={`${player.id ?? player.name}-${player.shirtNumber}`}
            className="flex items-center justify-between rounded-xl border border-white/10 bg-white/[0.03] px-3 py-2 text-sm"
          >
            <span className="text-white">{player.name}</span>
            <span className="text-emerald-100/60">
              {player.shirtNumber != null ? `#${player.shirtNumber}` : ""}
              {player.position ? ` · ${player.position}` : ""}
            </span>
          </li>
        ))}
      </ul>

      {bench.length > 0 ? (
        <>
          <p className="mb-2 mt-5 text-xs font-medium uppercase tracking-wide text-emerald-100/50">
            Bench
          </p>
          <ul className="space-y-2">
            {bench.map((player) => (
              <li
                key={`bench-${player.id ?? player.name}-${player.shirtNumber}`}
                className="flex items-center justify-between rounded-xl border border-white/5 bg-white/[0.02] px-3 py-2 text-sm text-emerald-100/80"
              >
                <span>{player.name}</span>
                <span className="text-emerald-100/50">
                  {player.shirtNumber != null ? `#${player.shirtNumber}` : ""}
                </span>
              </li>
            ))}
          </ul>
        </>
      ) : null}
    </div>
  );
}

export function MatchLineupsPanel({
  lineups,
}: {
  lineups: MatchLineupsDto | null;
}) {
  if (!lineups) {
    return (
      <EmptyState
        title="Lineups not available yet"
        description="Confirmed lineups are usually published about an hour before kickoff."
      />
    );
  }

  const hasStarters =
    lineups.homeStarting.length > 0 || lineups.awayStarting.length > 0;

  if (!hasStarters) {
    return (
      <EmptyState
        title="Lineups not published yet"
        description="Check back closer to kickoff for the starting elevens."
      />
    );
  }

  return (
    <Card>
      <CardTitle className="mb-6">Lineups</CardTitle>
      <div className="grid gap-8 lg:grid-cols-2">
        <LineupColumn
          teamName={lineups.homeTeam}
          starters={lineups.homeStarting}
          bench={lineups.homeBench}
        />
        <LineupColumn
          teamName={lineups.awayTeam}
          starters={lineups.awayStarting}
          bench={lineups.awayBench}
        />
      </div>
    </Card>
  );
}
