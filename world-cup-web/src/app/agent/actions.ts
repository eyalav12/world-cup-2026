"use server";

import { revalidatePath } from "next/cache";
import { toUserMessage } from "@/lib/api/client";
import { ensureThreadId, invokeAgentChat } from "@/lib/agent/server";

export type AgentFormState = {
  error: string | null;
};

export async function sendAgentMessage(
  _prev: AgentFormState | null,
  formData: FormData,
): Promise<AgentFormState> {
  const prompt = formData.get("prompt")?.toString().trim();
  if (!prompt) {
    return { error: "Please enter a message." };
  }

  try {
    const threadId = await ensureThreadId();
    await invokeAgentChat(threadId, prompt);
    revalidatePath("/agent");
    return { error: null };
  } catch (error) {
    const message =
      error instanceof Error && error.name === "TimeoutError"
        ? "Agent request timed out after 2 minutes. Try a simpler question."
        : toUserMessage(error, "Failed to reach agent service");
    return { error: message };
  }
}
