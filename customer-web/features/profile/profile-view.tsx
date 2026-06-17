"use client";

import { useQuery } from "@tanstack/react-query";
import { ShieldCheck, UserRound } from "lucide-react";

import { getProfile } from "@/api/profile";
import { ErrorState } from "@/components/banking/error-state";
import { LoadingState } from "@/components/banking/loading-state";
import { PageHeader } from "@/components/banking/page-header";
import { Badge } from "@/components/ui/badge";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { queryKeys } from "@/lib/query-keys";

export function ProfileView() {
  const profileQuery = useQuery({
    queryKey: queryKeys.profile,
    queryFn: getProfile,
  });

  if (profileQuery.isLoading) {
    return <LoadingState />;
  }

  if (profileQuery.isError || !profileQuery.data) {
    return <ErrorState onRetry={() => void profileQuery.refetch()} />;
  }

  const profile = profileQuery.data;

  return (
    <div className="grid gap-6">
      <PageHeader
        eyebrow="Profile"
        title="Customer profile"
        description="Profile data is shown from a customer-scoped read model."
      />
      <div className="grid gap-4 lg:grid-cols-[0.8fr_1.2fr]">
        <Card>
          <CardHeader>
            <div className="flex size-12 items-center justify-center rounded-md bg-primary/10">
              <UserRound className="size-6 text-primary" />
            </div>
            <CardTitle>{profile.fullName}</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3 text-sm">
            <div className="flex items-center justify-between gap-4">
              <span className="text-muted-foreground">Customer number</span>
              <span className="font-medium">{profile.customerNumber}</span>
            </div>
            <Separator />
            <div className="flex items-center justify-between gap-4">
              <span className="text-muted-foreground">Status</span>
              <Badge variant="success">{profile.status}</Badge>
            </div>
            <div className="flex items-center justify-between gap-4">
              <span className="text-muted-foreground">KYC</span>
              <Badge variant="success">{profile.kycStatus}</Badge>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Contact and controls</CardTitle>
          </CardHeader>
          <CardContent className="grid gap-5">
            <div className="grid gap-4 sm:grid-cols-2">
              <div>
                <p className="text-sm text-muted-foreground">Email</p>
                <p className="font-medium">{profile.email}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Phone</p>
                <p className="font-medium">{profile.phone}</p>
              </div>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">Mailing address</p>
              <p className="font-medium">{profile.mailingAddress}</p>
            </div>
            <div className="flex items-start gap-3 rounded-md border bg-secondary p-4">
              <ShieldCheck className="mt-0.5 size-5 text-primary" />
              <p className="text-sm text-muted-foreground">
                High-risk profile changes require backend authorization,
                verification, and audit before they affect customer records.
              </p>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
