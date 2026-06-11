/** ISO 3166-1 alpha-2 for flagcdn; extend as DB team names appear. */
const TEAM_ISO: Record<string, string> = {
  Argentina: "ar",
  Australia: "au",
  Austria: "at",
  Belgium: "be",
  Brazil: "br",
  Canada: "ca",
  Chile: "cl",
  Colombia: "co",
  Croatia: "hr",
  Denmark: "dk",
  Ecuador: "ec",
  Egypt: "eg",
  England: "gb-eng",
  France: "fr",
  Germany: "de",
  Ghana: "gh",
  Iran: "ir",
  Italy: "it",
  Japan: "jp",
  Mexico: "mx",
  Morocco: "ma",
  Netherlands: "nl",
  Nigeria: "ng",
  Norway: "no",
  Poland: "pl",
  Portugal: "pt",
  Qatar: "qa",
  "Republic of Ireland": "ie",
  "Saudi Arabia": "sa",
  Scotland: "gb-sct",
  Senegal: "sn",
  Serbia: "rs",
  Spain: "es",
  Switzerland: "ch",
  Tunisia: "tn",
  Uruguay: "uy",
  USA: "us",
  "United States": "us",
  Wales: "gb-wls",
};

/** football-data.org crest when you expose teamId from backend */
const TEAM_FOOTBALL_DATA_ID: Record<string, number> = {};

export function getTeamFlagUrl(teamName: string): string | null {
  const code = TEAM_ISO[teamName];
  if (!code) return null;
  return `https://flagcdn.com/w80/${code}.png`;
}

export function getTeamCrestUrl(teamName: string): string | null {
  const id = TEAM_FOOTBALL_DATA_ID[teamName];
  if (!id) return null;
  return `https://crests.football-data.org/${id}.png`;
}

export function getTeamImageUrl(teamName: string): string | null {
  return getTeamCrestUrl(teamName) ?? getTeamFlagUrl(teamName);
}

export function formatGroupId(groupId: string): string {
  const match = groupId.match(/^GROUP_([A-Z])$/i);
  if (match) return `Group ${match[1]}`;
  return groupId.replace(/_/g, " ");
}

export function sortGroupIds(groups: string[]): string[] {
  return [...groups].sort((a, b) => a.localeCompare(b));
}
