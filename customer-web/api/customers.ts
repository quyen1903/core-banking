import { apiFetch, hasBackendApi } from "./http";
import { delay, DEMO_CUSTOMER } from "./mock-data";
import type {
  CustomerResponse,
  RegisterCustomerRequest,
} from "@/types/api";

export async function getCustomer(customerId: string) {
  if (hasBackendApi()) {
    return apiFetch<CustomerResponse>(`/api/v1/customers/${customerId}`);
  }

  return delay(DEMO_CUSTOMER);
}

export async function registerCustomer(request: RegisterCustomerRequest) {
  return apiFetch<CustomerResponse>("/api/v1/customers", {
    method: "POST",
    body: JSON.stringify(request),
  });
}
