export type AccountStatus = "ACTIVE" | "BLOCKED" | "CLOSED" | "PENDING";

export type TransactionDirection = "credit" | "debit";

export type TransactionStatus = "POSTED" | "PENDING" | "REVERSED";

export type TransferStatus = "PENDING_BACKEND" | "SUBMITTED" | "REJECTED";

export interface MoneyAmount {
  amount: string;
  currency: string;
}

export interface BankAccount {
  id: string;
  customerId: string;
  productId: string;
  productCode: string;
  productName: string;
  nickname: string;
  maskedAccountNumber: string;
  currency: string;
  availableBalance: string;
  currentBalance: string;
  status: AccountStatus;
  openedAt: string;
}

export interface DashboardSummary {
  totalAvailable: MoneyAmount;
  totalCurrent: MoneyAmount;
  monthInflow: MoneyAmount;
  monthOutflow: MoneyAmount;
  pendingTransfers: string;
}

export interface Transaction {
  id: string;
  accountId: string;
  postedAt: string;
  description: string;
  category: string;
  amount: string;
  currency: string;
  direction: TransactionDirection;
  status: TransactionStatus;
  reference: string;
  channel: string;
}

export interface CustomerProfile {
  id: string;
  customerNumber: string;
  fullName: string;
  email: string;
  phone: string;
  status: string;
  kycStatus: string;
  riskRating: string;
  mailingAddress: string;
}

export interface TransferRequest {
  fromAccountId: string;
  beneficiaryName: string;
  beneficiaryAccount: string;
  routingCode: string;
  amount: string;
  currency: string;
  memo?: string;
  scheduledDate?: string;
}

export interface TransferReceipt {
  id: string;
  idempotencyKey: string;
  status: TransferStatus;
  submittedAt: string;
  message: string;
}
