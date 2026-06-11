import os

from dotenv import load_dotenv

from langchain_core.messages import HumanMessage, ToolMessage

from langchain.agents import create_agent

from langchain.chat_models import init_chat_model

from langchain_openai import ChatOpenAI

from agents.matches_agent.tools import (get_head_to_head_from_all_db_history,get_matches_by_date,get_matches_by_group,get_matches_by_team_name,get_standings_by_group,get_teams_last_matches_football_data_api,get_teams_last_matches_from_all_db_history)


load_dotenv()

os.environ["OPENAI_API_KEY"] = os.getenv("OPENAI_API_KEY")



model1 = init_chat_model("gpt-4.1")



model = ChatOpenAI(model="gpt-4.1")



TOOLS = [

    get_matches_by_date,

    get_head_to_head_from_all_db_history,

    get_teams_last_matches_football_data_api,

    get_matches_by_group,

    get_matches_by_team_name,

    get_standings_by_group,

    get_teams_last_matches_from_all_db_history,

]



llm_with_tools = model.bind_tools(TOOLS)



functions_mapper = {

    "get_head_to_head_from_all_db_history": get_head_to_head_from_all_db_history,

    "get_teams_last_matches_from_all_db_history": get_teams_last_matches_from_all_db_history,

    "get_teams_last_matches_football_data_api": get_teams_last_matches_football_data_api,

    "get_matches_by_date": get_matches_by_date,

    "get_matches_by_group": get_matches_by_group,

    "get_matches_by_team_name": get_matches_by_team_name,

    "get_standings_by_group": get_standings_by_group,

}



agent = create_agent(

    model=model,

    tools=TOOLS,

    system_prompt="you are a world cup 2026 football data assistant",

)





def _execute_tool_calls(tool_calls, messages) -> None:

    """Run each tool the model requested and append ToolMessage results to messages."""

    for tool_call in tool_calls:

        try:

            tool_name = tool_call["name"]

            tool_result = functions_mapper[tool_name].invoke(tool_call["args"])

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





def invoke_agent(content: str):

    """

    Original single-round agent: LLM -> tools once -> LLM once -> return.

    Stops too early when the model wants more tool calls (empty content).

    """

    messages = [{"role": "user", "content": content}]

    ai_msg = llm_with_tools.invoke(messages)

    messages.append(ai_msg)

    if ai_msg.tool_calls:

        _execute_tool_calls(ai_msg.tool_calls, messages)

    final_response = llm_with_tools.invoke(messages)

    print(final_response)

    return final_response.content





def invoke_agent_multi_step(content: str, max_steps: int = 10):

    """

    Multi-step agent loop: keep calling tools until the model returns text.



    Each step:

      1. Ask the LLM with the full message history.

      2. If it requests tools -> run them, append results, go to step 1.

      3. If it returns text (no tool_calls) -> that is the final answer.

    """

    messages = [{"role": "user", "content": content}]



    for step in range(max_steps):

        ai_msg = llm_with_tools.invoke(messages)

        messages.append(ai_msg)

        print(f"[step {step + 1}] tool_calls={bool(ai_msg.tool_calls)}")



        if not ai_msg.tool_calls:

            print(ai_msg)

            return ai_msg.content or ""



        _execute_tool_calls(ai_msg.tool_calls, messages)



    return (

        "Agent reached the maximum number of tool steps without a final text answer. "

        "Try a simpler question or increase max_steps."

    )





def invoke_agent_with_create_agent(content: str):

    """

    Use LangChain's create_agent graph — it runs the tool loop for you internally

    (model -> tools -> model -> ... until done).

    """

    result = agent.invoke({"messages": [HumanMessage(content=content)]})

    final_messages = result["messages"]

    last_message = final_messages[-1]

    print(last_message)

    return last_message.content or ""

