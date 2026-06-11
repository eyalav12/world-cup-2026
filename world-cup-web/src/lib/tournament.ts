import { format, startOfDay } from "date-fns";

/** First day of group-stage fixtures (local noon avoids TZ edge cases). */
export const TOURNAMENT_START = startOfDay(new Date("2026-06-11T12:00:00"));

export const TOURNAMENT_START_ISO = "2026-06-11";

/** Earliest date to show when browsing or fetching upcoming fixtures. */
export function getMatchWindowStart(reference = new Date()): Date {
  const today = startOfDay(reference);
  return today < TOURNAMENT_START ? TOURNAMENT_START : today;
}

export function getDefaultMatchPickerDate(reference = new Date()): string {
  return format(getMatchWindowStart(reference), "yyyy-MM-dd");
}
