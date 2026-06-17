export function createIdempotencyKey(prefix = "qb-web") {
  if (!globalThis.crypto?.randomUUID) {
    throw new Error("Secure browser id generation is unavailable.");
  }

  return `${prefix}-${globalThis.crypto.randomUUID()}`;
}
