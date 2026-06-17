"use client";

import Link from "next/link";
import { useQuery } from "@tanstack/react-query";
import { ArrowLeft, ArrowRightLeft } from "lucide-react";

import { getAccount } from "@/api/accounts";
import { getAccountTransactions } from "@/api/transactions";
import { ErrorState } from "@/components/banking/error-state";
import { LoadingState } from "@/components/banking/loading-state";
import { PageHeader } from "@/components/banking/page-header";
import { TransactionTable } from "@/components/banking/transaction-table";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { formatCurrencyAmount, formatDate } from "@/lib/format";
import { queryKeys } from "@/lib/query-keys";

interface AccountDetailViewProps {
  accountId: string;
}

export function AccountDetailView({ accountId }: AccountDetailViewProps) {
  const accountQuery = useQuery({
    queryKey: queryKeys.account(accountId),
    queryFn: () => getAccount(accountId),
  });
  const transactionsQuery = useQuery({
    queryKey: queryKeys.accountTransactions(accountId),
    queryFn: () => getAccountTransactions(accountId),
  });

  if (accountQuery.isLoading || transactionsQuery.isLoading) {
    return <LoadingState />;
  }

  if (accountQuery.isError || transactionsQuery.isError || !accountQuery.data) {
    return (
      <ErrorState
        title="Account unavailable"
        description="The account details could not be loaded."
        onRetry={() => {
          void accountQuery.refetch();
          void transactionsQuery.refetch();
        }}
      />
    );
  }

  const account = accountQuery.data;

  return (
    <div className="grid gap-6">
      <PageHeader
        eyebrow="Account detail"
        title={account.nickname}
        description={`${account.productName} - ${account.maskedAccountNumber}`}
        actions={
          <>
            <Button asChild variant="outline">
              <Link href="/accounts">
                <ArrowLeft className="size-4" />
                Accounts
              </Link>
            </Button>
            <Button asChild>
              <Link href="/transfers">
                <ArrowRightLeft className="size-4" />
                Transfer
              </Link>
            </Button>
          </>
        }
      />

      <div className="grid gap-4 lg:grid-cols-3">
        <Card>
          <CardHeader>
            <CardTitle>Available</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-3xl font-semibold tracking-normal">
              {formatCurrencyAmount(account.availableBalance, account.currency)}
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader>
            <CardTitle>Current</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-3xl font-semibold tracking-normal">
              {formatCurrencyAmount(account.currentBalance, account.currency)}
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader>
            <CardTitle>Status</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            <Badge variant={account.status === "ACTIVE" ? "success" : "warning"}>
              {account.status}
            </Badge>
            <p className="text-sm text-muted-foreground">
              Opened {formatDate(account.openedAt)}
            </p>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Account transactions</CardTitle>
        </CardHeader>
        <CardContent>
          <TransactionTable transactions={transactionsQuery.data ?? []} />
        </CardContent>
      </Card>
    </div>
  );
}
