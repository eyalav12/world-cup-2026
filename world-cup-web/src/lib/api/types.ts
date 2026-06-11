export type MatchResult = "HOME_TEAM" | "AWAY_TEAM" | "DRAW";

export interface MatchDto {
  matchId: number;
  homeTeam: string;
  awayTeam: string;
  matchDate: string;
  stage: string;
  competition: string;
  status: string;
  score: string | null;
  result: MatchResult | null;
}

export interface LineupPlayer {
  id?: number | null;
  name: string;
  position?: string | null;
  shirtNumber?: number | null;
}

export interface MatchLineupsDto {
  matchId: number;
  homeTeam: string;
  awayTeam: string;
  homeStarting: LineupPlayer[];
  awayStarting: LineupPlayer[];
  homeBench: LineupPlayer[];
  awayBench: LineupPlayer[];
}

export interface PlayerDto {
  name: string;
  position: string;
}

export type TeamsByGroup = Record<string, string[]>;

export interface SportsbookOdds {
  bookmaker: string;
  homeOdds: number;
  homePct: number;
  drawOdds: number;
  drawPct: number;
  awayOdds: number;
  awayPct: number;
}

export interface OddsSummaryDTO {
  matchId?: number;
  marketAverage?: Record<string, number>;
  marketExtremes?: Record<string, unknown>;
  topSportsbooks?: SportsbookOdds[];
}

export interface HistoryMatchData {
  homeTeamName: string;
  awayTeamName: string;
  homeTeamCode?: string;
  awayTeamCode?: string;
  matchDate: string;
  stageName?: string;
  score?: string;
  result?: MatchResult;
  stadiumName?: string;
  cityName?: string;
  tournamentName?: string;
}

export interface MatchResultsContainer<T> {
  homeTeamResults: T[];
  awayTeamResults: T[];
}

export interface UserLeaderBoardDto {
  name: string;
  totalScore: number;
}

export interface GroupStandingRow {
  id?: number;
  groupName: string;
  position: number;
  teamId: number;
  name: string;
  crest?: string | null;
  playedGames: number;
  form?: string | null;
  won: number;
  draw: number;
  lost: number;
  points: number;
  goalsFor: number;
  goalsAgainst: number;
  goalsDifference: number;
}

export interface ApiErrorBody {
  message?: string;
  status?: number;
  timestamp?: string;
}

export interface NewsSource {
  id?: string | null;
  name?: string | null;
}

export interface NewsArticle {
  title: string;
  description?: string | null;
  url: string;
  publishedAt?: string | null;
  urlToImage?: string | null;
  source?: NewsSource | null;
}

export interface NewsResponse {
  status?: string;
  articles: NewsArticle[];
}

export interface CleanedMarket {
  question: string;
  outcomePrices: number[];
}

export interface CleanedTournamentOdds {
  title: string;
  markets: CleanedMarket[];
}
