"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  ArrowRightLeft,
  BadgeDollarSign,
  LayoutDashboard,
  Landmark,
  ShieldCheck,
  UserRound,
} from "lucide-react";

import { cn } from "@/lib/utils";

const navigationItems = [
  {
    href: "/dashboard",
    label: "Dashboard",
    icon: LayoutDashboard,
  },
  {
    href: "/accounts",
    label: "Accounts",
    icon: Landmark,
  },
  {
    href: "/transactions",
    label: "Transactions",
    icon: BadgeDollarSign,
  },
  {
    href: "/transfers",
    label: "Transfers",
    icon: ArrowRightLeft,
  },
  {
    href: "/profile",
    label: "Profile",
    icon: UserRound,
  },
];

interface AppSidebarProps {
  className?: string;
}

export function AppSidebar({ className }: AppSidebarProps) {
  const pathname = usePathname();

  return (
    <aside
      className={cn(
        "fixed inset-y-0 left-0 z-40 flex w-72 flex-col border-r border-sidebar-border bg-sidebar text-sidebar-foreground",
        className,
      )}
    >
      <div className="flex h-16 items-center gap-3 border-b border-sidebar-border px-5">
        <div className="flex size-9 items-center justify-center rounded-md bg-sidebar-primary text-sidebar-primary-foreground">
          <ShieldCheck className="size-5" />
        </div>
        <div className="min-w-0">
          <p className="truncate text-sm font-semibold">QuinnBank</p>
          <p className="truncate text-xs text-sidebar-foreground/65">
            Customer banking
          </p>
        </div>
      </div>
      <nav className="flex-1 space-y-1 px-3 py-4">
        {navigationItems.map((item) => {
          const active =
            pathname === item.href ||
            (item.href !== "/dashboard" && pathname.startsWith(item.href));

          return (
            <Link
              key={item.href}
              href={item.href}
              className={cn(
                "flex items-center gap-3 rounded-md px-3 py-2.5 text-sm font-medium text-sidebar-foreground/75 transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground",
                active &&
                  "bg-sidebar-accent text-sidebar-accent-foreground shadow-sm",
              )}
            >
              <item.icon className="size-4" />
              <span className="truncate">{item.label}</span>
            </Link>
          );
        })}
      </nav>
      <div className="border-t border-sidebar-border p-4">
        <div className="rounded-md border border-sidebar-border bg-sidebar-accent/60 p-3">
          <p className="text-xs font-medium text-sidebar-foreground">
            Backend authority
          </p>
          <p className="mt-1 text-xs leading-5 text-sidebar-foreground/65">
            Client data is display-only until accepted by QuinnBank Core APIs.
          </p>
        </div>
      </div>
    </aside>
  );
}
