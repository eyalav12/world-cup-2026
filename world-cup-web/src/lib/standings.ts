import type { GroupStandingRow } from "@/lib/api/types";

export function groupStandingsByGroup(
  rows: GroupStandingRow[],
): Map<string, GroupStandingRow[]> {
  const map = new Map<string, GroupStandingRow[]>();

  for (const row of rows) {
    const list = map.get(row.groupName) ?? [];
    list.push(row);
    map.set(row.groupName, list);
  }

  for (const [group, list] of map) {
    map.set(
      group,
      [...list].sort(
        (a, b) => a.position - b.position || b.points - a.points,
      ),
    );
  }

  return map;
}

export function formatGoalDifference(value: number | null | undefined): string {
  if (value == null) return "—";
  if (value > 0) return `+${value}`;
  return String(value);
}

export function formatStandingNumber(value: number | null | undefined): string {
  if (value == null) return "—";
  return String(value);
}

/** e.g. "W,W,D,L,W" → "W W D L W" */
export function formatStandingForm(form: string | null | undefined): string {
  if (!form?.trim()) return "—";
  return form.replace(/,/g, " ").trim();
}
