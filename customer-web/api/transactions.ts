import { delay, findDemoTransactions } from "./mock-data";

export async function getTransactions() {
  // TODO: Replace with a backend transaction query endpoint scoped by the
  // authenticated customer and authorized account set.
  return delay(findDemoTransactions());
}

export async function getAccountTransactions(accountId: string) {
  // TODO: Replace with a backend endpoint such as
  // GET /api/v1/accounts/{accountId}/transactions.
  return delay(findDemoTransactions(accountId));
}
