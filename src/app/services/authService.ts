import { apiFetch } from "./apiClient";

export interface LoginResponse {
  email: string;
  role: "EMPLOYEE" | "MANAGER" | "FINANCE";
}

export function loginRequest(
  email: string,
  password: string
): Promise<LoginResponse> {
  return apiFetch<LoginResponse>("/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });
}