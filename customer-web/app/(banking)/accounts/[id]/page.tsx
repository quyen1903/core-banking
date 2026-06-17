import { AccountDetailView } from "@/features/accounts/account-detail-view";

interface AccountPageProps {
  params: Promise<{
    id: string;
  }>;
}

export default async function AccountPage({ params }: AccountPageProps) {
  const { id } = await params;

  return <AccountDetailView accountId={id} />;
}
