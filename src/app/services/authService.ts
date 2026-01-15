import { apiFetch } from "./apiClient";

export interface LoginResponse {
  token: string;
  email: string;
  role: "EMPLOYEE" | "MANAGER" | "FINANCE";
}

export async function loginRequest(email: string, password: string): Promise<LoginResponse> {
  // ajuste o path conforme backend: aqui assumimos API_BASE + "/auth/login"
  return apiFetch<LoginResponse>("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });
}

/**
 * Optional: valida token (backend deve expor /auth/me que lê Authorization header)
 * Se seu backend não oferece esse endpoint, você pode remover o uso de meRequest no AuthProvider.
 */
export interface MeResponse {
  email: string;
  role: "EMPLOYEE" | "MANAGER" | "FINANCE";
}

export async function meRequest(token?: string): Promise<MeResponse> {
  // Se precisar usar um token diferente do localStorage, pode passar aqui.
  // apiFetch já injeta o token do localStorage; para forçar passar um token,
  // faça fetch manual:
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "";
  const t = token ?? localStorage.getItem("token");
  if (!t) throw new Error("no token");
  const res = await fetch(`${API_BASE_URL}/auth/me`, {
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${t}`,
    },
  });
  if (!res.ok) throw new Error("invalid token");
  return res.json();
}
