from utils.url_builder_util import tool_caller
from langchain.tools import tool


@tool
def get_match_odds_by_team_names(team_a: str, team_b: str, match_date: str = ""):
    """Fetch sportsbook odds for a match between two teams by name (preferred for user questions).

    Use when the user asks about:
    - odds or lines for a fixture (e.g. England vs Croatia)
    - who is favored in a match between two named teams
    - betting prices when team names are given but not match id

    team_a and team_b are order-independent (England + Croatia = Croatia + England).
    Team names must match the backend (England, Croatia, Brazil, United States, etc.).

    match_date is optional, format YYYY-MM-DD. Use when the user specifies a date or multiple fixtures exist.
    If omitted, returns the next upcoming TIMED match between the two teams.

    Returns JSON: market averages, extremes, and top sportsbooks. Error if match or odds not found.
    """
    params = {"teamA": team_a, "teamB": team_b}
    if match_date and match_date.strip():
        params["matchDate"] = match_date.strip()
    return tool_caller("odds/oddsSummaryByTeamNames", **params)


@tool
def get_match_odds_summary(match_id: int):
    """Fetch sportsbook odds when you already have the football-data match id.

    Prefer get_match_odds_by_team_names when the user gives team names instead of match id.

    match_id is the football-data match id (same as MatchDto.matchId in fixtures), not the internal DB id.

    Returns JSON: market averages, extremes, and top sportsbooks for that match.
    """
    return tool_caller("odds/oddsSummaryByMatchId", matchId=str(match_id))


@tool
def get_tournament_winner_odds():
    """Fetch Polymarket odds for which team wins the World Cup 2026.

    Use when the user asks about:
    - tournament winner / outright winner markets
    - which team is favorite to win the World Cup
    - championship betting prices (prediction market)
    """
    return tool_caller("polymarketodds/tournamentWinnerOdds")


@tool
def get_top_scorer_odds():
    """Fetch Polymarket odds for the World Cup top / golden boot scorer market.

    Use when the user asks about:
    - top scorer betting
    - golden boot favorites
    - which player is favored to score the most goals in the tournament
    """
    return tool_caller("polymarketodds/topScorerOdds")


@tool
def get_group_winner_odds(group_name: str):
    """Fetch Polymarket odds for which team wins a group.

    Use when the user asks about:
    - group winner markets (e.g. who wins Group A)
    - group stage outright winner prices

    group_name examples: group_a, group_b (lowercase with underscore, as stored in backend cache).
    """
    return tool_caller("polymarketodds/groupWinnerOdds", groupName=group_name)


@tool
def get_advancement_odds(stage: str):
    """Fetch Polymarket odds for teams to reach a knockout stage.

    Use when the user asks about:
    - odds to reach round of 16, quarterfinals, semifinals, or final
    - "will X reach the knockout stage" style markets

    stage must be one of: round-of-16, quarterfinals, semifinals, final
    """
    return tool_caller("polymarketodds/advancementOdds", stage=stage)
