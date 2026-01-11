import { ChevronDown, ArrowRightToLine } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { Button } from '../components/ui/button';
import type { Language } from '../translations';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from './ui/dropdown-menu';
import ubsLogo from '@/assets/UBS_Logo_Semibold.svg';

interface NavbarProps {
  language: Language;
  onLanguageChange: (lang: Language) => void;
}

export function Navbar({ language, onLanguageChange }: NavbarProps) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <header className="flex items-center justify-between border-b border-border px-6 py-3 bg-background">
      {/* Logo + Nome */}
      <div className="flex items-center gap-3">
        <img src={ubsLogo} alt="UBS" className="h-8" />
        <span className="font-semibold text-lg">
          Expense Manager
        </span>
      </div>

      {/* Usuário + ações */}
      <div className="flex items-center gap-4">
        {/* Idioma */}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="sm" className="gap-2">
              <span>{language === "pt" ? "PT" : "EN"}</span>
              <ChevronDown className="h-4 w-4" />
            </Button>
          </DropdownMenuTrigger>

          <DropdownMenuContent align="end">
            <DropdownMenuItem onClick={() => onLanguageChange("pt")}>
              🇧🇷 Português (PT)
            </DropdownMenuItem>
            <DropdownMenuItem onClick={() => onLanguageChange("en")}>
              us English (EN)
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>

        {/* Info do usuário */}
        {user && (
          <div className="text-right leading-tight">
            <div className="text-sm font-medium">
              {user.email}
            </div>
            <div className="text-xs text-muted-foreground">
              {user.role === "employee" && "Funcionário"}
              {user.role === "manager" && "Gestor"}
              {user.role === "finance" && "Financeiro"}
            </div>
          </div>
        )}

        {/* Logout */}
        <Button
          className="bg-[#E60000] hover:bg-[#CC0000] text-white"
          onClick={handleLogout}
        >
          <ArrowRightToLine className="w-3 h-3 mr-1" />
          Log Out
        </Button>
      </div>
    </header>
  );
}