import { Badge } from "@/components/ui/badge";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  formatDateTime,
  formatSignedCurrencyAmount,
} from "@/lib/format";
import type { Transaction } from "@/types/banking";

interface TransactionTableProps {
  transactions: Transaction[];
}

export function TransactionTable({ transactions }: TransactionTableProps) {
  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Transaction</TableHead>
          <TableHead className="hidden md:table-cell">Channel</TableHead>
          <TableHead className="hidden lg:table-cell">Reference</TableHead>
          <TableHead>Status</TableHead>
          <TableHead className="text-right">Amount</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {transactions.map((transaction) => (
          <TableRow key={transaction.id}>
            <TableCell>
              <div className="min-w-0">
                <p className="font-medium">{transaction.description}</p>
                <p className="text-xs text-muted-foreground">
                  {formatDateTime(transaction.postedAt)} - {transaction.category}
                </p>
              </div>
            </TableCell>
            <TableCell className="hidden md:table-cell">
              {transaction.channel}
            </TableCell>
            <TableCell className="hidden max-w-44 truncate lg:table-cell">
              {transaction.reference}
            </TableCell>
            <TableCell>
              <Badge
                variant={
                  transaction.status === "POSTED"
                    ? "success"
                    : transaction.status === "PENDING"
                      ? "warning"
                      : "secondary"
                }
              >
                {transaction.status}
              </Badge>
            </TableCell>
            <TableCell
              className={
                transaction.direction === "credit"
                  ? "text-right font-semibold text-emerald-700 dark:text-emerald-300"
                  : "text-right font-semibold text-foreground"
              }
            >
              {formatSignedCurrencyAmount(
                transaction.amount,
                transaction.currency,
                transaction.direction,
              )}
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}
