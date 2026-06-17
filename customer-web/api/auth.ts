import { delay } from "./mock-data";

export interface LoginRequest {
  email: string;
  password: string;
}

export async function authenticateCustomer(request: LoginRequest) {
  // TODO: Replace with the backend session/login endpoint when QuinnBank Core
  // exposes customer authentication. Do not store tokens in localStorage.
  void request;
  return delay({ status: "DEMO_SESSION" as const }, 450);
}
