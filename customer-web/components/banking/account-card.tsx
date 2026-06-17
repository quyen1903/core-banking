import Link from "next/link";
import { ArrowUpRight, Landmark } from "lucide-react";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { formatCurrencyAmount, formatDate } from "@/lib/format";
import type { BankAccount } from "@/types/banking";

interface AccountCardProps {
  account: BankAccount;
}

export function AccountCard({ account }: AccountCardProps) {
  const statusVariant =
    account.status === "ACTIVE"
      ? "success"
      : account.status === "PENDING"
        ? "warning"
        : "secondary";

  return (
    <Card className="overflow-hidden">
      <CardHeader className="flex flex-row items-start justify-between gap-4 space-y-0">
        <div className="min-w-0 space-y-2">
          <div className="flex items-center gap-2">
            <div className="flex size-9 shrink-0 items-center justify-center rounded-md bg-primary/10">
              <Landmark className="size-4 text-primary" />
            </div>
            <div className="min-w-0">
              <CardTitle className="truncate text-base">
                {account.nickname}
              </CardTitle>
              <p className="truncate text-sm text-muted-foreground">
                {account.productName}
              </p>
            </div>
          </div>
        </div>
        <Badge variant={statusVariant}>{account.status}</Badge>
      </CardHeader>
      <CardContent className="space-y-5">
        <div>
          <p className="text-xs font-medium uppercase tracking-normal text-muted-foreground">
            Available
          </p>
          <p className="mt-1 text-2xl font-semibold tracking-normal">
            {formatCurrencyAmount(account.availableBalance, account.currency)}
          </p>
        </div>
        <div className="grid gap-3 text-sm sm:grid-cols-2">
          <div>
            <p className="text-muted-foreground">Account</p>
            <p className="font-medium">{account.maskedAccountNumber}</p>
          </div>
          <div>
            <p className="text-muted-foreground">Opened</p>
            <p className="font-medium">{formatDate(account.openedAt)}</p>
          </div>
        </div>
        <Button asChild variant="outline" className="w-full">
          <Link href={`/accounts/${account.id}`}>
            Details
            <ArrowUpRight className="size-4" />
          </Link>
        </Button>
      </CardContent>
    </Card>
  );
}
