import { useState } from 'react';
import { Navigate, Routes, Route } from 'react-router-dom';
import { AppLayout } from './layouts/AppLayout';
import { Navbar } from './components/Navbar';
import { Navigation } from './components/Navigation';
import { LoginPage } from './pages/LoginPage';
import { ExpensesPage } from './pages/ExpensesPage';
import { ReportsPage } from './pages/ReportsPage';
import { EmployeesPage } from './pages/EmployeesPage';
import { ApprovalPage } from './pages/ApprovalPage';
import { AlertsPage } from './pages/AlertsPage';
import { translations, type Language } from './translations';
import { ProtectedRoute } from './routes/ProtectedRoute';

export default function App() {
  const [language, setLanguage] = useState<Language>("pt");

  const t = translations[language];

  return (
    <Routes>
      {/* Pública */}
      <Route
        path="/login"
        element={
          <LoginPage t={t} language={language} onLanguageChange={setLanguage} />
        }
      />

      {/* Privadas */}
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <AppLayout>
              <>
                <Navbar
                  language={language}
                  onLanguageChange={setLanguage}
                />
                <Navigation
                  labels={{
                    expenses: t.expenses.title,
                    employees: t.employees.title,
                    approval: t.approval.title,
                    alerts: t.alerts.title,
                    reports: t.reports.title,
                  }}
                />
              </>
            </AppLayout>
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="/expenses" replace />} />

        <Route path="/expenses" element={<ExpensesPage t={t} language={language} />} />

        <Route
          path="/employees"
          element={
            <ProtectedRoute allowedRoles={["manager"]}>
              <EmployeesPage t={t} />
            </ProtectedRoute>
          }
        />

        <Route
          path="approval"
          element={
            <ProtectedRoute allowedRoles={["manager", "finance"]}>
              <ApprovalPage t={t} language={language} />
            </ProtectedRoute>
          }
        />

        <Route
          path="/alerts"
          element={
            <ProtectedRoute allowedRoles={["finance"]}>
              <AlertsPage t={t} language={language} />
            </ProtectedRoute>
          }
        />

        <Route
          path="/reports"
          element={
            <ProtectedRoute allowedRoles={["finance"]}>
              <ReportsPage t={t} language={language} />
            </ProtectedRoute>
          }
        />
      </Route>
    </Routes>
  );
}