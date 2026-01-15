import { cn } from "../lib/utils"
import {
  LayoutDashboard,
  Users,
  Receipt,
  CheckSquare,
  AlertTriangle,
  BarChart3,
  LogOut,
} from "lucide-react"

const menuItems = [
  { label: "Dashboard", icon: LayoutDashboard },
  { label: "Funcionários", icon: Users },
  { label: "Despesas", icon: Receipt },
  { label: "Aprovações", icon: CheckSquare },
  { label: "Alertas", icon: AlertTriangle },
  { label: "Relatórios", icon: BarChart3 },
]

export function Sidebar() {
  return (
    <aside
      className={cn(
        "flex h-screen w-64 flex-col border-r",
        "bg-sidebar text-sidebar-foreground"
      )}
    >
      {/* Logo */}
      <div className="flex h-16 items-center border-b px-6">
        <span className="text-lg font-semibold tracking-tight">
          UBS Expense
        </span>
      </div>

      {/* Menu */}
      <nav className="flex-1 space-y-1 p-4">
        {menuItems.map((item) => (
          <button
            key={item.label}
            className={cn(
              "flex w-full items-center gap-3 rounded-md px-3 py-2 text-sm",
              "hover:bg-sidebar-accent hover:text-sidebar-accent-foreground",
              "transition-colors"
            )}
          >
            <item.icon className="h-4 w-4" />
            {item.label}
          </button>
        ))}
      </nav>

      {/* Footer */}
      <div className="border-t p-4">
        <button
          className={cn(
            "flex w-full items-center gap-3 rounded-md px-3 py-2 text-sm",
            "hover:bg-destructive hover:text-destructive-foreground",
            "transition-colors"
          )}
        >
          <LogOut className="h-4 w-4" />
          Log Out
        </button>
      </div>
    </aside>
  )
}
