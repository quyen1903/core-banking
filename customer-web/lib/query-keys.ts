export const queryKeys = {
  accounts: ["accounts"] as const,
  account: (accountId: string) => ["accounts", accountId] as const,
  transactions: ["transactions"] as const,
  accountTransactions: (accountId: string) =>
    ["transactions", "account", accountId] as const,
  dashboard: ["dashboard"] as const,
  profile: ["profile"] as const,
};
