import { AppSidebar } from "@/components/layout/app-sidebar";
import { TopNavigation } from "@/components/layout/top-navigation";

export default function BankingLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <div className="min-h-screen bg-background">
      <AppSidebar className="hidden md:flex" />
      <div className="md:pl-72">
        <TopNavigation />
        <main className="mx-auto w-full max-w-7xl px-4 py-6 sm:px-6 lg:px-8">
          {children}
        </main>
      </div>
    </div>
  );
}
