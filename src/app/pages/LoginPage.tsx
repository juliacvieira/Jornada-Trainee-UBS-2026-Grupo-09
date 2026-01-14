import React, { useState, useContext } from "react";
import { AuthContext } from "../auth/AuthContext";

export default function LoginPage() {
  const auth = useContext(AuthContext);
  if (!auth) throw new Error("AuthContext not found");
  const { login, loading } = auth;

  const [email, setEmail] = useState<string>("employee@ubs.com");
  const [password, setPassword] = useState<string>("123456");
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
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="max-w-md w-full bg-white p-8 rounded shadow">
        <h1 className="text-2xl font-semibold mb-6">Entrar - ExpenseManager</h1>

        {error && <div className="mb-4 text-red-600">{error}</div>}

        <form onSubmit={handleSubmit} className="space-y-4">
          <label className="block">
            <span className="text-sm">E-mail</span>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="mt-1 block w-full rounded border px-3 py-2"
              required
            />
          </label>

          <label className="block">
            <span className="text-sm">Senha</span>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="mt-1 block w-full rounded border px-3 py-2"
              required
            />
          </label>

          <button
            type="submit"
            className="w-full py-2 px-4 rounded bg-indigo-600 text-white disabled:opacity-60"
            disabled={submitting || loading}
          >
            {submitting || loading ? "Entrando..." : "Entrar"}
          </button>
        </form>

        <p className="text-xs text-gray-500 mt-4">
          Usuários de teste: <br />
          employee@ubs.com / 123456 <br />
          manager@ubs.com / 123456
        </p>
      </div>
    </div>
  );
}
