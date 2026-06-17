const currencySymbols: Record<string, string> = {
  USD: "$",
  EUR: "EUR ",
  GBP: "GBP ",
  VND: "VND ",
};

export function formatCurrencyAmount(amount: string, currency: string) {
  const sign = amount.trim().startsWith("-") ? "-" : "";
  const unsigned = amount.trim().replace(/^-/, "");
  const [wholePartRaw, fractionalPartRaw = ""] = unsigned.split(".");
  const wholePart = wholePartRaw.replace(/^0+(?=\d)/, "") || "0";
  const groupedWhole = wholePart.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  const fraction = fractionalPartRaw.padEnd(2, "0").slice(0, 2);
  const symbol = currencySymbols[currency] ?? `${currency} `;

  return `${sign}${symbol}${groupedWhole}.${fraction}`;
}

export function formatSignedCurrencyAmount(
  amount: string,
  currency: string,
  direction: "credit" | "debit",
) {
  const prefix = direction === "credit" ? "+" : "-";
  return `${prefix}${formatCurrencyAmount(amount, currency)}`;
}

export function formatDate(value: string) {
  return new Intl.DateTimeFormat("en-US", {
    month: "short",
    day: "2-digit",
    year: "numeric",
  }).format(new Date(value));
}

export function formatDateTime(value: string) {
  return new Intl.DateTimeFormat("en-US", {
    month: "short",
    day: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date(value));
}

export function normalizeDecimalString(value: unknown) {
  if (typeof value === "string") {
    return value;
  }

  if (typeof value === "number" && Number.isFinite(value)) {
    // TODO: Prefer backend money fields serialized as strings to avoid browser
    // numeric precision loss before this formatter receives the value.
    return value.toString();
  }

  return "0.00";
}
