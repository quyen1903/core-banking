import { delay, DEMO_CUSTOMER } from "./mock-data";

export async function getProfile() {
  // TODO: Replace with an authenticated customer profile endpoint. The frontend
  // must not infer customer identity from route params or client storage.
  return delay(DEMO_CUSTOMER);
}
