import React, { useState, useContext } from "react";
import { AuthContext } from "../auth/AuthContext";
import { ChevronDown, Eye, EyeOff } from 'lucide-react';
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";
import { Checkbox } from "../components/ui/checkbox";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '../components/ui/dropdown-menu';

import type { TranslationKeys, Language } from "../translations";
import ubsLogo from "@/assets/UBS_Logo_Semibold.svg";

interface LoginPageProps {
  t: TranslationKeys;
  language: Language;
  onLanguageChange: (lang: Language) => void;
}

export function LoginPage({ t, language, onLanguageChange }: LoginPageProps) {
  const auth = useContext(AuthContext);
  if (!auth) throw new Error("AuthContext not found");
  const { login, loading } = auth;

  const [email, setEmail] = useState<string>("employee@ubs.com");
  const [password, setPassword] = useState<string>("123456");
  const [showPassword, setShowPassword] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await login(email, password);
    } catch (err: any) {
      setError(err?.message || "Erro ao efetuar login");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100 p-8">
      <div className="w-full max-w-md">
        <div className="bg-white rounded-lg shadow-lg p-8">
          {/* Language Selector */}
          <div className="flex items-center gap-1">
            <DropdownMenu>
              <DropdownMenuTrigger className="flex items-center gap-2 px-4 py-2 hover:bg-gray-50 transition-colors">
                <span>{language === 'pt' ? 'PT' : 'EN'}</span>
                <ChevronDown className="w-4 h-4" />
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuItem onClick={() => onLanguageChange('pt')}>
                  🇧🇷 Português (PT)
                </DropdownMenuItem>
                <DropdownMenuItem onClick={() => onLanguageChange('en')}>
                  us English (EN)
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
          <div className="mb-8">
            {/* UBS Logo */}
            <div className="flex justify-center mb-8">
              <img src={ubsLogo} alt="UBS Logo" className="h-10" />
            </div>

            <h1 className="text-gray-900 mb-2 text-center">{t.login.title}</h1>
            <p className="text-gray-600 text-center">{t.login.subtitle}</p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-2">
              <Label htmlFor="email">{t.login.email}</Label>
              <Input
                id="email"
                type="email"
                placeholder={t.login.emailPlaceholder}
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="space-y-2 relative">
              <Label htmlFor="password">{t.login.password}</Label>
              <Input
                id="password"
                type={showPassword ? 'text' : 'password'}
                placeholder={t.login.passwordPlaceholder}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
              <button
                type="button"
                className="absolute right-2 top-8 text-gray-500"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
              </button>
            </div>

            {/* Role (mock) */}
            {/* <div className="space-y-2">
              <Label htmlFor="role">{t.login.role}</Label>
              <select
                id="role"
                value={role}
                onChange={(e) => setRole(e.target.value as UserRole)}
                className="w-full rounded-md border bg-input px-3 py-2 text-sm"
              >
                <option value="EMPLOYEE">{t.employees.roles.employee}</option>
                <option value="MANAGER">{t.employees.roles.manager}</option>
                <option value="FINANCE">{t.employees.roles.finance}</option>
              </select>
            </div> */}

            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Checkbox id="remember" />
                <label htmlFor="remember" className="text-sm text-gray-700 cursor-pointer">
                  {t.login.rememberMe}
                </label>
              </div>
              <a href="#" className="text-sm text-[#E60000] hover:underline">
                {t.login.forgotPassword}
              </a>
            </div>

            <Button 
              type="submit" 
              className="w-full bg-[#E60000] hover:bg-[#CC0000] text-white"
              disabled={submitting || loading}
            >
              {submitting || loading ? t.login.loginButton : t.login.loginButton}
            </Button>
          </form>
        </div>

        <p className="text-center text-sm text-gray-600 mt-6">
          © 2025 UBS. All rights reserved.
        </p>
      </div>
    </div>
  );
}
