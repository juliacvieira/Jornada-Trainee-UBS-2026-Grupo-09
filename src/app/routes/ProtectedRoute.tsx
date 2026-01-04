import { Navigate } from "react-router-dom";
import { useAuth} from "../hooks/useAuth";
import type { UserRole } from "../auth/types";

interface ProtectedRouteProps {
  children: React.ReactNode;
  allowedRoles?: UserRole[];
}

export function ProtectedRoute({ children, allowedRoles }: ProtectedRouteProps) {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to="/expenses" replace />;
  }

  return <>{children}</>;
}