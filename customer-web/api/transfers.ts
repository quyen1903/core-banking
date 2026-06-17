import { delay, DEMO_TRANSFER_RECEIPTS } from "./mock-data";
import type { TransferReceipt, TransferRequest } from "@/types/banking";

export async function getRecentTransferReceipts() {
  // TODO: Replace with backend transfer history once transfer workflow exists.
  return delay(DEMO_TRANSFER_RECEIPTS);
}

export async function createTransfer(
  request: TransferRequest,
  idempotencyKey: string,
): Promise<TransferReceipt> {
  // TODO: Replace with POST /api/v1/transfers using the Idempotency-Key header.
  // The backend must own authorization, account ownership, validation, limits,
  // posting, audit, replay behavior, and final transfer state.
  void request;

  return delay(
    {
      id: `transfer-demo-${idempotencyKey.slice(-8)}`,
      idempotencyKey,
      status: "PENDING_BACKEND",
      submittedAt: new Date().toISOString(),
      message: "Transfer request is waiting for backend workflow integration.",
    },
    650,
  );
}
