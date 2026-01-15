import { NavLink } from "react-router-dom";
import { Button } from "./ui/button";
import { useAuth } from "@/app/hooks/useAuth";
import type { UserRole } from "@/app/auth/types";

type Page =
  | "expenses"
  | "reports"
  | "employees"
  | "approval"
  | "alerts";

interface NavigationProps {
  labels: Record<Page, string>;
}

const pagesByRole: Record<UserRole, Page[]> = {
  employee: ["expenses"],
  manager: ["expenses", "employees", "approval"],
  FINANCE: ["expenses", "approval", "alerts", "reports"],
};

export function Navigation({ labels }: NavigationProps) {
  const { user } = useAuth();

  if (!user) return null;
  if (user.role === "employee") return null;

  const allowedPages = pagesByRole[user.role];

  return (
    <nav className="flex gap-2 border-b border-border px-6 py-2">
      {allowedPages.map((page) => (
        <NavLink key={page} to={`/${page}`}>
          {({ isActive }) => (
            <Button variant={isActive ? "default" : "ghost"}>
              {labels[page]}
            </Button>
          )}
        </NavLink>
      ))}
    </nav>
  );
}
