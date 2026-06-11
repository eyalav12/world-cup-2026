import os
from dotenv import load_dotenv
from langchain_core.messages import HumanMessage, ToolMessage
from langchain.agents import create_agent
from langchain.chat_models import init_chat_model
from langchain_openai import ChatOpenAI
from agents.news_agent.news_agent_prompts import NEWS_SYSTEM_PROMPT
from agents.news_agent.news_tools import get_general_world_cup_news,get_news_by_team_name

load_dotenv()

os.environ["OPENAI_API_KEY"] = os.getenv("OPENAI_API_KEY")
model = ChatOpenAI(model="gpt-4.1")

TOOLS = [
    get_news_by_team_name,
    get_general_world_cup_news
]

news_agent = create_agent(
    model=model,
    tools= TOOLS,
    system_prompt=NEWS_SYSTEM_PROMPT
)

def invoke_news_agent(content:str):
    result = news_agent.invoke({"messages": [HumanMessage(content=content)]})
    final_messages = result["messages"]
    last_message = final_messages[-1]
    print(last_message)
    return last_message.content or ""