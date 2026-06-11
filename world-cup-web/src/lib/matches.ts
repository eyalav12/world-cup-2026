import { addDays, format, isSameDay, parseISO } from "date-fns";
import type { MatchDto } from "./api/types";
import { getMatchWindowStart } from "./tournament";

export function parseMatchDate(match: MatchDto): Date {
  return parseISO(match.matchDate);
}

export function formatMatchDateTime(match: MatchDto): string {
  try {
    return format(parseMatchDate(match), "EEE, MMM d · HH:mm");
  } catch {
    return match.matchDate;
  }
}

export function formatMatchDateShort(match: MatchDto): string {
  try {
    return format(parseMatchDate(match), "MMM d");
  } catch {
    return match.matchDate;
  }
}

export function matchStatusLabel(status: string): string {
  switch (status) {
    case "TIMED":
      return "Upcoming";
    case "IN_PLAY":
      return "Live";
    case "FINISHED":
      return "Finished";
    case "POSTPONED":
      return "Postponed";
    default:
      return status.replace(/_/g, " ");
  }
}

export function isLiveMatch(match: MatchDto): boolean {
  return match.status === "IN_PLAY";
}

export function isFinishedMatch(match: MatchDto): boolean {
  return match.status === "FINISHED";
}

export function sortMatchesChronologically(matches: MatchDto[]): MatchDto[] {
  return [...matches].sort(
    (a, b) => parseMatchDate(a).getTime() - parseMatchDate(b).getTime(),
  );
}

export function groupMatchesByDay(matches: MatchDto[]): Map<string, MatchDto[]> {
  const sorted = sortMatchesChronologically(matches);
  const map = new Map<string, MatchDto[]>();

  for (const match of sorted) {
    const key = format(parseMatchDate(match), "yyyy-MM-dd");
    const list = map.get(key) ?? [];
    list.push(match);
    map.set(key, list);
  }

  return map;
}

export async function fetchUpcomingWindow(daysAhead = 14): Promise<MatchDto[]> {
  const { getMatchesByDate } = await import("./api/endpoints");
  const windowStart = getMatchWindowStart();
  const all: MatchDto[] = [];

  for (let i = 0; i <= daysAhead; i++) {
    const day = addDays(windowStart, i);
    try {
      const dayMatches = await getMatchesByDate(day);
      all.push(...dayMatches);
    } catch {
      /* day may have no matches */
    }
  }

  const seen = new Set<number>();
  return sortMatchesChronologically(
    all.filter((m) => {
      if (seen.has(m.matchId)) return false;
      seen.add(m.matchId);
      return m.status === "TIMED" || m.status === "IN_PLAY";
    }),
  );
}

export function filterMatchesForDay(matches: MatchDto[], day: Date): MatchDto[] {
  return matches.filter((m) => isSameDay(parseMatchDate(m), day));
}
