const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "";

function getToken(): string | null {
  try {
    return localStorage.getItem("token");
  } catch {
    return null;
  }
}

export async function apiFetch<T = any>(path: string, options: RequestInit = {}): Promise<T> {
  const token = getToken();
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options.headers as Record<string, string> || {}),
  };
  if (token) headers["Authorization"] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
  });

  if (res.status === 401) {
    // optional: clear local auth state on 401; the AuthProvider also calls /auth/me on restore
    try {
      localStorage.removeItem("token");
      localStorage.removeItem("user");
    } catch {}
    // Let caller handle navigation / re-login
    throw new Error("Unauthorized");
  }

  if (!res.ok) {
    const text = await res.text();
    // Try parse JSON error
    try {
      const json = JSON.parse(text);
      throw new Error(json.message || JSON.stringify(json));
    } catch {
      throw new Error(text || res.statusText);
    }
  }

  // handle empty body
  const contentType = res.headers.get("content-type") || "";
  if (contentType.includes("application/json")) {
    return res.json();
  }
  // @ts-ignore
  return null;
}
