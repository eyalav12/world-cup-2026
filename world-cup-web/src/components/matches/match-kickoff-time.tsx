"use client";

import { useEffect, useState } from "react";
import type { MatchDto } from "@/lib/api/types";
import {
  formatMatchDateTimeInZone,
  getBrowserTimeZone,
} from "@/lib/match-time";

type Props = {
  match: MatchDto;
  className?: string;
};

/**
 * Formats kickoff in the visitor's local timezone.
 * Renders after mount so SSR (UTC) and browser stay in sync — no 19:00 vs 22:00 split.
 */
export function MatchKickoffTime({ match, className }: Props) {
  const [text, setText] = useState<string | null>(null);

  useEffect(() => {
    setText(
      formatMatchDateTimeInZone(match.matchDate, getBrowserTimeZone()),
    );
  }, [match.matchDate]);

  return (
    <span className={className} suppressHydrationWarning>
      {text ?? "…"}
    </span>
  );
}
