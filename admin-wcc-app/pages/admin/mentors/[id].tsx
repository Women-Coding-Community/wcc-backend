import { useEffect } from 'react';
import { useRouter } from 'next/router';
import AdminLayout from '@/components/AdminLayout';
import EditMentorForm from '@/components/EditMentor/EditMentorForm';
import { getStoredToken, isTokenExpired } from '@/lib/auth';

export default function EditMentorPage() {
  const router = useRouter();
  const { id } = router.query;

  useEffect(() => {
    const token = getStoredToken();
    if (!token || isTokenExpired(token)) {
      router.replace('/login');
    }
  }, [router]);

  if (!id || typeof id !== 'string') return null;

  return (
    <AdminLayout>
      <EditMentorForm mentorId={id} />
    </AdminLayout>
  );
}
