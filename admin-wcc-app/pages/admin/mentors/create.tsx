import { Paper, Typography, Breadcrumbs, Link as MuiLink } from '@mui/material';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import Link from 'next/link';
import AdminLayout from '@/components/AdminLayout';
import CreateMentorForm from '@/components/CreateMentor/CreateMentorForm';

export default function CreateMentorPage() {
  return (
    <AdminLayout>
      <Paper sx={{ p: 3 }}>
        <Breadcrumbs separator={<NavigateNextIcon fontSize="small" />} sx={{ mb: 2 }}>
          <Link href="/admin" passHref legacyBehavior>
            <MuiLink underline="hover" color="inherit">
              Admin
            </MuiLink>
          </Link>
          <Link href="/admin/mentors" passHref legacyBehavior>
            <MuiLink underline="hover" color="inherit">
              Mentors
            </MuiLink>
          </Link>
          <Typography color="text.primary">Create</Typography>
        </Breadcrumbs>
        <Typography variant="h5" sx={{ mb: 3 }}>
          Create New Mentor
        </Typography>
        <CreateMentorForm />
      </Paper>
    </AdminLayout>
  );
}
