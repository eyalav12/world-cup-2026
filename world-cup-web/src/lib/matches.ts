import { addDays, parseISO } from "date-fns";
import type { MatchDto } from "./api/types";
import { getMatchWindowStart, TOURNAMENT_TIMEZONE } from "./tournament";

const tournamentDayFormatter = new Intl.DateTimeFormat("en-CA", {
  timeZone: TOURNAMENT_TIMEZONE,
  year: "numeric",
  month: "2-digit",
  day: "2-digit",
});

const tournamentDatePartFormatter = new Intl.DateTimeFormat("en-US", {
  timeZone: TOURNAMENT_TIMEZONE,
  weekday: "short",
  month: "short",
  day: "numeric",
});

const tournamentTimeFormatter = new Intl.DateTimeFormat("en-US", {
  timeZone: TOURNAMENT_TIMEZONE,
  hour: "2-digit",
  minute: "2-digit",
  hour12: false,
});

const tournamentDateShortFormatter = new Intl.DateTimeFormat("en-US", {
  timeZone: TOURNAMENT_TIMEZONE,
  month: "short",
  day: "numeric",
});

export function parseMatchDate(match: MatchDto): Date {
  return parseISO(match.matchDate);
}

/** yyyy-MM-dd in tournament timezone (matches backend byDate bucketing). */
export function getMatchTournamentDayKey(match: MatchDto): string {
  try {
    return tournamentDayFormatter.format(parseMatchDate(match));
  } catch {
    const raw = match.matchDate.trim();
    return raw.length >= 10 ? raw.slice(0, 10) : raw;
  }
}

export function formatMatchDateTime(match: MatchDto): string {
  try {
    const d = parseMatchDate(match);
    return `${tournamentDatePartFormatter.format(d)} · ${tournamentTimeFormatter.format(d)}`;
  } catch {
    return match.matchDate;
  }
}

export function formatMatchDateShort(match: MatchDto): string {
  try {
    return tournamentDateShortFormatter.format(parseMatchDate(match));
  } catch {
    return match.matchDate;
  }
}

export function formatMatchCalendarDate(match: MatchDto): string {
  return getMatchTournamentDayKey(match);
}

export function formatTournamentDayLabel(dayKey: string | null | undefined): string {
  if (!dayKey) return "Date TBD";
  try {
    const [y, m, d] = dayKey.split("-").map(Number);
    const date = new Date(Date.UTC(y, m - 1, d, 12, 0, 0));
    return new Intl.DateTimeFormat("en-US", {
      timeZone: TOURNAMENT_TIMEZONE,
      weekday: "short",
      month: "short",
      day: "numeric",
    }).format(date);
  } catch {
    return dayKey;
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

export function isUpcomingMatch(match: MatchDto): boolean {
  return match.status === "TIMED";
}

/** Split by status; preserve API order (backend sorts before responding). */
export function partitionMatchesByStatus(matches: MatchDto[]) {
  return {
    live: matches.filter(isLiveMatch),
    upcoming: matches.filter(isUpcomingMatch),
    finished: matches.filter(isFinishedMatch),
  };
}

export function groupMatchesByDay(matches: MatchDto[]): Map<string, MatchDto[]> {
  const map = new Map<string, MatchDto[]>();

  for (const match of matches) {
    const key = getMatchTournamentDayKey(match);
    const list = map.get(key) ?? [];
    list.push(match);
    map.set(key, list);
  }

  return map;
}

export function groupUpcomingByTournamentDay(
  matches: MatchDto[],
): Map<string, MatchDto[]> {
  const map = new Map<string, MatchDto[]>();
  for (const match of matches) {
    const key = getMatchTournamentDayKey(match);
    const list = map.get(key) ?? [];
    list.push(match);
    map.set(key, list);
  }
  return map;
}

export function filterMatchesForTournamentDay(
  matches: MatchDto[],
  dayKey: string,
): MatchDto[] {
  return matches.filter((m) => getMatchTournamentDayKey(m) === dayKey);
}

/** @deprecated use filterMatchesForTournamentDay */
export function filterMatchesForDay(matches: MatchDto[], day: Date): MatchDto[] {
  const key = tournamentDayFormatter.format(day);
  return filterMatchesForTournamentDay(matches, key);
}

function dedupeMatches(matches: MatchDto[]): MatchDto[] {
  const seen = new Set<number>();
  return matches.filter((m) => {
    if (seen.has(m.matchId)) return false;
    seen.add(m.matchId);
    return true;
  });
}

function isRenderableMatch(match: MatchDto): boolean {
  return Boolean(match.homeTeam?.trim() && match.awayTeam?.trim());
}

export async function fetchMatchWindow(daysAhead = 14): Promise<MatchDto[]> {
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

  return dedupeMatches(all).filter(isRenderableMatch);
}

export async function fetchUpcomingWindow(daysAhead = 14): Promise<MatchDto[]> {
  const all = await fetchMatchWindow(daysAhead);
  return all.filter((m) => m.status === "TIMED" || m.status === "IN_PLAY");
}

export async function fetchHomeMatchSections(daysAhead = 10) {
  const { getRecentFinishedMatches } = await import("./api/endpoints");
  const [windowMatches, recentFinished] = await Promise.all([
    fetchMatchWindow(daysAhead),
    getRecentFinishedMatches(6).catch(() => [] as MatchDto[] | null),
  ]);

  const { live, upcoming } = partitionMatchesByStatus(windowMatches);
  const justFinished = recentFinished ?? [];

  return { live, upcoming, justFinished };
}
