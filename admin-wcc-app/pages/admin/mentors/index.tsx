import { useEffect, useState } from 'react';
import { Alert, Box, Button, Paper, Typography } from '@mui/material';
import AdminLayout from '@/components/AdminLayout';
import MentorCard from '@/components/mentors/MentorCard';
import { getMentors } from '@/services/mentorService';
import { MentorItem } from '@/types/mentor';
import { getStoredToken, isTokenExpired } from '@/lib/auth';
import { useRouter } from 'next/router';

export default function MentorsPage() {
  const router = useRouter();
  const [items, setItems] = useState<MentorItem[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const token = getStoredToken();
    if (!token || isTokenExpired(token)) router.replace('/login');
    else {
      getMentors(token)
        .then(setItems)
        .catch((e: Error) => setError(e.message));
    }
  }, [router]);

  return (
    <AdminLayout>
      <Paper sx={{ p: 3 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h5">Mentors</Typography>
          <Button
            variant="contained"
            color="primary"
            onClick={() => router.push('/admin/mentors/create')}
          >
            Create New Mentor
          </Button>
        </Box>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <Box>
          {items.map((m) => (
            <MentorCard key={m.id} mentor={m} />
          ))}
          {items.length === 0 && !error && (
            <Typography color="text.secondary">No mentors found.</Typography>
          )}
        </Box>
      </Paper>
    </AdminLayout>
  );
}
