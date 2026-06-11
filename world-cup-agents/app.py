from fastapi import FastAPI

from pydantic import BaseModel

from agents.matches_agent.agent import invoke_agent_multi_step,invoke_agent_with_create_agent,invoke_agent

from agents.matches_agent.agent_v2 import invoke_agent_v2

from agents.news_agent.news_agent import news_agent,invoke_news_agent

from agents.bets_agent.bets_agent import invoke_bets_agent

from graph.graph_v2 import get_thread_messages, invoke_graph, serialize_messages



app = FastAPI()


class AgentQuery(BaseModel):
    user_prompt: str
    thread_id: str


def _chat_response(result: dict) -> dict:
    return {
        "answer": result.get("final_answer") or "",
        "messages": serialize_messages(result.get("messages")),
    }


@app.post("/agent/chat")
def run_agent(payload: AgentQuery):
    result = invoke_graph(payload.user_prompt, thread_id=payload.thread_id)

    print(result)

    return _chat_response(result)


@app.get("/agent/chat/{thread_id}/messages")
def read_thread_messages(thread_id: str):
    return {"messages": get_thread_messages(thread_id)}


@app.post("/agent/chat/v2")
def run_agent_v2(payload: AgentQuery):
    """Enhanced matches agent (composite matchup tool)."""
    result = invoke_graph(payload.user_prompt, thread_id=payload.thread_id)

    print(result)

    return _chat_response(result)


@app.post("/agent/chat/news")
def run_news_agent(payload: AgentQuery):
    response = invoke_news_agent(payload.user_prompt)

    print(response)

    return response


@app.get("/")
def read_root():
    x = 5
    y= 7
    return {"message":"hello world"}


# if __name__ == "__main__":
#     import uvicorn
#     uvicorn.run("app:app", host="127.0.0.1", port=8000, reload=True)
