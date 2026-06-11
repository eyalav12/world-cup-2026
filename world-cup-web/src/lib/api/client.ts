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

/** Turn fetch/JSON failures into short user-facing copy (no raw parser errors). */
export function toUserMessage(error: unknown, fallback: string): string {
  if (error instanceof ApiError) {
    if (error.status === 204) return fallback;
    return error.message;
  }
  if (error instanceof Error) {
    if (error.name === "TimeoutError") {
      return "Request timed out. Please try again.";
    }
    const msg = error.message.toLowerCase();
    if (
      error.name === "SyntaxError" ||
      msg.includes("json") ||
      msg.includes("unexpected end")
    ) {
      return fallback;
    }
    if (msg.includes("fetch failed") || msg.includes("network")) {
      return "Could not reach the server. Please try again in a moment.";
    }
    return error.message;
  }
  return fallback;
}

function buildUrl(path: string): string {
  const base = getApiBaseUrl();
  return `${base}${path.startsWith("/") ? path : `/${path}`}`;
}

async function parseErrorResponse(res: Response): Promise<string> {
  let message = res.statusText || "Request failed";
  try {
    const body = (await res.json()) as ApiErrorBody;
    if (body.message) message = body.message;
  } catch {
    /* ignore */
  }
  return message;
}

async function readJsonBody<T>(res: Response): Promise<T> {
  const text = await res.text();
  if (!text.trim()) {
    throw new ApiError("No data available yet.", res.status);
  }
  try {
    return JSON.parse(text) as T;
  } catch {
    throw new ApiError("Could not read data from the server.", res.status);
  }
}

const defaultFetchInit: RequestInit = {
  cache: "no-store",
  headers: { Accept: "application/json" },
};

export async function apiFetch<T>(
  path: string,
  init?: RequestInit,
): Promise<T> {
  const res = await fetch(buildUrl(path), {
    ...defaultFetchInit,
    ...init,
    headers: {
      ...defaultFetchInit.headers,
      ...init?.headers,
    },
  });

  if (res.status === 204) {
    throw new ApiError("No data available yet.", 204);
  }

  if (!res.ok) {
    throw new ApiError(await parseErrorResponse(res), res.status);
  }

  return readJsonBody<T>(res);
}

/** Backend 204 → null. Safe JSON parse on 200. */
export async function fetchNullable<T>(
  path: string,
  init?: RequestInit,
): Promise<T | null> {
  const res = await fetch(buildUrl(path), {
    ...defaultFetchInit,
    ...init,
    headers: {
      ...defaultFetchInit.headers,
      ...init?.headers,
    },
  });

  if (res.status === 204) return null;

  if (!res.ok) {
    throw new ApiError(await parseErrorResponse(res), res.status);
  }

  return readJsonBody<T>(res);
}

/** Backend 204 or 404 → null. */
export async function fetchNullableOptional<T>(
  path: string,
  init?: RequestInit,
): Promise<T | null> {
  const res = await fetch(buildUrl(path), {
    ...defaultFetchInit,
    ...init,
    headers: {
      ...defaultFetchInit.headers,
      ...init?.headers,
    },
  });

  if (res.status === 204 || res.status === 404) return null;

  if (!res.ok) {
    throw new ApiError(await parseErrorResponse(res), res.status);
  }

  return readJsonBody<T>(res);
}
