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

export async function apiFetch<T>(
  path: string,
  init?: RequestInit & { next?: { revalidate?: number } },
): Promise<T> {
  const base = getApiBaseUrl();
  const url = `${base}${path.startsWith("/") ? path : `/${path}`}`;

  const res = await fetch(url, {
    ...init,
    headers: {
      Accept: "application/json",
      ...init?.headers,
    },
  });

  if (!res.ok) {
    let message = res.statusText;
    try {
      const body = (await res.json()) as ApiErrorBody;
      if (body.message) message = body.message;
    } catch {
      /* ignore */
    }
    throw new ApiError(message, res.status);
  }

  return res.json() as Promise<T>;
}
