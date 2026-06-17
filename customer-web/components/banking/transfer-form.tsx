"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation } from "@tanstack/react-query";
import { SendHorizontal } from "lucide-react";
import { Controller, useForm } from "react-hook-form";
import { z } from "zod";

import { createTransfer } from "@/api/transfers";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import { formatDateTime } from "@/lib/format";
import { createIdempotencyKey } from "@/lib/idempotency";
import type { BankAccount, TransferReceipt } from "@/types/banking";

const positiveAmountPattern =
  /^(?!0+(?:\.0{1,2})?$)(?:0|[1-9]\d{0,11})(?:\.\d{1,2})?$/;

const transferFormSchema = z.object({
  fromAccountId: z.string().uuid("Select a funding account."),
  beneficiaryName: z.string().trim().min(2).max(120),
  beneficiaryAccount: z
    .string()
    .trim()
    .min(4)
    .max(34)
    .regex(/^[A-Za-z0-9 -]+$/, "Use letters, numbers, spaces, or hyphens."),
  routingCode: z
    .string()
    .trim()
    .min(4)
    .max(20)
    .regex(/^[A-Za-z0-9-]+$/, "Use letters, numbers, or hyphens."),
  amount: z
    .string()
    .trim()
    .regex(positiveAmountPattern, "Enter a positive amount with up to 2 decimals."),
  currency: z.string().length(3),
  scheduledDate: z.string().optional(),
  memo: z.string().trim().max(140).optional(),
});

type TransferFormValues = z.infer<typeof transferFormSchema>;

interface TransferFormProps {
  accounts: BankAccount[];
}

export function TransferForm({ accounts }: TransferFormProps) {
  const defaultAccount = accounts[0];
  const form = useForm<TransferFormValues>({
    resolver: zodResolver(transferFormSchema),
    defaultValues: {
      fromAccountId: defaultAccount?.id ?? "",
      beneficiaryName: "",
      beneficiaryAccount: "",
      routingCode: "",
      amount: "",
      currency: defaultAccount?.currency ?? "USD",
      scheduledDate: "",
      memo: "",
    },
  });

  const mutation = useMutation<
    TransferReceipt,
    Error,
    { request: TransferFormValues; idempotencyKey: string }
  >({
    mutationFn: ({ request, idempotencyKey }) =>
      createTransfer(request, idempotencyKey),
    onSuccess: () => {
      form.reset({
        fromAccountId: defaultAccount?.id ?? "",
        beneficiaryName: "",
        beneficiaryAccount: "",
        routingCode: "",
        amount: "",
        currency: defaultAccount?.currency ?? "USD",
        scheduledDate: "",
        memo: "",
      });
    },
  });

  function handleAccountChange(accountId: string) {
    const account = accounts.find((item) => item.id === accountId);
    form.setValue("fromAccountId", accountId, { shouldValidate: true });
    form.setValue("currency", account?.currency ?? "USD", {
      shouldValidate: true,
    });
  }

  function onSubmit(values: TransferFormValues) {
    let idempotencyKey: string;

    try {
      idempotencyKey = createIdempotencyKey("qb-transfer");
    } catch {
      form.setError("root", {
        message: "A secure request identifier could not be created.",
      });
      return;
    }

    mutation.mutate({ request: values, idempotencyKey });
  }

  const receipt = mutation.data;

  return (
    <Card>
      <CardHeader>
        <CardTitle>New transfer</CardTitle>
        <CardDescription>
          Transfer requests remain pending until the backend workflow accepts
          them.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form className="grid gap-5" onSubmit={form.handleSubmit(onSubmit)}>
          <div className="grid gap-2">
            <Label htmlFor="fromAccountId">From account</Label>
            <Controller
              control={form.control}
              name="fromAccountId"
              render={({ field }) => (
                <Select
                  value={field.value}
                  onValueChange={(value) => handleAccountChange(value)}
                >
                  <SelectTrigger id="fromAccountId">
                    <SelectValue placeholder="Select account" />
                  </SelectTrigger>
                  <SelectContent>
                    {accounts.map((account) => (
                      <SelectItem key={account.id} value={account.id}>
                        {account.nickname} - {account.maskedAccountNumber}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              )}
            />
            {form.formState.errors.fromAccountId ? (
              <p className="text-sm text-destructive">
                {form.formState.errors.fromAccountId.message}
              </p>
            ) : null}
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <div className="grid gap-2">
              <Label htmlFor="beneficiaryName">Recipient name</Label>
              <Input id="beneficiaryName" {...form.register("beneficiaryName")} />
              {form.formState.errors.beneficiaryName ? (
                <p className="text-sm text-destructive">
                  {form.formState.errors.beneficiaryName.message}
                </p>
              ) : null}
            </div>
            <div className="grid gap-2">
              <Label htmlFor="beneficiaryAccount">Recipient account</Label>
              <Input
                id="beneficiaryAccount"
                autoComplete="off"
                {...form.register("beneficiaryAccount")}
              />
              {form.formState.errors.beneficiaryAccount ? (
                <p className="text-sm text-destructive">
                  {form.formState.errors.beneficiaryAccount.message}
                </p>
              ) : null}
            </div>
          </div>

          <div className="grid gap-4 md:grid-cols-3">
            <div className="grid gap-2">
              <Label htmlFor="routingCode">Routing code</Label>
              <Input
                id="routingCode"
                autoComplete="off"
                {...form.register("routingCode")}
              />
              {form.formState.errors.routingCode ? (
                <p className="text-sm text-destructive">
                  {form.formState.errors.routingCode.message}
                </p>
              ) : null}
            </div>
            <div className="grid gap-2">
              <Label htmlFor="amount">Amount</Label>
              <Input
                id="amount"
                inputMode="decimal"
                placeholder="0.00"
                {...form.register("amount")}
              />
              {form.formState.errors.amount ? (
                <p className="text-sm text-destructive">
                  {form.formState.errors.amount.message}
                </p>
              ) : null}
            </div>
            <div className="grid gap-2">
              <Label htmlFor="scheduledDate">Schedule date</Label>
              <Input
                id="scheduledDate"
                type="date"
                {...form.register("scheduledDate")}
              />
            </div>
          </div>

          <div className="grid gap-2">
            <Label htmlFor="memo">Memo</Label>
            <Textarea id="memo" {...form.register("memo")} />
            {form.formState.errors.memo ? (
              <p className="text-sm text-destructive">
                {form.formState.errors.memo.message}
              </p>
            ) : null}
          </div>

          {form.formState.errors.root ? (
            <p className="text-sm text-destructive">
              {form.formState.errors.root.message}
            </p>
          ) : null}

          {mutation.error ? (
            <p className="text-sm text-destructive">
              The transfer request could not be submitted.
            </p>
          ) : null}

          {receipt ? (
            <div className="rounded-md border bg-secondary p-4 text-sm">
              <p className="font-medium">{receipt.message}</p>
              <p className="mt-1 text-muted-foreground">
                Request: {receipt.idempotencyKey}
              </p>
              <p className="text-muted-foreground">
                Created: {formatDateTime(receipt.submittedAt)}
              </p>
            </div>
          ) : null}

          <Button
            type="submit"
            className="w-full sm:w-fit"
            disabled={mutation.isPending || accounts.length === 0}
          >
            <SendHorizontal className="size-4" />
            {mutation.isPending ? "Submitting" : "Submit transfer"}
          </Button>
        </form>
      </CardContent>
    </Card>
  );
}
