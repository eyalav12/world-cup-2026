"""Enhanced tools — composite matchup fetch + improved HTTP helper.

Does not replace agents/tools.py; import from here in agent_v2 only.
"""

import os
from concurrent.futures import ThreadPoolExecutor
from urllib.parse import urlencode

import requests
from langchain.tools import tool

from agents.matches_agent.tools import (
    get_matches_by_date,
    get_matches_by_group,
    get_matches_by_team_name,
    get_standings_by_group,
)

BACKEND_BASE_URL = os.getenv("BACKEND_API_URL", "http://localhost:8083")


def tool_caller_v2(path: str, **params) -> dict | list:
    """GET helper with URL-encoded query params and explicit errors."""
    try:
        query = urlencode({k: v for k, v in params.items() if v is not None})
        url = f"{BACKEND_BASE_URL}/{path}?{query}" if query else f"{BACKEND_BASE_URL}/{path}"
        response = requests.get(url, timeout=60)
        if response.status_code == 200:
            return response.json()
        return {"error": f"Service returned {response.status_code}"}
    except Exception as e:
        return {"error": str(e)}


@tool
def get_matchup_analysis(teamA: str, teamB: str) -> dict:
    """Compare two teams for predictions, form, or head-to-head.

    ALWAYS use this tool when the user asks about:
    - who will win / prediction between two teams
    - comparing two teams
    - form + head-to-head together
    - match preview between teamA and teamB

    Fetches in one parallel call:
    - World Cup head-to-head history
    - Last World Cup tournament matches for each team (historical DB)
    - Recent form from football-data API for each team

    Team names must match API names (e.g. England, Croatia, Brazil, USA).
    """
    tasks = {
        "headToHeadWorldCup": ("history/data/headToHead", {"teamA": teamA, "teamB": teamB}),
        "lastMatchesWorldCupHistory": (
            "history/data/teamsLastMatches",
            {"teamA": teamA, "teamB": teamB},
        ),
        "recentFormFootballData": (
            "history/data/teamsLastMatchesApi",
            {"teamA": teamA, "teamB": teamB},
        ),
    }

    results: dict = {"teamA": teamA, "teamB": teamB}

    def fetch(item):
        key, (path, params) = item
        return key, tool_caller_v2(path, **params)

    with ThreadPoolExecutor(max_workers=3) as executor:
        for key, data in executor.map(fetch, tasks.items()):
            results[key] = data

    return results


# Re-export existing tools so agent_v2 can use one TOOLS list without duplicating definitions.
MATCHUP_FIXTURE_TOOLS = [
    get_matchup_analysis,
    get_matches_by_date,
    get_matches_by_group,
    get_matches_by_team_name,
    get_standings_by_group,
]
