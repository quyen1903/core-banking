"use client";

import { useQuery } from "@tanstack/react-query";
import { Landmark } from "lucide-react";

import { getAccounts } from "@/api/accounts";
import { AccountCard } from "@/components/banking/account-card";
import { EmptyState } from "@/components/banking/empty-state";
import { ErrorState } from "@/components/banking/error-state";
import { LoadingState } from "@/components/banking/loading-state";
import { PageHeader } from "@/components/banking/page-header";
import { queryKeys } from "@/lib/query-keys";

export function AccountsView() {
  const accountsQuery = useQuery({
    queryKey: queryKeys.accounts,
    queryFn: getAccounts,
  });

  if (accountsQuery.isLoading) {
    return <LoadingState />;
  }

  if (accountsQuery.isError) {
    return <ErrorState onRetry={() => void accountsQuery.refetch()} />;
  }

  const accounts = accountsQuery.data ?? [];

  return (
    <div className="grid gap-6">
      <PageHeader
        eyebrow="Accounts"
        title="Your accounts"
        description="Review balances and account status returned by QuinnBank Core or demo data."
      />
      {accounts.length === 0 ? (
        <EmptyState
          icon={Landmark}
          title="No accounts available"
          description="Accounts will appear after the backend returns customer-scoped account data."
        />
      ) : (
        <div className="grid gap-4 lg:grid-cols-3">
          {accounts.map((account) => (
            <AccountCard key={account.id} account={account} />
          ))}
        </div>
      )}
    </div>
  );
}
