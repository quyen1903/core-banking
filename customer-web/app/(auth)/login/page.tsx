import Link from "next/link";
import { Landmark, LockKeyhole, ShieldCheck } from "lucide-react";

import { LoginForm } from "@/features/auth/login-form";
import { Card, CardContent } from "@/components/ui/card";

export default function LoginPage() {
  return (
    <main className="min-h-screen bg-background">
      <div className="grid min-h-screen lg:grid-cols-[1fr_440px]">
        <section className="relative hidden overflow-hidden border-r bg-sidebar text-sidebar-foreground lg:block">
          <div className="absolute inset-0 opacity-15 [background-image:linear-gradient(90deg,rgba(255,255,255,.14)_1px,transparent_1px),linear-gradient(rgba(255,255,255,.14)_1px,transparent_1px)] [background-size:44px_44px]" />
          <div className="relative flex h-full flex-col justify-between p-10">
            <div className="flex items-center gap-3">
              <div className="flex size-10 items-center justify-center rounded-md bg-sidebar-primary text-sidebar-primary-foreground">
                <ShieldCheck className="size-5" />
              </div>
              <div>
                <p className="text-base font-semibold">QuinnBank</p>
                <p className="text-sm text-sidebar-foreground/70">
                  Customer banking
                </p>
              </div>
            </div>

            <div className="max-w-xl space-y-8">
              <div className="space-y-4">
                <p className="text-sm font-medium uppercase tracking-normal text-sidebar-primary">
                  Internet banking
                </p>
                <h1 className="max-w-2xl text-5xl font-semibold leading-tight tracking-normal">
                  Serious banking workflows with a calm customer experience.
                </h1>
                <p className="max-w-lg text-base leading-7 text-sidebar-foreground/72">
                  The browser presents account information and sends requests.
                  QuinnBank Core remains the source of truth.
                </p>
              </div>

              <div className="grid max-w-2xl gap-4 sm:grid-cols-2">
                <Card className="border-sidebar-border bg-sidebar-accent text-sidebar-foreground">
                  <CardContent className="space-y-3 p-5">
                    <Landmark className="size-5 text-sidebar-primary" />
                    <p className="text-sm font-medium">Account visibility</p>
                    <p className="text-sm text-sidebar-foreground/65">
                      Balances and activity are treated as backend-owned data.
                    </p>
                  </CardContent>
                </Card>
                <Card className="border-sidebar-border bg-sidebar-accent text-sidebar-foreground">
                  <CardContent className="space-y-3 p-5">
                    <LockKeyhole className="size-5 text-sidebar-primary" />
                    <p className="text-sm font-medium">Controlled actions</p>
                    <p className="text-sm text-sidebar-foreground/65">
                      Transfer requests carry idempotency for server replay
                      protection.
                    </p>
                  </CardContent>
                </Card>
              </div>
            </div>

            <Link
              href="/login"
              className="w-fit rounded-md text-sm font-medium text-sidebar-foreground/80"
            >
              quinnbank.example.invalid
            </Link>
          </div>
        </section>

        <section className="flex min-h-screen items-center justify-center px-4 py-10 sm:px-6 lg:px-8">
          <div className="w-full max-w-md space-y-6">
            <div className="space-y-2 lg:hidden">
              <div className="flex items-center gap-3">
                <div className="flex size-10 items-center justify-center rounded-md bg-primary text-primary-foreground">
                  <ShieldCheck className="size-5" />
                </div>
                <div>
                  <p className="font-semibold">QuinnBank</p>
                  <p className="text-sm text-muted-foreground">
                    Customer banking
                  </p>
                </div>
              </div>
            </div>
            <LoginForm />
          </div>
        </section>
      </div>
    </main>
  );
}
