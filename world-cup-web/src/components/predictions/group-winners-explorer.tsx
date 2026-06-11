"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { TournamentWinnerPanel } from "@/components/predictions/tournament-winner-panel";
import { getGroupWinnerOdds } from "@/lib/api/endpoints";
import { formatGroupId } from "@/lib/teams";
import { EmptyState } from "@/components/ui/empty-state";
import { cn } from "@/lib/utils";

export function GroupWinnersExplorer({ groups }: { groups: string[] }) {
  const [selected, setSelected] = useState(groups[0]);

  const { data, isLoading, isError } = useQuery({
    queryKey: ["group-winner-odds", selected],
    queryFn: () => getGroupWinnerOdds(selected!),
    enabled: Boolean(selected),
  });

  if (groups.length === 0) {
    return <EmptyState title="No groups available" />;
  }

  return (
    <div>
      <div className="mb-6 flex flex-wrap gap-2">
        {groups.map((g) => (
          <button
            key={g}
            type="button"
            onClick={() => setSelected(g)}
            className={cn(
              "rounded-full px-4 py-1.5 text-sm font-medium transition-colors",
              selected === g
                ? "bg-amber-500/90 text-[#0b1f14]"
                : "border border-white/15 text-emerald-100/80 hover:bg-white/10",
            )}
          >
            {formatGroupId(g)}
          </button>
        ))}
      </div>

      {isLoading ? (
        <p className="text-sm text-emerald-100/60">Loading group winner markets…</p>
      ) : null}

      {isError ? (
        <EmptyState
          title="Could not load group markets"
          description="Check that the backend is running and the Polymarket sync has completed."
        />
      ) : null}

      {!isLoading && !isError ? (
        <TournamentWinnerPanel data={data ?? null} limit={4} />
      ) : null}
    </div>
  );
}
