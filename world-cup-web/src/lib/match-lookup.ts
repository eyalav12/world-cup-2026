import { addDays } from "date-fns";
import { getMatchesByDate } from "./api/endpoints";
import type { MatchDto } from "./api/types";
import { TOURNAMENT_START } from "./tournament";

export async function findMatchById(
  matchId: number,
  daysAround = 60,
): Promise<MatchDto | null> {
  const start = TOURNAMENT_START;
  for (let i = 0; i <= daysAround; i++) {
    const day = addDays(start, i);
    try {
      const matches = await getMatchesByDate(day);
      const hit = matches.find((m) => m.matchId === matchId);
      if (hit) return hit;
    } catch {
      /* continue */
    }
  }
  return null;
}
