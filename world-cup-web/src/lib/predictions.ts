import type { CleanedMarket, CleanedTournamentOdds } from "./api/types";

/** Extract team name from "Will Spain win the 2026 FIFA World Cup?" */
export function parseTeamFromQuestion(question: string): string | null {
  const match = question.match(/^Will\s+(.+?)\s+win\b/i);
  return match?.[1]?.trim() ?? null;
}

/** Extract player or team name from Polymarket "Will X win …?" / "Will X reach …?" questions. */
export function parseSubjectFromQuestion(question: string): string {
  const winMatch = question.match(/^Will\s+(.+?)\s+win\b/i);
  if (winMatch?.[1]) return winMatch[1].trim();

  const reachMatch = question.match(/^Will\s+(.+?)\s+reach\b/i);
  if (reachMatch?.[1]) return reachMatch[1].trim();

  const scoreMatch = question.match(/^Will\s+(.+?)\s+score\b/i);
  if (scoreMatch?.[1]) return scoreMatch[1].trim();

  return question;
}

export function yesPercent(market: CleanedMarket): number {
  return market.outcomePrices[0] ?? 0;
}

export function sortMarketsByYesPct(markets: CleanedMarket[]): CleanedMarket[] {
  return [...markets].sort((a, b) => yesPercent(b) - yesPercent(a));
}

export function topWinnerMarkets(
  data: CleanedTournamentOdds | null,
  limit = 15,
): { title: string; markets: CleanedMarket[] } | null {
  if (!data?.markets?.length) return null;
  return {
    title: data.title,
    markets: sortMarketsByYesPct(data.markets).slice(0, limit),
  };
}

export function formatPredictionPct(value: number): string {
  if (Number.isNaN(value)) return "—";
  return `${value.toFixed(1)}%`;
}

/** Keep only markets for teams in the tournament (matches /teams list). */
export function filterMarketsByQualifiedTeams(
  markets: CleanedMarket[],
  qualifiedTeams: string[],
): CleanedMarket[] {
  const qualified = new Set(qualifiedTeams);
  return markets.filter((market) => {
    const team = parseTeamFromQuestion(market.question);
    return team != null && qualified.has(team);
  });
}

export function filterTournamentOddsByTeams(
  data: CleanedTournamentOdds | null,
  qualifiedTeams: string[],
): CleanedTournamentOdds | null {
  if (!data) return null;
  return {
    ...data,
    markets: filterMarketsByQualifiedTeams(data.markets, qualifiedTeams),
  };
}

export function flattenTeamsFromGroups(
  teamsByGroup: Record<string, string[]>,
): string[] {
  return Object.values(teamsByGroup).flat();
}
