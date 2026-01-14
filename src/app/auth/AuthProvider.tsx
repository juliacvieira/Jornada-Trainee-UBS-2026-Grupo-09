import { useState, useEffect } from "react";
import { AuthContext } from "./AuthContext";
import { loginRequest } from "../services/authService";
import type { User, UserRole } from "./types";
import { setAuthToken, clearAuthToken } from "./authToken";

export interface AuthContextType {
  user: User | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

// interface AuthUser {
//   email: string;
//   role: "EMPLOYEE" | "MANAGER" | "FINANCE";
//   token: string;
// }

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(() => {
    try {
      const raw = localStorage.getItem('user');
      return raw ? JSON.parse(raw) as User : null;
    } catch {
      return null;
    }
  });

  useEffect(() => {
    if (user) {
      localStorage.setItem('user', JSON.stringify(user));
      if (user.token) localStorage.setItem('token', user.token);
    } else {
      localStorage.removeItem('user');
      localStorage.removeItem('token');
    }
    if (user?.token) {
      setAuthToken(user.token);
    }
  }, [user]);

  async function login(email: string, password: string) {
    const response = await loginRequest(email, password);

    const userData = {
      id: response.id,
      email: response.email,
      name: response.name,
      role: response.role,
      token: response.token,
    };

    setUser(userData);

    if (response.token) {
      setAuthToken(response.token);
    }
  }

  // function login(email: string, role: UserRole) {
  //   setUser({ email, role });
  // }

  function logout() {
    setUser(null);
    clearAuthToken();
  }

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}