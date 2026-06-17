import { ArrowDownLeft, ArrowUpRight, Clock3, WalletCards } from "lucide-react";

import { Card, CardContent } from "@/components/ui/card";
import { formatCurrencyAmount } from "@/lib/format";
import type { DashboardSummary } from "@/types/banking";

interface BalanceSummaryProps {
  summary: DashboardSummary;
}

export function BalanceSummary({ summary }: BalanceSummaryProps) {
  const items = [
    {
      label: "Available balance",
      value: formatCurrencyAmount(
        summary.totalAvailable.amount,
        summary.totalAvailable.currency,
      ),
      icon: WalletCards,
      tone: "text-primary",
    },
    {
      label: "Month inflow",
      value: formatCurrencyAmount(
        summary.monthInflow.amount,
        summary.monthInflow.currency,
      ),
      icon: ArrowDownLeft,
      tone: "text-emerald-700 dark:text-emerald-300",
    },
    {
      label: "Month outflow",
      value: formatCurrencyAmount(
        summary.monthOutflow.amount,
        summary.monthOutflow.currency,
      ),
      icon: ArrowUpRight,
      tone: "text-rose-700 dark:text-rose-300",
    },
    {
      label: "Pending transfers",
      value: summary.pendingTransfers,
      icon: Clock3,
      tone: "text-amber-700 dark:text-amber-300",
    },
  ];

  return (
    <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
      {items.map((item) => (
        <Card key={item.label}>
          <CardContent className="flex items-center justify-between gap-4 p-5">
            <div className="min-w-0 space-y-1">
              <p className="truncate text-sm text-muted-foreground">
                {item.label}
              </p>
              <p className="truncate text-xl font-semibold tracking-normal">
                {item.value}
              </p>
            </div>
            <div className="flex size-10 shrink-0 items-center justify-center rounded-md bg-secondary">
              <item.icon className={`size-5 ${item.tone}`} />
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}
