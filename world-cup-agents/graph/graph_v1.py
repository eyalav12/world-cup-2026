from typing import TypedDict, Annotated
from langgraph.graph import StateGraph,END,START
from langgraph.graph.message import add_messages
from agents.bets_agent.bets_agent import bets_agent, invoke_bets_agent
from agents.news_agent.news_agent import news_agent, invoke_news_agent
from agents.matches_agent.agent import agent, invoke_agent_with_create_agent

class Plan(TypedDict):
    run_matches:bool
    run_news:bool
    run_odds:bool
    intent:str
    team_a:str | None
    team_b:str | None
    team_name:str | None
    group_name:str | None
    match_date:str | None
    match_id:str | None

class GraphState(TypedDict):
    messages: Annotated[list, add_messages]
    user_query:str
    plan: Plan
    matches_result: str | None
    news_result: str | None
    odds_result: str | None
    final_answer: str | None

def run_router_plan_agent(graph_state:GraphState)->GraphState:
    pass

def run_matches_agent(graph_state:GraphState)->GraphState:
    print("inside matches agent")
    matches_result = invoke_agent_with_create_agent(graph_state["user_query"])
    graph_state["matches_result"] = matches_result
    return graph_state

def run_news_agent(graph_state:GraphState)->GraphState:
    print("inside news agent")
    news_result = invoke_news_agent(graph_state["user_query"])
    graph_state["news_result"] = news_result
    return graph_state

def run_odds_agent(graph_state:GraphState)->GraphState:
    print("inside odds agent")
    odds_result = invoke_bets_agent(graph_state["user_query"])
    graph_state["odds_result"] = odds_result
    return graph_state

def run_synthesizer_agent(state_graph:StateGraph)->GraphState:
    pass



def build_graph(user_query: str):
    builder = StateGraph(GraphState)
    builder.add_node("run_router_plan_agent",run_router_plan_agent)
    builder.add_node("run_matches_agent",run_matches_agent)
    builder.add_node("run_news_agent",run_news_agent)
    builder.add_node("run_odds_agent",run_odds_agent)
    builder.add_node("run_synthesizer_agent",run_synthesizer_agent)

    builder.add_edge(START,"run_router_plan_agent")
    builder.add_conditional_edges("run_router_plan_agent",["run_matches_agent","run_news_agent","run_odds_agent"])
    builder.add_conditional_edges("run_matches_agent","run_synthesizer_agent")
    builder.add_conditional_edges("run_news_agent","run_synthesizer_agent")
    builder.add_conditional_edges("run_odds_agent","run_synthesizer_agent")
    builder.add_conditional_edges("run_synthesizer_agent",END)

    graph = builder.compile()
    graph.invoke({"user_query":user_query})









