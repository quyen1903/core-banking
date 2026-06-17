import type { ApiErrorBody } from "@/types/api";

const rawApiBaseUrl = process.env.NEXT_PUBLIC_QUINNBANK_API_BASE_URL;

export const apiBaseUrl = rawApiBaseUrl?.replace(/\/$/, "");

export function hasBackendApi() {
  return Boolean(apiBaseUrl);
}

export class ApiClientError extends Error {
  readonly status: number;
  readonly code: string;
  readonly correlationId?: string;

  constructor(
    status: number,
    code: string,
    message: string,
    correlationId?: string,
  ) {
    super(message);
    this.name = "ApiClientError";
    this.status = status;
    this.code = code;
    this.correlationId = correlationId;
  }
}

function headersWithJson(headers?: HeadersInit) {
  const nextHeaders = new Headers(headers);

  if (!nextHeaders.has("Accept")) {
    nextHeaders.set("Accept", "application/json");
  }

  if (!nextHeaders.has("Content-Type")) {
    nextHeaders.set("Content-Type", "application/json");
  }

  return nextHeaders;
}

async function readErrorBody(response: Response): Promise<ApiErrorBody> {
  try {
    const parsed = (await response.json()) as Partial<ApiErrorBody>;

    return {
      code: parsed.code || "REQUEST_FAILED",
      message: parsed.message || "The request could not be completed.",
      correlationId: parsed.correlationId,
    };
  } catch {
    return {
      code: "REQUEST_FAILED",
      message: "The request could not be completed.",
    };
  }
}

export async function apiFetch<T>(path: string, init: RequestInit = {}) {
  if (!apiBaseUrl) {
    throw new ApiClientError(
      503,
      "BACKEND_NOT_CONFIGURED",
      "The banking service is not configured for this frontend.",
    );
  }

  const response = await fetch(`${apiBaseUrl}${path}`, {
    ...init,
    cache: "no-store",
    credentials: "include",
    headers: headersWithJson(init.headers),
  });

  if (!response.ok) {
    const errorBody = await readErrorBody(response);

    throw new ApiClientError(
      response.status,
      errorBody.code,
      errorBody.message,
      errorBody.correlationId,
    );
  }

  return (await response.json()) as T;
}
