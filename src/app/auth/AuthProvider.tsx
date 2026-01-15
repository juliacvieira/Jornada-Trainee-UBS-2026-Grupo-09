import React, { useState, useEffect, useCallback } from "react";
import { AuthContext } from "./AuthContext";
import type { User } from "./types";
import { loginRequest, meRequest } from "../services/authService";
import { useNavigate } from "react-router-dom";

export interface AuthContextType {
  user: User | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  setUser: (u: User | null) => void;
  loading: boolean;
}

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUserState] = useState<User | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const navigate = useNavigate();

  const setUser = useCallback((u: User | null) => {
    setUserState(u);
    if (u) {
      try {
        localStorage.setItem("user", JSON.stringify(u));
        if (u.token) localStorage.setItem("token", u.token);
      } catch (e) {
        // ignore storage errors
      }
    } else {
      try {
        localStorage.removeItem("user");
        localStorage.removeItem("token");
      } catch (e) {}
    }
  }, []);

  useEffect(() => {
    // On mount, try restore from localStorage and validate with /auth/me if available
    async function restore() {
      try {
        const raw = localStorage.getItem("user");
        const token = localStorage.getItem("token");
        if (raw && token) {
          const parsed = JSON.parse(raw) as User;
          // Optionally validate token by calling /auth/me
          try {
            const me = await meRequest(token);
            // If me returned, ensure shape and token
            setUserState({ email: me.email, role: me.role, token });
          } catch (err) {
            // token invalid -> clear
            setUserState(null);
            localStorage.removeItem("user");
            localStorage.removeItem("token");
          }
        }
      } catch (e) {
        // ignore parse errors
      } finally {
        setLoading(false);
      }
    }
    restore();
  }, [setUser]);

  async function login(email: string, password: string) {
    setLoading(true);
    try {
      const res = await loginRequest(email, password);
      // expected: { token, email, role }
      const newUser: User = { email: res.email, role: res.role, token: res.token };
      setUser(newUser);
      setUserState(newUser);
      setLoading(false);
      // Navigate to default authenticated page
      navigate("/expenses");
    } catch (err: any) {
      setLoading(false);
      throw err;
    }
  }

  function logout() {
    setUser(null);
    setUserState(null);
    try {
      localStorage.removeItem("token");
      localStorage.removeItem("user");
    } catch {}
    navigate("/login");
  }

  return (
    <AuthContext.Provider value={{ user, login, logout, setUser, loading }}>
      {children}
    </AuthContext.Provider>
  );
};
