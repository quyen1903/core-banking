import type { BankAccount, CustomerProfile } from "./banking";

export interface ApiErrorBody {
  code: string;
  message: string;
  correlationId?: string;
}

export interface AccountResponse {
  id: string;
  maskedAccountNumber: string;
  customerId: string;
  productId: string;
  currency: string;
  availableBalance: string | number;
  currentBalance: string | number;
  status: BankAccount["status"];
}

export interface OpenAccountRequest {
  customerId: string;
  productCode: string;
}

export type CustomerResponse = Pick<
  CustomerProfile,
  | "id"
  | "customerNumber"
  | "fullName"
  | "email"
  | "phone"
  | "status"
  | "kycStatus"
  | "riskRating"
>;

export interface RegisterCustomerRequest {
  fullName: string;
  email?: string;
  phone?: string;
}
