"""
Multi-agent LangGraph v2 — router → workers (0–3) → synthesizer.
- Nodes return partial dicts; LangGraph merges into state.
- Conditionals pick ONE next node per step; chaining runs 2–3 workers.
- messages + add_messages keeps session history across turns.
"""

from __future__ import annotations
import os
import re
from typing import Annotated, Literal
from dotenv import load_dotenv
from langchain_core.messages import AIMessage, BaseMessage, HumanMessage, SystemMessage
from langchain_openai import ChatOpenAI
from langgraph.checkpoint.memory import MemorySaver
from langgraph.graph import END, START, StateGraph
from langgraph.graph.message import add_messages
from pydantic import BaseModel, Field
from typing_extensions import TypedDict
from agents.bets_agent.bets_agent import invoke_bets_agent
from agents.matches_agent.agent import invoke_agent_with_create_agent
from agents.news_agent.news_agent import invoke_news_agent

load_dotenv()
os.environ.setdefault("OPENAI_API_KEY", os.getenv("OPENAI_API_KEY", ""))
_llm = ChatOpenAI(model="gpt-4.1")

class Plan(TypedDict, total=False):
    run_matches: bool
    run_news: bool
    run_odds: bool
    intent: str
    team_a: str | None
    team_b: str | None
    team_name: str | None
    group_name: str | None
    match_date: str | None
    match_id: str | None

class GraphState(TypedDict):
    messages: Annotated[list[BaseMessage], add_messages]
    user_query: str
    plan: Plan
    matches_result: str | None
    news_result: str | None
    odds_result: str | None
    final_answer: str | None

class PlanModel(BaseModel):
    run_matches: bool = Field(description="Fixtures, form, H2H, standings.")
    run_news: bool = Field(description="ESPN news.")
    run_odds: bool = Field(description="Odds / Polymarket.")
    intent: str = Field(description='prediction | fixtures | news | odds | general')
    team_a: str | None = None
    team_b: str | None = None
    team_name: str | None = None
    group_name: str | None = None
    match_date: str | None = None
    match_id: str | None = None


ROUTER_SYSTEM_PROMPT = """You are a routing planner for World Cup 2026. Do NOT answer the user.

Workers:
- matches: fixtures, form, H2H, standings
- news: ESPN news
- odds: match & tournament odds

Rules:
- Read the full conversation. Short follow-ups like "yes", "go ahead", "do it", "that one" mean continue the prior user request — inherit teams, dates, and intent from earlier turns.
- Greetings only when there is no prior task (exact hi/hello/hey) → all run_* false, intent=general
- Host countries, tournament dates, schedule, when does it start, where is it → run_matches=true
- prediction / preview / X vs Y → run_matches, run_news, run_odds all true unless user said "just odds", "news only", etc.
- User asks for matches on a date AND news AND prediction/odds → run all three workers
- fixtures / schedule → run_matches only (unless they also ask news/odds)
- news only → run_news; odds only → run_odds
- General World Cup questions with no need for live data → all run_* false, intent=general
- Extract team_a, team_b, team_name, group_name, match_date from the conversation (including prior turns).
- Dates like 11.6 or June 11 → match_date=2026-06-11
"""


SYNTHESIZER_WITH_DATA_PROMPT = """You are the World Cup 2026 assistant.

Combine the specialist summaries below into one clear reply. Use specialist data for fixtures, news, and odds.
Do not invent scores, headlines, or prices that are not in the summaries."""

SYNTHESIZER_GENERAL_PROMPT = """You are a helpful AI assistant for a World Cup 2026 app.

You specialize in football / World Cup 2026 — hosts (USA, Canada, Mexico), tournament dates (June–July 2026), teams, fixtures, predictions, news, and odds. Use tools data when provided in the conversation.

You can also answer general questions, creative requests (poems, jokes), and everyday chat using your normal knowledge and reasoning. Be friendly and concise.

Do not invent live match scores, news headlines, or betting odds unless specialist summaries were provided."""

def _plan(state: GraphState) -> Plan:
    return state.get("plan") or {}


def _message_label(msg: BaseMessage) -> str:
    if isinstance(msg, HumanMessage):
        return "User"
    if isinstance(msg, AIMessage):
        return "Assistant"
    return "System"


def _format_conversation(state: GraphState, *, max_messages: int = 12) -> str:
    messages = state.get("messages") or []
    lines: list[str] = []
    for msg in messages[-max_messages:]:
        content = getattr(msg, "content", "")
        if not content or not isinstance(content, str):
            continue
        lines.append(f"{_message_label(msg)}: {content.strip()}")
    return "\n".join(lines)


def _has_prior_turns(state: GraphState) -> bool:
    messages = state.get("messages") or []
    human_count = sum(1 for m in messages if isinstance(m, HumanMessage))
    return human_count > 1 or any(isinstance(m, AIMessage) for m in messages[:-1])


def _build_worker_prompt(state: GraphState) -> str:
    plan = _plan(state)
    conv = _format_conversation(state)
    lines: list[str] = []
    if conv:
        lines.append(f"Conversation so far:\n{conv}")
        lines.append("")
    lines.append(f"Current request: {state['user_query']}")
    if plan.get("team_a") and plan.get("team_b"):
        lines.append(f"Focus: {plan['team_a']} vs {plan['team_b']}.")
    elif plan.get("team_name"):
        lines.append(f"Focus: team {plan['team_name']}.")
    if plan.get("group_name"):
        lines.append(f"Focus: group {plan['group_name']}.")
    if plan.get("match_date"):
        lines.append(f"Focus: date {plan['match_date']}.")
    return "\n".join(lines)

def _first_worker(state: GraphState) -> Literal["matches", "news", "odds", "synthesizer"]:
    plan = _plan(state)
    if plan.get("run_matches"):
        return "matches"
    if plan.get("run_news"):
        return "news"
    if plan.get("run_odds"):
        return "odds"
    return "synthesizer"

def _after_matches(state: GraphState) -> Literal["news", "odds", "synthesizer"]:
    plan = _plan(state)
    if plan.get("run_news"):
        return "news"
    if plan.get("run_odds"):
        return "odds"
    return "synthesizer"

def _after_news(state: GraphState) -> Literal["odds", "synthesizer"]:
    if _plan(state).get("run_odds"):
        return "odds"
    return "synthesizer"

def _is_greeting_only(user_query: str) -> bool:
    q = user_query.strip().lower().rstrip("!.?")
    return q in {"hi", "hello", "hey", "yo", "hola"}


def _is_small_talk(user_query: str, *, has_history: bool) -> bool:
    """Only bare one-word greetings skip workers. 'hi how are you' uses the general LLM."""
    if has_history:
        return False
    return _is_greeting_only(user_query)


def _is_affirmative_follow_up(user_query: str) -> bool:
    q = user_query.strip().lower().rstrip("!.?")
    phrases = {
        "yes",
        "yeah",
        "yep",
        "sure",
        "ok",
        "okay",
        "please",
        "go ahead",
        "yes go ahead",
        "do it",
        "that one",
        "sounds good",
        "please do",
        "continue",
    }
    if q in phrases:
        return True
    return q.startswith(("yes ", "ok ", "sure ")) and len(q) < 48


def _conversation_text(state: GraphState) -> str:
    return _format_conversation(state).lower()


def _normalize_match_date(text: str) -> str | None:
    if re.search(r"\b11[\./]6\b|\bjune\s+11\b|\b11\s+june\b", text, re.I):
        return "2026-06-11"
    if re.search(r"\b12[\./]6\b|\bjune\s+12\b", text, re.I):
        return "2026-06-12"
    return None


def _apply_combined_intent_rules(plan: Plan, state: GraphState) -> Plan:
    user_query = state["user_query"]
    text = f"{user_query}\n{_conversation_text(state)}".lower()

    if any(x in text for x in ("just odds", "only odds", "news only", "just news")):
        return plan

    wants_prediction = any(
        w in text
        for w in (
            "prediction",
            "predict",
            "preview",
            "who wins",
            "who will win",
            "favored",
            "favorite",
            "betting",
        )
    )
    wants_news = "news" in text
    wants_odds = any(w in text for w in ("odds", "polymarket", "bookmaker"))
    wants_matches = (
        _should_fetch_matches(user_query)
        or wants_prediction
        or wants_news
        or wants_odds
        or "match" in text
        or "fixture" in text
    )

    match_date = _normalize_match_date(text)
    if match_date and not plan.get("match_date"):
        plan["match_date"] = match_date
        wants_matches = True

    if wants_prediction:
        plan["run_matches"] = True
        plan["run_news"] = True
        plan["run_odds"] = True
        plan["intent"] = "prediction"
    else:
        if wants_matches:
            plan["run_matches"] = True
        if wants_news:
            plan["run_news"] = True
        if wants_odds:
            plan["run_odds"] = True

    if _is_affirmative_follow_up(user_query) and _has_prior_turns(state):
        if wants_prediction or any(w in text for w in ("mexico", " vs ", " versus ", "south africa")):
            plan["run_matches"] = True
            plan["run_news"] = True
            plan["run_odds"] = True
            plan["intent"] = "prediction"

    return plan


def _should_fetch_matches(user_query: str) -> bool:
    q = user_query.lower()
    keywords = (
        "fixture",
        "schedule",
        "when does",
        "what date",
        "dates",
        "where is",
        "where's",
        "host",
        "tournament start",
        "take place",
        "held",
        "groups",
        "standings",
        "match on",
        "games on",
    )
    return any(k in q for k in keywords)


def _should_run_full_prediction(plan: Plan, state: GraphState) -> bool:
    user_query = state["user_query"]
    text = f"{user_query}\n{_conversation_text(state)}".lower()
    if any(x in text for x in ("just odds", "only odds", "news only", "just news")):
        return False
    if plan.get("intent") in ("prediction", "preview"):
        return True
    if plan.get("team_a") and plan.get("team_b") and any(
        word in text for word in ("predict", "preview", " vs ", " versus ", "who wins")
    ):
        return True
    if any(w in text for w in ("prediction", "predict", "preview", "who wins", "favored")):
        return True
    return False


def run_router_plan_agent(state: GraphState) -> dict:
    user_query = state["user_query"]
    has_history = _has_prior_turns(state)

    if _is_small_talk(user_query, has_history=has_history):
        return {
            "plan": {
                "run_matches": False,
                "run_news": False,
                "run_odds": False,
                "intent": "general",
            }
        }

    conv = _format_conversation(state)
    router_input = user_query
    if conv:
        router_input = f"Conversation so far:\n{conv}\n\nCurrent user message:\n{user_query}"
        if _is_affirmative_follow_up(user_query):
            router_input += (
                "\n\nNote: this is a short confirmation — continue the prior request "
                "and fill in teams/dates from earlier turns."
            )

    plan_model: PlanModel = _llm.with_structured_output(PlanModel).invoke(
        [SystemMessage(content=ROUTER_SYSTEM_PROMPT), HumanMessage(content=router_input)]
    )
    plan: Plan = plan_model.model_dump()
    if _should_run_full_prediction(plan, state):
        plan["run_matches"] = plan["run_news"] = plan["run_odds"] = True
    elif _should_fetch_matches(user_query):
        plan["run_matches"] = True
    plan = _apply_combined_intent_rules(plan, state)
    return {"plan": plan}

def run_matches_agent(state: GraphState) -> dict:
    if not _plan(state).get("run_matches"):
        return {}
    return {"matches_result": invoke_agent_with_create_agent(_build_worker_prompt(state))}

def run_news_agent(state: GraphState) -> dict:
    if not _plan(state).get("run_news"):
        return {}
    return {"news_result": invoke_news_agent(_build_worker_prompt(state))}

def run_odds_agent(state: GraphState) -> dict:
    if not _plan(state).get("run_odds"):
        return {}
    return {"odds_result": invoke_bets_agent(_build_worker_prompt(state))}

def run_synthesizer_agent(state: GraphState) -> dict:
    user_query = state["user_query"]
    plan = _plan(state)
    has_history = _has_prior_turns(state)
    conv = _format_conversation(state)

    if _is_small_talk(user_query, has_history=has_history):
        answer = (
            "Hello! I'm your World Cup 2026 assistant. "
            "Ask me about fixtures, match predictions, team news, or odds."
        )
        return {"final_answer": answer, "messages": [AIMessage(content=answer)]}

    ran_any = any(state.get(k) for k in ("matches_result", "news_result", "odds_result"))

    if ran_any:
        body = f"Conversation:\n{conv or '(none)'}\n\nCurrent user message: {user_query}\nIntent: {plan.get('intent', 'general')}\n"
        for title, key in [("Matches", "matches_result"), ("News", "news_result"), ("Odds", "odds_result")]:
            body += f"\n--- {title} ---\n{state.get(key) or '(not run)'}\n"
        answer = _llm.invoke(
            [SystemMessage(content=SYNTHESIZER_WITH_DATA_PROMPT), HumanMessage(content=body)]
        ).content or ""
    else:
        general_input = user_query
        if conv:
            general_input = f"Conversation so far:\n{conv}\n\nCurrent user message:\n{user_query}"
        answer = _llm.invoke(
            [
                SystemMessage(content=SYNTHESIZER_GENERAL_PROMPT),
                HumanMessage(content=general_input),
            ]
        ).content or ""

    return {"final_answer": answer, "messages": [AIMessage(content=answer)]}


_WORKER_PATHS = {
    "matches": "run_matches_agent",
    "news": "run_news_agent",
    "odds": "run_odds_agent",
    "synthesizer": "run_synthesizer_agent",
}


def build_graph():
    builder = StateGraph(GraphState)
    builder.add_node("run_router_plan_agent", run_router_plan_agent)
    builder.add_node("run_matches_agent", run_matches_agent)
    builder.add_node("run_news_agent", run_news_agent)
    builder.add_node("run_odds_agent", run_odds_agent)
    builder.add_node("run_synthesizer_agent", run_synthesizer_agent)
    builder.add_edge(START, "run_router_plan_agent")
    builder.add_conditional_edges("run_router_plan_agent", _first_worker, _WORKER_PATHS)
    builder.add_conditional_edges(
        "run_matches_agent",
        _after_matches,
        {"news": "run_news_agent", "odds": "run_odds_agent", "synthesizer": "run_synthesizer_agent"},
    )
    builder.add_conditional_edges(
        "run_news_agent",
        _after_news,
        {"odds": "run_odds_agent", "synthesizer": "run_synthesizer_agent"},
    )
    builder.add_edge("run_odds_agent", "run_synthesizer_agent")
    builder.add_edge("run_synthesizer_agent", END)
    return builder.compile(checkpointer=MemorySaver())


_compiled_graph = build_graph()


def serialize_messages(messages: list[BaseMessage] | None) -> list[dict[str, str]]:
    out: list[dict[str, str]] = []
    for msg in messages or []:
        content = getattr(msg, "content", "")
        if not content or not isinstance(content, str):
            continue
        if isinstance(msg, HumanMessage):
            out.append({"role": "user", "content": content.strip()})
        elif isinstance(msg, AIMessage):
            out.append({"role": "assistant", "content": content.strip()})
    return out


def get_thread_messages(thread_id: str) -> list[dict[str, str]]:
    config = {"configurable": {"thread_id": thread_id}}
    snapshot = _compiled_graph.get_state(config)
    if not snapshot or not snapshot.values:
        return []
    return serialize_messages(snapshot.values.get("messages"))


def invoke_graph(user_query: str, thread_id: str) -> GraphState:
    """Run one turn. Prior turns live in the graph checkpointer for this thread_id."""
    config = {"configurable": {"thread_id": thread_id}}
    return _compiled_graph.invoke(
        {
            "messages": [HumanMessage(content=user_query)],
            "user_query": user_query,
            "plan": {},
            "matches_result": None,
            "news_result": None,
            "odds_result": None,
            "final_answer": None,
        },
        config,
    )