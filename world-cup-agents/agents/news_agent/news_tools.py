from utils.url_builder_util import tool_caller
from langchain.tools import tool


@tool
def get_general_world_cup_news():
    """Fetch the latest general World Cup 2026 headlines from ESPN (tournament-wide, not team-specific).

    Use when the user asks for:
    - latest / breaking / general World Cup news
    - tournament headlines without naming a specific team

    Do NOT use for team-specific questions — use get_news_by_team_name instead.

    Returns JSON from the backend: headlines with headline, description, publishedAt (url is in data but do not repeat urls in your answer to the user).
    """
    return tool_caller("agent/espn/news/generalNewsSummary")


@tool
def get_news_by_team_name(team_name: str):
    """Fetch the latest ESPN headlines for one national team.

    Use when the user asks about:
    - news, injuries, squad updates, or stories about a single team

    For matchups or predictions involving two teams (e.g. "England vs Croatia"):
    - call this tool TWICE — once with team_name for each side
    - combine both results in the answer (see system prompt format)

    team_name must match backend team names exactly where possible:
    England, Croatia, Brazil, Argentina, France, United States, Mexico, etc.
    If no results, try the official full name (e.g. USA → United States).

    Returns JSON from the backend: headlines with headline, description, publishedAt (url is in data but do not repeat urls in your answer to the user).
    """
    return tool_caller("agent/espn/news/byTeamName", teamName=team_name)
