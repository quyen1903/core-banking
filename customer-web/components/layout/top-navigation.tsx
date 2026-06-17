import { Bell, Search } from "lucide-react";

import { MobileSidebar } from "./mobile-sidebar";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

export function TopNavigation() {
  return (
    <header className="sticky top-0 z-30 border-b bg-background/95 backdrop-blur">
      <div className="flex h-16 items-center gap-3 px-4 sm:px-6 lg:px-8">
        <MobileSidebar />
        <div className="relative hidden max-w-sm flex-1 md:block">
          <Search className="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            className="pl-9"
            placeholder="Search accounts and activity"
            aria-label="Search accounts and activity"
          />
        </div>
        <div className="ml-auto flex items-center gap-2">
          <Button variant="ghost" size="icon">
            <Bell className="size-5" />
            <span className="sr-only">Notifications</span>
          </Button>
          <Avatar className="size-9">
            <AvatarFallback>QD</AvatarFallback>
          </Avatar>
        </div>
      </div>
    </header>
  );
}
