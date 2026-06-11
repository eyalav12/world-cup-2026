"""
Enhanced agent — composite matchup tool + structured system prompt.

Original agent remains in agents/agent.py (unchanged).
Switch app endpoint to invoke_agent_v2 when ready.
"""

import os

from dotenv import load_dotenv
from langchain.agents import create_agent
from langchain_core.messages import AIMessage, HumanMessage, ToolMessage
from langchain_openai import ChatOpenAI

from agents.matches_agent.matchup_tools import MATCHUP_FIXTURE_TOOLS
from agents.matches_agent.prompts import MATCHUP_SYSTEM_PROMPT

load_dotenv()
os.environ["OPENAI_API_KEY"] = os.getenv("OPENAI_API_KEY")

model_v2 = ChatOpenAI(model="gpt-4.1")

TOOLS_V2 = MATCHUP_FIXTURE_TOOLS

functions_mapper_v2 = {tool.name: tool for tool in TOOLS_V2}

agent_v2 = create_agent(
    model=model_v2,
    tools=TOOLS_V2,
    system_prompt=MATCHUP_SYSTEM_PROMPT,
)

llm_with_tools_v2 = model_v2.bind_tools(TOOLS_V2)


def message_content_to_str(content) -> str:
    """LangChain may return content as str or list of blocks."""
    if content is None:
        return ""
    if isinstance(content, str):
        return content
    if isinstance(content, list):
        parts = []
        for block in content:
            if isinstance(block, str):
                parts.append(block)
            elif isinstance(block, dict) and block.get("text"):
                parts.append(str(block["text"]))
        return "\n".join(parts)
    return str(content)


def _execute_tool_calls_v2(tool_calls, messages) -> None:
    for tool_call in tool_calls:
        try:
            tool_name = tool_call["name"]
            tool_fn = functions_mapper_v2[tool_name]
            tool_result = tool_fn.invoke(tool_call["args"])
            messages.append(
                ToolMessage(str(tool_result), tool_call_id=tool_call["id"])
            )
        except Exception as e:
            print("error in calling", e)
            messages.append(
                ToolMessage(
                    content=f"Error: {str(e)}",
                    tool_call_id=tool_call["id"],
                )
            )


def invoke_agent_multi_step_v2(content: str, max_steps: int = 10):
    """Multi-step loop using v2 tools and prompt context via bound tools only."""
    messages = [{"role": "user", "content": content}]

    for step in range(max_steps):
        ai_msg = llm_with_tools_v2.invoke(messages)
        messages.append(ai_msg)
        print(f"[v2 step {step + 1}] tool_calls={bool(ai_msg.tool_calls)}")

        if not ai_msg.tool_calls:
            print(ai_msg)
            return message_content_to_str(ai_msg.content)

        _execute_tool_calls_v2(ai_msg.tool_calls, messages)

    return (
        "Agent reached the maximum number of tool steps without a final text answer. "
        "Try a simpler question or increase max_steps."
    )


def invoke_agent_v2(content: str):
    """LangChain create_agent graph with v2 tools + MATCHUP_SYSTEM_PROMPT."""
    result = agent_v2.invoke({"messages": [HumanMessage(content=content)]})
    final_messages = result["messages"]
    last_message = final_messages[-1]
    print(last_message)
    if isinstance(last_message, AIMessage):
        return message_content_to_str(last_message.content)
    return message_content_to_str(getattr(last_message, "content", last_message))
