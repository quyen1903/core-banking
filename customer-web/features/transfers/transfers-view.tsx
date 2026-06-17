"use client";

import { useQuery } from "@tanstack/react-query";
import { ArrowRightLeft } from "lucide-react";

import { getAccounts } from "@/api/accounts";
import { getRecentTransferReceipts } from "@/api/transfers";
import { EmptyState } from "@/components/banking/empty-state";
import { ErrorState } from "@/components/banking/error-state";
import { LoadingState } from "@/components/banking/loading-state";
import { PageHeader } from "@/components/banking/page-header";
import { TransferForm } from "@/components/banking/transfer-form";
import { Badge } from "@/components/ui/badge";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { formatDateTime } from "@/lib/format";
import { queryKeys } from "@/lib/query-keys";

export function TransfersView() {
  const accountsQuery = useQuery({
    queryKey: queryKeys.accounts,
    queryFn: getAccounts,
  });
  const receiptsQuery = useQuery({
    queryKey: ["transfer-receipts"],
    queryFn: getRecentTransferReceipts,
  });

  if (accountsQuery.isLoading || receiptsQuery.isLoading) {
    return <LoadingState />;
  }

  if (accountsQuery.isError || receiptsQuery.isError) {
    return (
      <ErrorState
        onRetry={() => {
          void accountsQuery.refetch();
          void receiptsQuery.refetch();
        }}
      />
    );
  }

  const accounts = accountsQuery.data ?? [];
  const receipts = receiptsQuery.data ?? [];

  return (
    <div className="grid gap-6">
      <PageHeader
        eyebrow="Transfers"
        title="Move money"
        description="Create a transfer request with a unique idempotency key for backend submission."
      />
      {accounts.length === 0 ? (
        <EmptyState
          icon={ArrowRightLeft}
          title="No funding account"
          description="A customer-scoped account is required before transfer requests can be created."
        />
      ) : (
        <div className="grid gap-6 xl:grid-cols-[1.1fr_0.9fr]">
          <TransferForm accounts={accounts} />
          <Card>
            <CardHeader>
              <CardTitle>Recent transfer requests</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              {receipts.map((receipt) => (
                <div
                  key={receipt.id}
                  className="rounded-md border bg-background p-4"
                >
                  <div className="flex items-center justify-between gap-3">
                    <p className="truncate text-sm font-medium">{receipt.id}</p>
                    <Badge variant="warning">{receipt.status}</Badge>
                  </div>
                  <p className="mt-2 text-sm text-muted-foreground">
                    {receipt.message}
                  </p>
                  <p className="mt-2 truncate text-xs text-muted-foreground">
                    {receipt.idempotencyKey}
                  </p>
                  <p className="text-xs text-muted-foreground">
                    {formatDateTime(receipt.submittedAt)}
                  </p>
                </div>
              ))}
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}
