from langchain.tools import tool
from utils.url_builder_util import tool_caller
import requests



@tool
def get_head_to_head_from_all_db_history(teamA: str, teamB: str):
    """get the head to head results of teamA against teamB from data base history that includes all time world cup results"""
    # response = requests.get(f"http://localhost:8083/history/data/headToHead?teamA={teamA}&teamB={teamB}")
    # data = response.json()
    # return data
    return tool_caller("history/data/headToHead",teamA=teamA,teamB=teamB)

@tool
def get_teams_last_matches_from_all_db_history(teamA:str, teamB:str):
    """get the last matches results for each of the teams, from data base history, that includes all time world cup results. returns a reponse of combined results for both teams, that is seperated independent. sorted by time of the matches"""
    # response = requests.get(f"http://localhost:8083/history/data/teamsLastMatches?teamA={teamA}&teamB={teamB}")
    # data = response.json()
    # return data
    return tool_caller("history/data/teamsLastMatches",teamA=teamA,teamB=teamB)

@tool
def get_teams_last_matches_football_data_api(teamA:str, teamB:str):
    """get the last matches results for each of the teams, from football data api. sorted by time of the matches, and gives it in this way"""
    # response = requests.get(f"http://localhost:8083/history/data/teamsLastMatchesApi?teamA={teamA}&teamB={teamB}")
    # data = response.json()
    # return data
    return tool_caller("history/data/teamsLastMatchesApi",teamA=teamA,teamB=teamB)

@tool
def get_teams_last_matches_results_from_current_tournament(teamA:str, teamB:str):
    """get the last matches results from this 2026 world cup tournament, that played so far. for each of the two teams"""
    response = requests.get(f"http://localhost:8083/history/data/teamsLastMatchesApi?teamA={teamA}&teamB={teamB}")
    data = response.json()
    return data

@tool
def get_matches_by_date(date:str):
    """get upcoming, live, or end matches, that is playing, played, or will play in the given date.
    The date argument must strictly use YYYY-MM-DD format"""
    # response = requests.get(f"http://localhost:8083/matches/byDate={date}")
    # data = response.json()
    # return data
    return tool_caller("matches/byDate",fromDate=date)

@tool
def get_matches_by_group(group:str):
    """get upcoming matches that is realted to teams in this group"""
    # response = requests.get(f"http://localhost:8083/matches/byGroupName={group}")
    # data = response.json()
    # return data
    return tool_caller("matches/byGroupName",groupName=group)

@tool
def get_matches_by_team_name(team:str):
    """get upcoming matches that is realted to the given team"""
    # response = requests.get(f"http://localhost:8083/matches/byGroupName={team}")
    # data = response.json()
    # return data
    return tool_caller("matches/byTeamName",teamName=team)


@tool
def get_standings_by_group(group:str):
    """get standings of the give group. includes rankings, points, and all data related to teams in this group, in this tournament"""
    # response = requests.get(f"http://localhost:8083/standings/byGroup={group}")
    # data = response.json()
    # return data
    return tool_caller("standings/byGroup",group=group)
