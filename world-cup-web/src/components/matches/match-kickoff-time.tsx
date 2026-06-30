"use client";

import { useSyncExternalStore } from "react";
import type { MatchDto } from "@/lib/api/types";
import { formatKickoffForDisplay } from "@/lib/match-time";

type Props = {
  match: MatchDto;
  className?: string;
};

function subscribe() {
  return () => {};
}

/** Kickoff in local / configured timezone — not US Eastern tournament time. */
export function MatchKickoffTime({ match, className }: Props) {
  const text = useSyncExternalStore(
    subscribe,
    () => formatKickoffForDisplay(match.matchDate),
    () => null,
  );

  return (
    <span className={className} suppressHydrationWarning>
      {text ?? "…"}
    </span>
  );
}
