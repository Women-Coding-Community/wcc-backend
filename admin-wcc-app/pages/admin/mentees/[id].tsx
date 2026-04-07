import { useEffect } from 'react';
import Router, { useRouter } from 'next/router';
import { Button, Paper, Typography } from '@mui/material';
import AdminLayout from '@/components/AdminLayout';
import { useAuth } from '@/components/AuthProvider';
import { getStoredToken, isTokenExpired } from '@/lib/auth';

export default function MenteeApplicationDetailPage() {
  const router = useRouter();
  const { roles } = useAuth();
  const { id } = router.query;

  const canAccess = roles.includes('ADMIN') || roles.includes('MENTORSHIP_ADMIN');

  useEffect(() => {
    const storedToken = getStoredToken();
    if (!storedToken || isTokenExpired(storedToken)) {
      Router.replace('/login');
      return;
    }
    if (roles.length > 0 && !canAccess) {
      Router.replace('/admin');
    }
  }, [canAccess, roles]);

  if (!canAccess && roles.length > 0) return null;

  return (
    <AdminLayout>
      <Paper sx={{ p: 3 }}>
        <Typography variant="h5" gutterBottom>
          Mentee Application #{id}
        </Typography>
        <Typography color="text.secondary" sx={{ mb: 3 }}>
          Manage approve and reject actions from the applications list.
        </Typography>
        <Button variant="outlined" onClick={() => router.push('/admin/mentees')}>
          Back to Pending Applications
        </Button>
      </Paper>
    </AdminLayout>
  );
}
