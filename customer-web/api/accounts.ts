import { apiFetch, hasBackendApi } from "./http";
import { delay, DEMO_ACCOUNTS, findDemoAccount } from "./mock-data";
import { normalizeDecimalString } from "@/lib/format";
import type { AccountResponse, OpenAccountRequest } from "@/types/api";
import type { BankAccount } from "@/types/banking";

function mapAccountResponse(response: AccountResponse): BankAccount {
  return {
    id: response.id,
    customerId: response.customerId,
    productId: response.productId,
    productCode: "BACKEND_PRODUCT",
    productName: "QuinnBank Account",
    nickname: "Linked account",
    maskedAccountNumber: response.maskedAccountNumber,
    currency: response.currency,
    availableBalance: normalizeDecimalString(response.availableBalance),
    currentBalance: normalizeDecimalString(response.currentBalance),
    status: response.status,
    openedAt: new Date().toISOString(),
  };
}

export async function getAccounts() {
  // TODO: Replace with GET /api/v1/accounts when the backend exposes a
  // customer-scoped account list endpoint.
  return delay(DEMO_ACCOUNTS);
}

export async function getAccount(accountId: string) {
  if (hasBackendApi()) {
    const response = await apiFetch<AccountResponse>(
      `/api/v1/accounts/${accountId}`,
    );
    return mapAccountResponse(response);
  }

  const account = findDemoAccount(accountId);

  if (!account) {
    throw new Error("Account was not found.");
  }

  return delay(account);
}

export async function openAccount(
  request: OpenAccountRequest,
  idempotencyKey: string,
) {
  const response = await apiFetch<AccountResponse>("/api/v1/accounts", {
    method: "POST",
    headers: {
      "Idempotency-Key": idempotencyKey,
    },
    body: JSON.stringify(request),
  });

  return mapAccountResponse(response);
}
