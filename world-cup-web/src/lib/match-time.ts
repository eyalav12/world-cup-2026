import { parseISO } from "date-fns";

/** IANA timezone from the browser (e.g. Asia/Jerusalem). Client-only. */
export function getBrowserTimeZone(): string {
  return Intl.DateTimeFormat().resolvedOptions().timeZone;
}

/** Calendar date yyyy-MM-dd for grouping fixtures in a given timezone. */
export function matchCalendarDayKey(iso: string, timeZone: string): string {
  const date = parseISO(iso);
  return new Intl.DateTimeFormat("en-CA", {
    timeZone,
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  }).format(date);
}

/** Format kickoff like "Thu, Jun 11 · 19:00" in the given IANA timezone. */
export function formatMatchDateTimeInZone(
  iso: string,
  timeZone: string,
): string {
  try {
    const date = parseISO(iso);
    const parts = new Intl.DateTimeFormat("en-US", {
      timeZone,
      weekday: "short",
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
      hour12: false,
    }).formatToParts(date);

    const pick = (type: Intl.DateTimeFormatPartTypes) =>
      parts.find((p) => p.type === type)?.value ?? "";

    const hour = pick("hour").padStart(2, "0");
    const minute = pick("minute").padStart(2, "0");

    return `${pick("weekday")}, ${pick("month")} ${pick("day")} · ${hour}:${minute}`;
  } catch {
    return iso;
  }
}
