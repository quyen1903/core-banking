"use client";

import { useQuery } from "@tanstack/react-query";
import { ReceiptText } from "lucide-react";

import { getTransactions } from "@/api/transactions";
import { EmptyState } from "@/components/banking/empty-state";
import { ErrorState } from "@/components/banking/error-state";
import { LoadingState } from "@/components/banking/loading-state";
import { PageHeader } from "@/components/banking/page-header";
import { TransactionTable } from "@/components/banking/transaction-table";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { queryKeys } from "@/lib/query-keys";

export function TransactionsView() {
  const transactionsQuery = useQuery({
    queryKey: queryKeys.transactions,
    queryFn: getTransactions,
  });

  if (transactionsQuery.isLoading) {
    return <LoadingState />;
  }

  if (transactionsQuery.isError) {
    return <ErrorState onRetry={() => void transactionsQuery.refetch()} />;
  }

  const transactions = transactionsQuery.data ?? [];

  return (
    <div className="grid gap-6">
      <PageHeader
        eyebrow="Transactions"
        title="Activity"
        description="Review posted and pending activity from the account ledger view."
      />
      {transactions.length === 0 ? (
        <EmptyState
          icon={ReceiptText}
          title="No transactions"
          description="Transactions will appear after backend activity endpoints are available."
        />
      ) : (
        <Card>
          <CardHeader>
            <CardTitle>All activity</CardTitle>
          </CardHeader>
          <CardContent>
            <TransactionTable transactions={transactions} />
          </CardContent>
        </Card>
      )}
    </div>
  );
}
