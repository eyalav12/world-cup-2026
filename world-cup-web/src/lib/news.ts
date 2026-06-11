import { format, parseISO } from "date-fns";
import type { NewsArticle } from "@/lib/api/types";

export function formatNewsDate(publishedAt: string | null | undefined): string {
  if (!publishedAt) return "";
  try {
    return format(parseISO(publishedAt), "MMM d, yyyy · HH:mm");
  } catch {
    return publishedAt;
  }
}

/** Extra terms for NewsAPI headlines that don't use DB team names. */
const TEAM_NEWS_ALIASES: Record<string, string[]> = {
  "United States": [
    "USMNT",
    "U.S. men's",
    "United States men's",
    "United States men",
    "USA national team",
  ],
  Canada: ["Canadian national team", "Canada men's", "CANMNT"],
  Mexico: ["El Tri", "Mexico national team", "Mexican national team"],
  "Korea Republic": ["South Korea", "Korean national team"],
  "Côte d'Ivoire": ["Ivory Coast"],
  "IR Iran": ["Iran national team"],
};

const HOST_NATIONS = new Set(["United States", "Canada", "Mexico"]);

const TITLE_EXCLUDE_PATTERNS = [
  /how to (watch|live stream)/i,
  /\blive stream\b/i,
  /\btv channel\b/i,
  /sticker album/i,
  /power rankings/i,
  /fantasy sports/i,
];

function containsTerm(text: string, term: string): boolean {
  return text.toLowerCase().includes(term.toLowerCase());
}

function isExcludedTitle(title: string, teamName: string): boolean {
  if (TITLE_EXCLUDE_PATTERNS.some((re) => re.test(title))) return true;

  if (teamName === "United States") {
    if (/mexico and canada/i.test(title) && !/usmnt/i.test(title)) return true;
    if (/choos(e|ing|en).*(usa|united states).*host/i.test(title)) return true;
    if (/host.*(usa|united states).*mexico/i.test(title)) return true;
  }

  return false;
}

function articleMatchesTeam(article: NewsArticle, teamName: string): boolean {
  const title = article.title ?? "";
  const description = article.description ?? "";

  if (isExcludedTitle(title, teamName)) return false;

  const aliases = TEAM_NEWS_ALIASES[teamName] ?? [];
  const strongTerms = aliases.length > 0 ? aliases : [teamName];
  const allTerms = [teamName, ...aliases];

  if (HOST_NATIONS.has(teamName)) {
    if (strongTerms.some((t) => containsTerm(title, t))) return true;
    if (containsTerm(title, teamName) && !/mexico|canada/i.test(title)) {
      return true;
    }
    if (strongTerms.some((t) => containsTerm(description, t))) return true;
    return false;
  }

  return allTerms.some(
    (t) => containsTerm(title, t) || containsTerm(description, t),
  );
}

/** Client-side cleanup until backend post-filter is in place. Falls back to raw feed if empty. */
export function filterNewsForTeam(
  teamName: string,
  articles: NewsArticle[],
  limit = 6,
): NewsArticle[] {
  const filtered = articles.filter((a) => articleMatchesTeam(a, teamName));
  const pool = filtered.length > 0 ? filtered : articles;
  return pool.slice(0, limit);
}
