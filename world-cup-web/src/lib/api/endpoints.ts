import { format } from "date-fns";
import { getApiBaseUrl } from "./config";
import { apiFetch, ApiError, readJsonResponse } from "./client";
import type {
  CleanedTournamentOdds,
  GroupStandingRow,
  HistoryMatchData,
  MatchDto,
  MatchLineupsDto,
  MatchResultsContainer,
  NewsResponse,
  OddsSummaryDTO,
  PlayerDto,
  TeamsByGroup,
  UserLeaderBoardDto,
} from "./types";

export function getTeamsByGroups() {
  return apiFetch<TeamsByGroup>("/teams/getTeamsByGroups");
}

export function getTeamsByGroupName(groupName: string) {
  return apiFetch<string[]>(
    `/teams/getTeamsByGroupName?groupName=${encodeURIComponent(groupName)}`,
  );
}

export function getTeamSquad(teamName: string) {
  return apiFetch<PlayerDto[]>(
    `/teams/getTeamSquad?teamName=${encodeURIComponent(teamName)}`,
  );
}

export function getMatchesByDate(fromDate: Date, toDate?: Date, status?: string) {
  const from = format(fromDate, "yyyy-MM-dd");
  const params = new URLSearchParams({ fromDate: from });
  if (toDate) params.set("toDate", format(toDate, "yyyy-MM-dd"));
  if (status) params.set("status", status);
  return apiFetch<MatchDto[]>(`/matches/byDate?${params}`);
}

export function getMatchesByTeamName(
  teamName: string,
  options?: { status?: string; limit?: number },
) {
  const params = new URLSearchParams({ teamName });
  if (options?.status) params.set("status", options.status);
  if (options?.limit != null) params.set("limit", String(options.limit));
  return apiFetch<MatchDto[]>(`/matches/byTeamName?${params}`);
}

export function getMatchesByGroupName(
  groupName: string,
  options?: { status?: string; limit?: number },
) {
  const params = new URLSearchParams({ groupName });
  if (options?.status) params.set("status", options.status);
  if (options?.limit != null) params.set("limit", String(options.limit));
  return apiFetch<MatchDto[]>(`/matches/byGroupName?${params}`);
}

export function getMatchByMatchId(matchId: number) {
  return fetchNullable<MatchDto>(`/matches/byMatchId?matchId=${matchId}`, 120);
}

export function getRecentFinishedMatches(limit = 10) {
  return fetchNullable<MatchDto[]>(`/matches/recent?limit=${limit}`, 120);
}

/** Returns null when lineups are not published yet (backend 204). */
export function getMatchLineups(matchId: number) {
  return fetchNullable<MatchLineupsDto>(
    `/matches/lineups?matchId=${matchId}`,
    120,
  );
}

export async function getOddsSummary(matchId: number) {
  const data = await fetchNullable<OddsSummaryDTO>(
    `/odds/oddsSummaryByMatchId?matchId=${matchId}`,
  );
  return data ?? {};
}

export function getTeamLastMatches(teamName: string) {
  return apiFetch<HistoryMatchData[]>(
    `/history/data/teamLastMatches?teamName=${encodeURIComponent(teamName)}`,
  );
}

export function getTeamLastMatchesFromApi(teamName: string, limit = 5) {
  return apiFetch<MatchDto[]>(
    `/history/data/teamLastMatchesApi?teamName=${encodeURIComponent(teamName)}&limit=${limit}`,
  );
}

/** Finished 2026 WC games — Football API first, then DB fallback. */
export async function getTeamTournamentResults(
  teamName: string,
  limit = 6,
): Promise<MatchDto[]> {
  const [fromApi, fromDb] = await Promise.all([
    getTeamLastMatchesFromApi(teamName, 20).catch(() => [] as MatchDto[]),
    getMatchesByTeamName(teamName, { status: "FINISHED", limit }).catch(
      () => [] as MatchDto[],
    ),
  ]);

  const wcFinished = fromApi.filter(
    (m) =>
      m.status === "FINISHED" &&
      m.competition?.toLowerCase().includes("world cup"),
  );

  const seen = new Set<number>();
  const merged: MatchDto[] = [];
  for (const m of [...wcFinished, ...fromDb]) {
    if (m.matchId == null || seen.has(m.matchId)) continue;
    seen.add(m.matchId);
    merged.push(m);
  }
  return merged.slice(0, limit);
}

/** Live + upcoming fixtures, with match-window fallback when byTeamName is empty. */
export async function getTeamUpcomingFixtures(teamName: string): Promise<MatchDto[]> {
  const [live, timed] = await Promise.all([
    getMatchesByTeamName(teamName, { status: "IN_PLAY" }).catch(() => [] as MatchDto[]),
    getMatchesByTeamName(teamName, { status: "TIMED" }).catch(() => [] as MatchDto[]),
  ]);
  const combined = [...live, ...timed];
  if (combined.length > 0) return combined;

  const { fetchMatchWindow } = await import("@/lib/matches");
  const window = await fetchMatchWindow(21);
  return window.filter(
    (m) =>
      (m.status === "TIMED" || m.status === "IN_PLAY") &&
      (m.homeTeam === teamName || m.awayTeam === teamName),
  );
}

export function getHeadToHead(teamA: string, teamB: string) {
  return apiFetch<HistoryMatchData[]>(
    `/history/data/headToHead?teamA=${encodeURIComponent(teamA)}&teamB=${encodeURIComponent(teamB)}`,
  );
}

export function getTeamsLastMatches(teamA: string, teamB: string) {
  return apiFetch<MatchResultsContainer<HistoryMatchData>>(
    `/history/data/teamsLastMatches?teamA=${encodeURIComponent(teamA)}&teamB=${encodeURIComponent(teamB)}`,
  );
}

export function getGlobalLeaderboard(page = 0, size = 20) {
  return apiFetch<UserLeaderBoardDto[]>(
    `/leaderBoard/global?page=${page}&size=${size}`,
  );
}

/** Returns null when Redis cache is empty (backend 204). */
async function fetchNullable<T>(path: string, _revalidate = 300): Promise<T | null> {
  const base = getApiBaseUrl();
  const url = `${base}${path}`;

  const res = await fetch(url, {
    headers: { Accept: "application/json" },
    cache: "no-store",
  });

  if (res.status === 204) return null;

  if (!res.ok) {
    let message = res.statusText;
    try {
      const body = await readJsonResponse<{ message?: string }>(res);
      if (body?.message) message = body.message;
    } catch (error) {
      if (error instanceof ApiError) message = error.message;
    }
    throw new ApiError(message, res.status);
  }

  return readJsonResponse<T>(res);
}

export function getGroupWinnerOdds(groupId: string) {
  const groupName = toPolymarketGroupKey(groupId);
  return fetchNullable<CleanedTournamentOdds>(
    `/polymarketodds/groupWinnerOdds?groupName=${encodeURIComponent(groupName)}`,
    900,
  );
}

/** stage: round-of-16 | quarterfinals | semifinals | final */
export function getAdvancementOdds(stage: string) {
  return fetchNullable<CleanedTournamentOdds>(
    `/polymarketodds/advancementOdds?stage=${encodeURIComponent(stage)}`,
    900,
  );
}

/** All group tables (flat list). Empty array if none synced yet. */
export async function getAllStandings(): Promise<GroupStandingRow[]> {
  const data = await fetchNullable<GroupStandingRow[]>("/standings/all", 120);
  return data ?? [];
}

/** Standings for one group (GROUP_A, Group A, etc.). */
export async function getStandingsByGroup(groupId: string): Promise<GroupStandingRow[]> {
  const data = await fetchNullable<GroupStandingRow[]>(
    `/standings/byGroup?group=${encodeURIComponent(groupId)}`,
    120,
  );
  return data ?? [];
}

/** Matches Redis / Polymarket cache key format (e.g. GROUP_A → group_a). */
export function toPolymarketGroupKey(groupId: string): string {
  return groupId.toLowerCase().replace(/\s+/g, "-");
}

export function getTournamentWinnerOdds() {
  return fetchNullable<CleanedTournamentOdds>(
    "/polymarketodds/tournamentWinnerOdds",
    900,
  );
}

/** Returns null when Redis cache is empty (backend 204). */
export function getTopScorerOdds() {
  return fetchNullable<CleanedTournamentOdds>(
    "/polymarketodds/topScorerOdds",
    900,
  );
}

/** Returns null when cache empty or endpoint not deployed yet. */
export function getTeamPolymarketProps(teamName: string) {
  return fetchNullableOptional<CleanedTournamentOdds>(
    `/polymarketodds/team?teamName=${encodeURIComponent(teamName)}`,
    900,
  );
}

async function fetchNullableOptional<T>(
  path: string,
  _revalidate = 300,
): Promise<T | null> {
  const base = getApiBaseUrl();
  const url = `${base}${path}`;

  const res = await fetch(url, {
    headers: { Accept: "application/json" },
    cache: "no-store",
  });

  if (res.status === 204 || res.status === 404) return null;

  if (!res.ok) {
    let message = res.statusText;
    try {
      const body = await readJsonResponse<{ message?: string }>(res);
      if (body?.message) message = body.message;
    } catch (error) {
      if (error instanceof ApiError) message = error.message;
    }
    throw new ApiError(message, res.status);
  }

  return readJsonResponse<T>(res);
}

/** Returns null when Redis cache is empty (backend 204). */
async function fetchNews(path: string): Promise<NewsResponse | null> {
  return fetchNullable<NewsResponse>(path);
}

export function getGeneralNews() {
  return fetchNews("/espn/news/generalNews");
}

export function getNewsByTeamName(teamName: string) {
  return fetchNews(
    `/espn/news/byTeamName?teamName=${encodeURIComponent(teamName)}`,
  );
}
