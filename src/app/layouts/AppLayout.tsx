import { Outlet } from "react-router-dom";

interface AppLayoutProps {
  children: React.ReactNode;
}

export function AppLayout({ children }: AppLayoutProps) {
  return (
    <div className="min-h-screen bg-background text-foreground">
      {children}
      <main>
        <Outlet />
      </main>
    </div>
  );
}