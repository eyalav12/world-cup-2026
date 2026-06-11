"use client";

import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { toUserMessage } from "@/lib/api/client";
import { getGlobalLeaderboard } from "@/lib/api/endpoints";
import { EmptyState } from "@/components/ui/empty-state";
import { ErrorBanner } from "@/components/ui/error-banner";

const PAGE_SIZE = 25;

export function LeaderboardTable() {
  const [page, setPage] = useState(0);

  const { data, isLoading, error, isFetching } = useQuery({
    queryKey: ["leaderboard", "global", page],
    queryFn: () => getGlobalLeaderboard(page, PAGE_SIZE),
    placeholderData: (prev) => prev,
  });

  const leaders = data ?? [];
  const rankOffset = page * PAGE_SIZE;
  const hasPrev = page > 0;
  const hasNext = leaders.length === PAGE_SIZE;

  return (
    <div>
      {error ? (
        <ErrorBanner
          message={toUserMessage(error, "Could not load leaderboard.")}
        />
      ) : null}

      <div className="overflow-hidden rounded-2xl border border-white/10">
        {leaders.length === 0 && !isLoading && !error ? (
          <EmptyState
            title="No scores yet"
            description="The leaderboard will fill in once players start making predictions and matches are played."
          />
        ) : (
          <table className="w-full text-left text-sm">
            <thead className="bg-white/5 text-emerald-100/60">
              <tr>
                <th className="px-4 py-3 font-medium">#</th>
                <th className="px-4 py-3 font-medium">Player</th>
                <th className="px-4 py-3 font-medium text-right">
                  Correct bets
                </th>
              </tr>
            </thead>
            <tbody>
              {leaders.map((row, i) => (
                <tr
                  key={`${row.name}-${rankOffset + i}`}
                  className="border-t border-white/10 text-white"
                >
                  <td className="px-4 py-3 tabular-nums text-emerald-100/50">
                    {rankOffset + i + 1}
                  </td>
                  <td className="px-4 py-3 font-medium">{row.name}</td>
                  <td className="px-4 py-3 text-right font-semibold tabular-nums text-emerald-300">
                    {row.totalScore}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {leaders.length > 0 ? (
        <div className="mt-4 flex items-center justify-between gap-4">
          <p className="text-sm text-emerald-100/50">
            {isFetching ? "Loading…" : `Showing ${rankOffset + 1}–${rankOffset + leaders.length}`}
          </p>
          <div className="flex gap-2">
            <button
              type="button"
              disabled={!hasPrev || isFetching}
              onClick={() => setPage((p) => Math.max(0, p - 1))}
              className="rounded-full border border-white/15 px-4 py-1.5 text-sm text-white hover:bg-white/10 disabled:cursor-not-allowed disabled:opacity-40"
            >
              Previous
            </button>
            <button
              type="button"
              disabled={!hasNext || isFetching}
              onClick={() => setPage((p) => p + 1)}
              className="rounded-full border border-white/15 px-4 py-1.5 text-sm text-white hover:bg-white/10 disabled:cursor-not-allowed disabled:opacity-40"
            >
              Next
            </button>
          </div>
        </div>
      ) : null}
    </div>
  );
}
