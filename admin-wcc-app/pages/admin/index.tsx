import {Paper, Typography} from '@mui/material';
import AdminLayout from '@/components/AdminLayout';
import {useAuth} from '@/components/AuthProvider';
import {useEffect} from 'react';
import {useRouter} from 'next/router';
import {getStoredToken, isTokenExpired} from '@/lib/auth';

export default function AdminDashboard() {
  const {roles} = useAuth();
  const router = useRouter();
  useEffect(() => {
    const token = getStoredToken();
    if (!token || isTokenExpired(token)) router.replace('/login');
  }, [router]);

  return (
      <AdminLayout>
        <Paper sx={{p: 3}}>
          <Typography variant="h5" gutterBottom>Dashboard</Typography>
          <Typography variant="body1" color="text.secondary">
            Welcome {roles.join(', ')} to Women Coding Community Member Area.
          </Typography>
        </Paper>
      </AdminLayout>
  );
}
