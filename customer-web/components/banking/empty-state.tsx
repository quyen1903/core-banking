import type { LucideIcon } from "lucide-react";

import { Card, CardContent } from "@/components/ui/card";

interface EmptyStateProps {
  icon: LucideIcon;
  title: string;
  description: string;
}

export function EmptyState({ icon: Icon, title, description }: EmptyStateProps) {
  return (
    <Card>
      <CardContent className="flex min-h-52 flex-col items-center justify-center gap-3 p-8 text-center">
        <div className="flex size-11 items-center justify-center rounded-md bg-secondary">
          <Icon className="size-5 text-muted-foreground" />
        </div>
        <div className="space-y-1">
          <h3 className="text-base font-semibold">{title}</h3>
          <p className="max-w-md text-sm text-muted-foreground">{description}</p>
        </div>
      </CardContent>
    </Card>
  );
}
