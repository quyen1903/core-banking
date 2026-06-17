import type {
  BankAccount,
  CustomerProfile,
  DashboardSummary,
  Transaction,
  TransferReceipt,
} from "@/types/banking";

export const DEMO_CUSTOMER: CustomerProfile = {
  id: "9f35bf0d-f6ef-4b8a-90ad-828e5b8d9991",
  customerNumber: "CIF-DEMO-10001",
  fullName: "Quinn Demo Customer",
  email: "quinn.customer@example.invalid",
  phone: "+1 555 010 2048",
  status: "ACTIVE",
  kycStatus: "VERIFIED",
  riskRating: "LOW",
  mailingAddress: "100 Market Street, Suite 1200, Example City",
};

export const DEMO_ACCOUNTS: BankAccount[] = [
  {
    id: "7e9119ef-0e35-42f4-b55e-1916d2a309c2",
    customerId: DEMO_CUSTOMER.id,
    productId: "0e669ef1-853f-49a8-9a20-53a9d2bd9d11",
    productCode: "QB_CHECKING",
    productName: "Quinn Everyday Checking",
    nickname: "Operating account",
    maskedAccountNumber: "****1842",
    currency: "USD",
    availableBalance: "18420.55",
    currentBalance: "18740.55",
    status: "ACTIVE",
    openedAt: "2026-03-18T14:10:00.000Z",
  },
  {
    id: "ddbb3f8c-6138-481d-956d-26c45e532a48",
    customerId: DEMO_CUSTOMER.id,
    productId: "0c9013ff-9af0-4762-a5c4-632d2c0fbc8f",
    productCode: "QB_SAVINGS",
    productName: "Quinn Reserve Savings",
    nickname: "Reserve account",
    maskedAccountNumber: "****9037",
    currency: "USD",
    availableBalance: "64210.00",
    currentBalance: "64210.00",
    status: "ACTIVE",
    openedAt: "2025-11-02T09:00:00.000Z",
  },
  {
    id: "9dd0f7f1-5418-4652-8179-3027921f5aef",
    customerId: DEMO_CUSTOMER.id,
    productId: "d695af18-ec4f-4863-92ec-d18b70fddba2",
    productCode: "QB_ESCROW",
    productName: "Quinn Managed Escrow",
    nickname: "Project escrow",
    maskedAccountNumber: "****4470",
    currency: "USD",
    availableBalance: "12500.00",
    currentBalance: "12500.00",
    status: "PENDING",
    openedAt: "2026-05-21T16:40:00.000Z",
  },
];

export const DEMO_DASHBOARD: DashboardSummary = {
  totalAvailable: { amount: "95130.55", currency: "USD" },
  totalCurrent: { amount: "95450.55", currency: "USD" },
  monthInflow: { amount: "14280.00", currency: "USD" },
  monthOutflow: { amount: "8342.18", currency: "USD" },
  pendingTransfers: "2",
};

export const DEMO_TRANSACTIONS: Transaction[] = [
  {
    id: "txn-20260610-001",
    accountId: DEMO_ACCOUNTS[0].id,
    postedAt: "2026-06-10T15:42:00.000Z",
    description: "Payroll deposit",
    category: "Income",
    amount: "7280.00",
    currency: "USD",
    direction: "credit",
    status: "POSTED",
    reference: "ACH-DEMO-8291",
    channel: "ACH",
  },
  {
    id: "txn-20260609-002",
    accountId: DEMO_ACCOUNTS[0].id,
    postedAt: "2026-06-09T18:25:00.000Z",
    description: "Vendor payment",
    category: "Operations",
    amount: "1840.25",
    currency: "USD",
    direction: "debit",
    status: "POSTED",
    reference: "BILL-DEMO-6114",
    channel: "Online banking",
  },
  {
    id: "txn-20260608-003",
    accountId: DEMO_ACCOUNTS[1].id,
    postedAt: "2026-06-08T11:03:00.000Z",
    description: "Internal transfer",
    category: "Transfer",
    amount: "3000.00",
    currency: "USD",
    direction: "credit",
    status: "POSTED",
    reference: "TRF-DEMO-3008",
    channel: "Mobile",
  },
  {
    id: "txn-20260607-004",
    accountId: DEMO_ACCOUNTS[0].id,
    postedAt: "2026-06-07T12:10:00.000Z",
    description: "Card settlement",
    category: "Card",
    amount: "320.00",
    currency: "USD",
    direction: "debit",
    status: "PENDING",
    reference: "CARD-DEMO-2040",
    channel: "Card network",
  },
  {
    id: "txn-20260605-005",
    accountId: DEMO_ACCOUNTS[1].id,
    postedAt: "2026-06-05T17:54:00.000Z",
    description: "Reserve allocation",
    category: "Savings",
    amount: "5000.00",
    currency: "USD",
    direction: "credit",
    status: "POSTED",
    reference: "ALLOC-DEMO-5029",
    channel: "Online banking",
  },
  {
    id: "txn-20260603-006",
    accountId: DEMO_ACCOUNTS[0].id,
    postedAt: "2026-06-03T08:30:00.000Z",
    description: "Facilities payment",
    category: "Operations",
    amount: "2100.00",
    currency: "USD",
    direction: "debit",
    status: "POSTED",
    reference: "BILL-DEMO-7004",
    channel: "Online banking",
  },
];

export const DEMO_TRANSFER_RECEIPTS: TransferReceipt[] = [
  {
    id: "transfer-demo-001",
    idempotencyKey: "qb-web-demo-001",
    status: "PENDING_BACKEND",
    submittedAt: "2026-06-11T09:15:00.000Z",
    message: "Awaiting backend transfer workflow.",
  },
  {
    id: "transfer-demo-002",
    idempotencyKey: "qb-web-demo-002",
    status: "PENDING_BACKEND",
    submittedAt: "2026-06-10T14:20:00.000Z",
    message: "Awaiting backend transfer workflow.",
  },
];

export function findDemoAccount(accountId: string) {
  return DEMO_ACCOUNTS.find((account) => account.id === accountId);
}

export function findDemoTransactions(accountId?: string) {
  if (!accountId) {
    return DEMO_TRANSACTIONS;
  }

  return DEMO_TRANSACTIONS.filter(
    (transaction) => transaction.accountId === accountId,
  );
}

export function delay<T>(value: T, ms = 250) {
  return new Promise<T>((resolve) => {
    globalThis.setTimeout(() => resolve(value), ms);
  });
}
