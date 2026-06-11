import os
from dotenv import load_dotenv
from langchain_core.messages import HumanMessage
from langchain.agents import create_agent
from langchain_openai import ChatOpenAI
from agents.bets_agent.bets_agent_prompts import BETS_SYSTEM_PROMPT
from agents.bets_agent.bets_tools import (
    get_match_odds_by_team_names,
    get_match_odds_summary,
    get_tournament_winner_odds,
    get_top_scorer_odds,
    get_group_winner_odds,
    get_advancement_odds,
)

load_dotenv()

os.environ["OPENAI_API_KEY"] = os.getenv("OPENAI_API_KEY")
model = ChatOpenAI(model="gpt-4.1")

TOOLS = [
    get_match_odds_by_team_names,
    get_match_odds_summary,
    get_tournament_winner_odds,
    get_top_scorer_odds,
    get_group_winner_odds,
    get_advancement_odds,
]

bets_agent = create_agent(
    model=model,
    tools=TOOLS,
    system_prompt=BETS_SYSTEM_PROMPT,
)


def invoke_bets_agent(content: str):
    result = bets_agent.invoke({"messages": [HumanMessage(content=content)]})
    final_messages = result["messages"]
    last_message = final_messages[-1]
    print(last_message)
    return last_message.content or ""
