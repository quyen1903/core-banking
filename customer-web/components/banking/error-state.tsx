import { AlertTriangle } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";

interface ErrorStateProps {
  title?: string;
  description?: string;
  onRetry?: () => void;
}

export function ErrorState({
  title = "Unable to load this view",
  description = "The request could not be completed. Please try again.",
  onRetry,
}: ErrorStateProps) {
  return (
    <Card className="border-destructive/30">
      <CardContent className="flex min-h-44 flex-col items-start justify-center gap-4 p-6 sm:flex-row sm:items-center">
        <div className="flex size-10 shrink-0 items-center justify-center rounded-md bg-destructive/10">
          <AlertTriangle className="size-5 text-destructive" />
        </div>
        <div className="min-w-0 flex-1 space-y-1">
          <h3 className="text-base font-semibold">{title}</h3>
          <p className="text-sm text-muted-foreground">{description}</p>
        </div>
        {onRetry ? (
          <Button variant="outline" onClick={onRetry}>
            Retry
          </Button>
        ) : null}
      </CardContent>
    </Card>
  );
}
