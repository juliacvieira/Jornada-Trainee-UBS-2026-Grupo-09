import { apiFetch } from "./apiClient";

export interface LoginResponse {
    id: string;
    email: string;
    name: string;
    role: "EMPLOYEE" | "MANAGER" | "FINANCE";
    active: boolean;
    token?: string;
}

export function loginRequest(
    email: string,
    password: string
): Promise<LoginResponse> {
    return apiFetch<LoginResponse>("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({ email, password }),
    });
}