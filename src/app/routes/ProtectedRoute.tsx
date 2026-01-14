import React from "react";
import { Navigate } from "react-router-dom";
import { useContext } from "react";
import { AuthContext } from "../auth/AuthContext";

interface Props {
  children: React.ReactNode;
  allowedRoles?: Array<"EMPLOYEE" | "MANAGER" | "FINANCE">;
}

/**
 * Uso:
 * <ProtectedRoute allowedRoles={["MANAGER", "FINANCE"]}>
 *   <ManagerPage />
 * </ProtectedRoute>
 */
export default function ProtectedRoute({ children, allowedRoles }: Props) {
  const auth = useContext(AuthContext);
  if (!auth) throw new Error("AuthContext not found");
  const { user } = auth;

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    // redireciona para página padrão de despesas se não tiver autorização
    return <Navigate to="/expenses" replace />;
  }

  return <>{children}</>;
}
