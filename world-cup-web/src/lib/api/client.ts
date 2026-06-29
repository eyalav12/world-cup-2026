import { getApiBaseUrl } from "./config";
import type { ApiErrorBody } from "./types";

export class ApiError extends Error {
  status: number;

  constructor(message: string, status: number) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

/** Parse JSON body; returns null for 204 or empty responses. */
export async function readJsonResponse<T>(res: Response): Promise<T | null> {
  if (res.status === 204) return null;

  const text = await res.text();
  if (!text.trim()) return null;

  try {
    return JSON.parse(text) as T;
  } catch {
    throw new ApiError("Invalid JSON response", res.status);
  }
}

export async function apiFetch<T>(
  path: string,
  init?: RequestInit & { next?: { revalidate?: number } },
): Promise<T> {
  const base = getApiBaseUrl();
  const url = `${base}${path.startsWith("/") ? path : `/${path}`}`;

  const res = await fetch(url, {
    ...init,
    cache: "no-store",
    headers: {
      Accept: "application/json",
      ...init?.headers,
    },
  });

  if (!res.ok) {
    let message = res.statusText;
    try {
      const body = await readJsonResponse<ApiErrorBody>(res);
      if (body?.message) message = body.message;
    } catch (error) {
      if (error instanceof ApiError) message = error.message;
    }
    throw new ApiError(message, res.status);
  }

  const data = await readJsonResponse<T>(res);
  if (data === null) {
    throw new ApiError(res.statusText || "Empty response", res.status);
  }

  return data;
}
