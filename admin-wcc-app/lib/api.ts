export const API_BASE = process.env.NEXT_PUBLIC_API_BASE || 'http://localhost:8080';
export const API_KEY = process.env.NEXT_PUBLIC_API_KEY;

export type HttpMethod = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
export type ApiRequestBody = Record<string, unknown> | unknown[] | string | number | boolean | null;

export function getErrorMessage(error: unknown, fallback = 'Something went wrong'): string {
  return error instanceof Error && error.message ? error.message : fallback;
}

export async function apiFetch<T>(
  path: string,
  options: {
    method?: HttpMethod;
    body?: ApiRequestBody;
    token?: string;
  } = {}
): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  };
  if (options.token) headers['Authorization'] = `Bearer ${options.token}`;
  if (API_KEY) headers['X-API-KEY'] = API_KEY;

  const url = `${API_BASE}${path}`;

  const res = await fetch(url, {
    method: options.method || 'GET',
    headers,
    body: options.body ? JSON.stringify(options.body) : undefined,
    credentials: 'include',
  });
  if (!res.ok) {
    let message = `${res.status} ${res.statusText}`;
    try {
      const data = (await res.json()) as { message?: string };
      if (data.message) message = data.message;
    } catch {}
    throw new Error(message);
  }
  try {
    return (await res.json()) as T;
  } catch {
    return undefined as unknown as T;
  }
}
