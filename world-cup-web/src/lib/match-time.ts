import { parseISO } from "date-fns";

/**
 * Timezone used to display kickoffs in the UI.
 * Override with NEXT_PUBLIC_DISPLAY_TIMEZONE (e.g. Asia/Jerusalem).
 */
export function getDisplayTimeZone(): string {
  const override = process.env.NEXT_PUBLIC_DISPLAY_TIMEZONE?.trim();
  if (override) return override;

  if (typeof window === "undefined") return "UTC";

  const langs = navigator.languages?.length
    ? navigator.languages
    : [navigator.language];
  if (langs.some((l) => l.startsWith("he") || l.includes("-IL"))) {
    return "Asia/Jerusalem";
  }

  return Intl.DateTimeFormat().resolvedOptions().timeZone;
}

/** @deprecated use getDisplayTimeZone */
export function getBrowserTimeZone(): string {
  return getDisplayTimeZone();
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

/** Format kickoff like "Sun, Jun 29 · 23:30 GMT+3" in the given IANA timezone. */
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
      timeZoneName: "short",
    }).formatToParts(date);

    const pick = (type: Intl.DateTimeFormatPartTypes) =>
      parts.find((p) => p.type === type)?.value ?? "";

    const hour = pick("hour").padStart(2, "0");
    const minute = pick("minute").padStart(2, "0");
    const tz = pick("timeZoneName");

    const base = `${pick("weekday")}, ${pick("month")} ${pick("day")} · ${hour}:${minute}`;
    return tz ? `${base} ${tz}` : base;
  } catch {
    return iso;
  }
}

export function formatKickoffForDisplay(iso: string): string {
  return formatMatchDateTimeInZone(iso, getDisplayTimeZone());
}
