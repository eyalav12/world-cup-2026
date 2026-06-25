"use client";

import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { MatchCard } from "@/components/matches/match-card";
import { getMatchesByDate } from "@/lib/api/endpoints";
import { filterMatchesForTournamentDay } from "@/lib/matches";
import {
  getDefaultMatchPickerDate,
  TOURNAMENT_START_ISO,
} from "@/lib/tournament";
import { EmptyState } from "@/components/ui/empty-state";
import { ErrorBanner } from "@/components/ui/error-banner";

export function MatchesDateExplorer() {
  const [date, setDate] = useState(getDefaultMatchPickerDate);

  const { data, isLoading, error } = useQuery({
    queryKey: ["matches", date],
    queryFn: () => getMatchesByDate(new Date(`${date}T12:00:00`)),
  });

  // Order comes from the API (backend sorts by status, then kickoff time).
  const dayMatches = data ? filterMatchesForTournamentDay(data, date) : [];

  return (
    <div>
      <label className="mb-4 flex flex-col gap-2 sm:flex-row sm:items-center">
        <span className="text-sm font-medium text-emerald-200/80">
          Pick a date
        </span>
        <input
          type="date"
          value={date}
          min={TOURNAMENT_START_ISO}
          onChange={(e) => setDate(e.target.value)}
          className="w-full max-w-xs rounded-xl border border-white/15 bg-white/10 px-4 py-2 text-white outline-none focus:border-emerald-400/50 sm:w-auto"
        />
      </label>
      <p className="mb-4 text-xs text-emerald-100/45">
        Dates and kickoff times shown in US Eastern (tournament time).
      </p>

      {error ? (
        <ErrorBanner
          message={
            error instanceof Error
              ? error.message
              : "Failed to load matches for this date."
          }
        />
      ) : null}

      {isLoading ? (
        <p className="text-sm text-emerald-100/60">Loading matches…</p>
      ) : null}

      {!isLoading && !error && dayMatches.length === 0 ? (
        <EmptyState
          title="No matches on this day"
          description="Try another date during the tournament. Group stage begins 11 June 2026."
        />
      ) : null}

      {dayMatches.length > 0 ? (
        <div className="mt-6 grid gap-4 sm:grid-cols-2">
          {dayMatches.map((m) => (
            <MatchCard key={m.matchId} match={m} />
          ))}
        </div>
      ) : null}
    </div>
  );
}
