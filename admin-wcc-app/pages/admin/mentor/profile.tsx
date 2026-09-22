import { useEffect } from 'react';
import { Alert, Box, CircularProgress } from '@mui/material';
import AdminLayout from '@/components/AdminLayout';
import EditMentorForm from '@/components/EditMentor/EditMentorForm';
import { useAuth } from '@/components/AuthProvider';
import { getStoredToken, isTokenExpired } from '@/lib/auth';
import Router from 'next/router';

interface MemberWithId {
  id: number;
  fullName?: string;
}

export default function MyMentorProfilePage() {
  const { member, roles } = useAuth();
  const mentorId = (member as MemberWithId | null)?.id;
  const canAccess = roles.includes('MENTOR');

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
      {!mentorId ? (
        <Box display="flex" justifyContent="center" mt={6}>
          {roles.length > 0 ? (
            <Alert severity="warning">
              No mentor profile linked to your account. Contact an admin for help.
            </Alert>
          ) : (
            <CircularProgress />
          )}
        </Box>
      ) : (
        <EditMentorForm mentorId={String(mentorId)} />
      )}
    </AdminLayout>
  );
}
