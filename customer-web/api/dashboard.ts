import { delay, DEMO_DASHBOARD } from "./mock-data";

export async function getDashboardSummary() {
  // TODO: Replace with an authenticated backend dashboard summary endpoint.
  return delay(DEMO_DASHBOARD);
}
