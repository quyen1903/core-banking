"use client";

import Link from "next/link";
import { useQuery } from "@tanstack/react-query";
import { ArrowRight, CreditCard, ReceiptText } from "lucide-react";

import { getAccounts } from "@/api/accounts";
import { getDashboardSummary } from "@/api/dashboard";
import { getTransactions } from "@/api/transactions";
import { AccountCard } from "@/components/banking/account-card";
import { BalanceSummary } from "@/components/banking/balance-summary";
import { ErrorState } from "@/components/banking/error-state";
import { LoadingState } from "@/components/banking/loading-state";
import { PageHeader } from "@/components/banking/page-header";
import { TransactionTable } from "@/components/banking/transaction-table";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { queryKeys } from "@/lib/query-keys";

export function DashboardView() {
  const summaryQuery = useQuery({
    queryKey: queryKeys.dashboard,
    queryFn: getDashboardSummary,
  });
  const accountsQuery = useQuery({
    queryKey: queryKeys.accounts,
    queryFn: getAccounts,
  });
  const transactionsQuery = useQuery({
    queryKey: queryKeys.transactions,
    queryFn: getTransactions,
  });

  if (
    summaryQuery.isLoading ||
    accountsQuery.isLoading ||
    transactionsQuery.isLoading
  ) {
    return <LoadingState />;
  }

  if (summaryQuery.isError || accountsQuery.isError || transactionsQuery.isError) {
    return (
      <ErrorState
        onRetry={() => {
          void summaryQuery.refetch();
          void accountsQuery.refetch();
          void transactionsQuery.refetch();
        }}
      />
    );
  }

  const summary = summaryQuery.data;
  const accounts = accountsQuery.data ?? [];
  const recentTransactions = (transactionsQuery.data ?? []).slice(0, 5);

  if (!summary) {
    return (
      <ErrorState
        title="Dashboard unavailable"
        description="The account summary could not be loaded."
        onRetry={() => void summaryQuery.refetch()}
      />
    );
  }

  return (
    <div className="grid gap-6">
      <PageHeader
        eyebrow="Overview"
        title="Good afternoon, Quinn"
        description="Monitor balances, account activity, and pending requests from one workspace."
        actions={
          <Button asChild>
            <Link href="/transfers">
              New transfer
              <ArrowRight className="size-4" />
            </Link>
          </Button>
        }
      />

      <BalanceSummary summary={summary} />

      <div className="grid gap-4 xl:grid-cols-[1.4fr_0.6fr]">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between gap-4 space-y-0">
            <CardTitle>Recent activity</CardTitle>
            <Button asChild variant="ghost" size="sm">
              <Link href="/transactions">View all</Link>
            </Button>
          </CardHeader>
          <CardContent>
            <TransactionTable transactions={recentTransactions} />
          </CardContent>
        </Card>

        <div className="grid gap-4">
          <Card>
            <CardContent className="flex items-center gap-4 p-5">
              <div className="flex size-10 items-center justify-center rounded-md bg-primary/10">
                <CreditCard className="size-5 text-primary" />
              </div>
              <div className="min-w-0">
                <p className="font-medium">Card settlement</p>
                <p className="text-sm text-muted-foreground">
                  Pending card network settlement remains read-only in this demo.
                </p>
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="flex items-center gap-4 p-5">
              <div className="flex size-10 items-center justify-center rounded-md bg-accent">
                <ReceiptText className="size-5 text-accent-foreground" />
              </div>
              <div className="min-w-0">
                <p className="font-medium">Statements</p>
                <p className="text-sm text-muted-foreground">
                  Export workflows require backend authorization and audit.
                </p>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>

      <div className="grid gap-4 lg:grid-cols-3">
        {accounts.map((account) => (
          <AccountCard key={account.id} account={account} />
        ))}
      </div>
    </div>
  );
}
